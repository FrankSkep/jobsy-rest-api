package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.user.UserDTO;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT new com.fran.jobsy.app.dto.user.UserDTO(u.id, u.username, u.firstname, u.lastname, u.country, u.role) FROM User u")
    List<UserDTO> findAllAsUserDTO();

    Boolean existsByRole(Role role);
}