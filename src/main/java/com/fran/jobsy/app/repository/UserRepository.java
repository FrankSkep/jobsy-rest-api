package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.dashboard.RecentUserDTO;
import com.fran.jobsy.app.dto.dashboard.UsersByDateDTO;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Boolean existsByRole(Role role);

    @EntityGraph(attributePaths = {"workPhotos"})
    Optional<User> findById(Long id);

    boolean existsBySlug(String slug);

    Optional<User> findBySlug(String slug);

    // Dashboard statistics
    long countByRole(Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role IN :roles")
    long countByRoleIn(@Param("roles") List<Role> roles);

    @Query("""
            SELECT new com.fran.jobsy.app.dto.dashboard.RecentUserDTO(
                u.id, u.firstname, u.lastname, u.username, u.role,
                CASE WHEN u.photo IS NOT NULL THEN u.photo.url ELSE NULL END,
                u.createdAt
            )
            FROM User u
            ORDER BY u.createdAt DESC
            """)
    List<RecentUserDTO> findRecentUsers(Pageable pageable);

    @Query("""
            SELECT new com.fran.jobsy.app.dto.dashboard.UsersByDateDTO(
                CAST(u.createdAt AS LocalDate),
                COUNT(u),
                SUM(CASE WHEN u.role = 'USER' THEN 1 ELSE 0 END),
                SUM(CASE WHEN u.role = 'PROVIDER' THEN 1 ELSE 0 END),
                SUM(CASE WHEN u.role IN ('ADMIN', 'SUPER_ADMIN') THEN 1 ELSE 0 END)
            )
            FROM User u
            WHERE u.createdAt >= :startDate
            GROUP BY CAST(u.createdAt AS LocalDate)
            ORDER BY CAST(u.createdAt AS LocalDate) ASC
            """)
    List<UsersByDateDTO> countUsersByDateSince(@Param("startDate") LocalDateTime startDate);
}