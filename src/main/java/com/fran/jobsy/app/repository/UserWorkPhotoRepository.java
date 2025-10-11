package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.user.UserWorkPhotoDTO;
import com.fran.jobsy.app.entity.UserWorkPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserWorkPhotoRepository extends JpaRepository<UserWorkPhoto, Long> {

    @Query("SELECT new com.fran.jobsy.app.dto.user.UserWorkPhotoDTO(uwp.id, uwp.url, uwp.uploadedAt) " +
            "FROM UserWorkPhoto uwp WHERE uwp.user.id = :userId")
    List<UserWorkPhotoDTO> findWorkPhotosByUserId(@Param("userId") Long userId);
}
