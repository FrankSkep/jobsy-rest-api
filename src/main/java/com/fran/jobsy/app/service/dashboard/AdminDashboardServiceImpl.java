package com.fran.jobsy.app.service.dashboard;

import com.fran.jobsy.app.dto.dashboard.*;
import com.fran.jobsy.app.enums.BookingStatus;
import com.fran.jobsy.app.enums.ProviderRequestStatus;
import com.fran.jobsy.app.enums.Role;
import com.fran.jobsy.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final OfferingRepository offeringRepository;
    private final CategoryRepository categoryRepository;
    private final ProviderRequestRepository providerRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardData() {
        AdminStats stats = buildStats();
        List<RecentUserDTO> recentUsers = userRepository.findRecentUsers(PageRequest.of(0, 5));
        List<UsersByDateDTO> usersByDate = buildUsersByDate();
        List<PendingProviderRequestDTO> pendingRequests = providerRequestRepository
                .findByStatusAsDTO(ProviderRequestStatus.PENDING, PageRequest.of(0, 10));

        return new AdminDashboardResponse(stats, recentUsers, usersByDate, pendingRequests);
    }

    private AdminStats buildStats() {
        long totalUsers = userRepository.count();
        long totalProviders = userRepository.countByRole(Role.PROVIDER);
        long totalClients = userRepository.countByRole(Role.USER);
        long totalAdmins = userRepository.countByRoleIn(List.of(Role.ADMIN, Role.SUPER_ADMIN));

        long totalOfferings = offeringRepository.count();
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long completedBookings = bookingRepository.countByStatus(BookingStatus.COMPLETED);

        long pendingProviderRequests = providerRequestRepository.countByStatus(ProviderRequestStatus.PENDING);
        long totalCategories = categoryRepository.count();

        return new AdminStats(
                totalUsers,
                totalProviders,
                totalClients,
                totalAdmins,
                totalOfferings,
                totalBookings,
                pendingBookings,
                completedBookings,
                pendingProviderRequests,
                totalCategories
        );
    }

    private List<UsersByDateDTO> buildUsersByDate() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(29);
        LocalDateTime startDateTime = startDate.atStartOfDay();

        // Get actual data from DB
        List<UsersByDateDTO> dbData = userRepository.countUsersByDateSince(startDateTime);

        // Create a map for quick lookup
        Map<LocalDate, UsersByDateDTO> dataMap = dbData.stream()
                .collect(Collectors.toMap(UsersByDateDTO::date, dto -> dto));

        // Build complete list for last 30 days (fill gaps with zeros)
        List<UsersByDateDTO> result = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i);
            UsersByDateDTO dto = dataMap.getOrDefault(date, new UsersByDateDTO(date, 0, 0, 0, 0));
            result.add(dto);
        }

        return result;
    }
}

