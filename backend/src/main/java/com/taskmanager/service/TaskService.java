package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskActivity;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.TaskActivityRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskActivityRepository activityRepository;

    // ── Helpers ────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public TaskResponse toResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setStatus(task.getStatus());
        res.setPriority(task.getPriority());
        res.setDueDate(task.getDueDate());
        res.setCreatedAt(task.getCreatedAt());
        res.setUpdatedAt(task.getUpdatedAt());
        if (task.getAssignedTo() != null) {
            res.setAssignedToId(task.getAssignedTo().getId());
            res.setAssignedToName(task.getAssignedTo().getName());
        }
        if (task.getCreatedBy() != null) {
            res.setCreatedById(task.getCreatedBy().getId());
            res.setCreatedByName(task.getCreatedBy().getName());
        }
        return res;
    }

    private void logActivity(Task task, User user, TaskActivity.ActivityType type, String description) {
        activityRepository.save(TaskActivity.builder()
                .task(task)
                .performedBy(user)
                .activityType(type)
                .description(description)
                .build());
    }

    // ── Queries ────────────────────────────────────────────────────────

    /**
     * Advanced filtered & paginated search using JPA Specification.
     * Supports: status, priority, assignedToId, keyword, overdue filter
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(Task.Status status, Task.Priority priority,
                                           Long assignedToId, String keyword,
                                           Boolean overdueOnly, Pageable pageable) {
        Specification<Task> spec = Specification.where(null);

        if (status != null)       spec = spec.and(TaskSpecification.hasStatus(status));
        if (priority != null)     spec = spec.and(TaskSpecification.hasPriority(priority));
        if (assignedToId != null) spec = spec.and(TaskSpecification.assignedToUser(assignedToId));
        if (keyword != null)      spec = spec.and(TaskSpecification.titleContains(keyword));
        if (Boolean.TRUE.equals(overdueOnly)) spec = spec.and(TaskSpecification.isOverdue());

        return taskRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks() {
        User current = getCurrentUser();
        return taskRepository.findByAssignedTo(current).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        return toResponse(taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id)));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        return taskRepository.findAll(TaskSpecification.isOverdue())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Commands ────────────────────────────────────────────────────────

    public TaskResponse createTask(TaskRequest request) {
        User creator = getCurrentUser();

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssignedToId()));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.Status.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : Task.Priority.MEDIUM)
                .dueDate(request.getDueDate())
                .assignedTo(assignedTo)
                .createdBy(creator)
                .build();

        task = taskRepository.save(task);
        logActivity(task, creator, TaskActivity.ActivityType.CREATED,
                "Task '" + task.getTitle() + "' was created");

        return toResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        User currentUser = getCurrentUser();

        // Only creator or admin can edit
        if (!task.getCreatedBy().getId().equals(currentUser.getId()) && !isAdmin()) {
            throw new UnauthorizedException("You are not allowed to edit this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssignedToId()));
            task.setAssignedTo(assignedTo);
            logActivity(task, currentUser, TaskActivity.ActivityType.ASSIGNED,
                    "Task assigned to " + assignedTo.getName());
        }

        task = taskRepository.save(task);
        logActivity(task, currentUser, TaskActivity.ActivityType.UPDATED,
                "Task '" + task.getTitle() + "' was updated");

        return toResponse(task);
    }

    public TaskResponse updateStatus(Long id, Task.Status newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        User currentUser = getCurrentUser();
        Task.Status oldStatus = task.getStatus();
        task.setStatus(newStatus);
        task = taskRepository.save(task);

        logActivity(task, currentUser, TaskActivity.ActivityType.STATUS_CHANGED,
                "Status changed from " + oldStatus + " to " + newStatus);

        return toResponse(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        User currentUser = getCurrentUser();

        if (!task.getCreatedBy().getId().equals(currentUser.getId()) && !isAdmin()) {
            throw new UnauthorizedException("You are not allowed to delete this task");
        }

        logActivity(task, currentUser, TaskActivity.ActivityType.DELETED,
                "Task '" + task.getTitle() + "' was deleted");

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<TaskActivity> getTaskActivity(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }
        return activityRepository.findByTaskIdOrderByPerformedAtDesc(taskId);
    }
}
