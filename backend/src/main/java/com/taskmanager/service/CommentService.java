package com.taskmanager.service;

import com.taskmanager.dto.CommentRequest;
import com.taskmanager.dto.CommentResponse;
import com.taskmanager.entity.Comment;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskActivity;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.CommentRepository;
import com.taskmanager.repository.TaskActivityRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskActivityRepository activityRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
    }

    private CommentResponse toResponse(Comment comment) {
        CommentResponse res = new CommentResponse();
        res.setId(comment.getId());
        res.setContent(comment.getContent());
        res.setTaskId(comment.getTask().getId());
        res.setAuthorId(comment.getAuthor().getId());
        res.setAuthorName(comment.getAuthor().getName());
        res.setCreatedAt(comment.getCreatedAt());
        return res;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CommentResponse addComment(Long taskId, CommentRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
        User author = getCurrentUser();

        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .author(author)
                .build();

        comment = commentRepository.save(comment);

        // Log activity
        activityRepository.save(TaskActivity.builder()
                .task(task)
                .performedBy(author)
                .activityType(TaskActivity.ActivityType.COMMENTED)
                .description(author.getName() + " commented on the task")
                .build());

        return toResponse(comment);
    }

    public void deleteComment(Long taskId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        User currentUser = getCurrentUser();

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!comment.getAuthor().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }
}
