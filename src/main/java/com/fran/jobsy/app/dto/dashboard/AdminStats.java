package com.fran.jobsy.app.dto.dashboard;

public record AdminStats(
        long totalUsers,
        long totalProviders,
        long totalClients,
        long totalAdmins,
        long totalOfferings,
        long totalBookings,
        long pendingBookings,
        long completedBookings,
        long pendingProviderRequests,
        long totalCategories
) {
}

