package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    Optional<UserPhoto> findByUserId(Long userId);

    @Modifying
    @Query("delete from UserPhoto up where up.user.id = :userId")
    void deleteByUserId(Long userId);
}
