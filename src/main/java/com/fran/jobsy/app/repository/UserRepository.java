package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.user.UserResponse;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}