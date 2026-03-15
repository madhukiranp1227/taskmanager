package com.taskmanager.service;

import com.taskmanager.dto.DashboardStatsResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public DashboardStatsResponse getStats() {
        long totalTasks = taskRepository.count();
        long todoCount = taskRepository.countByStatus(Task.Status.TODO);
        long inProgressCount = taskRepository.countByStatus(Task.Status.IN_PROGRESS);
        long doneCount = taskRepository.countByStatus(Task.Status.DONE);
        long overdueCount = taskRepository.countOverdueTasks(LocalDate.now());
        long highPriority = taskRepository.countByPriority(Task.Priority.HIGH);
        long mediumPriority = taskRepository.countByPriority(Task.Priority.MEDIUM);
        long lowPriority = taskRepository.countByPriority(Task.Priority.LOW);

        double completionRate = totalTasks == 0 ? 0.0
                : Math.round((doneCount * 100.0 / totalTasks) * 10) / 10.0;

        List<User> users = userRepository.findAll();
        List<DashboardStatsResponse.UserTaskStat> tasksByUser = users.stream()
                .map(user -> DashboardStatsResponse.UserTaskStat.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .assignedCount(taskRepository.countByAssignedTo(user))
                        .completedCount(taskRepository.countByAssignedToAndStatus(user, Task.Status.DONE))
                        .build())
                .filter(stat -> stat.getAssignedCount() > 0)
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalTasks(totalTasks)
                .todoCount(todoCount)
                .inProgressCount(inProgressCount)
                .doneCount(doneCount)
                .overdueCount(overdueCount)
                .highPriorityCount(highPriority)
                .mediumPriorityCount(mediumPriority)
                .lowPriorityCount(lowPriority)
                .completionRate(completionRate)
                .tasksByUser(tasksByUser)
                .build();
    }
}
