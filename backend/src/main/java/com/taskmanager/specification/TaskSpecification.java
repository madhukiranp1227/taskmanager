package com.taskmanager.specification;

import com.taskmanager.entity.Task;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecification {

    public static Specification<Task> hasStatus(Task.Status status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(Task.Priority priority) {
        return (root, query, cb) ->
                priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> assignedToUser(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("assignedTo").get("id"), userId);
    }

    public static Specification<Task> createdByUser(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("createdBy").get("id"), userId);
    }

    public static Specification<Task> titleContains(String keyword) {
        return (root, query, cb) ->
                keyword == null ? null : cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Task> dueBefore(LocalDate date) {
        return (root, query, cb) ->
                date == null ? null : cb.lessThanOrEqualTo(root.get("dueDate"), date);
    }

    public static Specification<Task> isOverdue() {
        return (root, query, cb) -> cb.and(
                cb.lessThan(root.get("dueDate"), LocalDate.now()),
                cb.notEqual(root.get("status"), Task.Status.DONE)
        );
    }
}
