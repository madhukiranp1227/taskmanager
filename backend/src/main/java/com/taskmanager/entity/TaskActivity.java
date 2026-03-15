package com.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User performedBy;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private String description;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @PrePersist
    public void prePersist() {
        this.performedAt = LocalDateTime.now();
    }

    public enum ActivityType {
        CREATED,
        UPDATED,
        STATUS_CHANGED,
        ASSIGNED,
        COMMENTED,
        DELETED
    }
}
