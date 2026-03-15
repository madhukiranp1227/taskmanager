package com.taskmanager.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalTasks;
    private long todoCount;
    private long inProgressCount;
    private long doneCount;
    private long overdueCount;
    private long highPriorityCount;
    private long mediumPriorityCount;
    private long lowPriorityCount;
    private double completionRate;
    private List<UserTaskStat> tasksByUser;

    @Data
    @Builder
    public static class UserTaskStat {
        private Long userId;
        private String userName;
        private long assignedCount;
        private long completedCount;
    }
}
