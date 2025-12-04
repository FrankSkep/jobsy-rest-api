package com.fran.jobsy.app.dto.dashboard;

import java.util.List;

public record AdminDashboardResponse(
        AdminStats stats,
        List<RecentUserDTO> recentUsers,
        List<UsersByDateDTO> usersByDate,
        List<PendingProviderRequestDTO> pendingProviderRequests
) {
}

