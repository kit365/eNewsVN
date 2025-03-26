package com.fpt.enewsvn.repository;

import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByUsername(String username);

    @Override
    Optional<UserEntity> findById(Long aLong);


    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.role WHERE " +
            "u.role IS NOT NULL AND " +
            "(:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR u.status = :status)")
    Page<UserEntity> findByKeywordAndStatusWithRole(String keyword, Status status, Pageable pageable);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.role WHERE " +
            "u.role IS NOT NULL AND " +
            "(:status IS NULL OR u.status = :status)")
    Page<UserEntity> findByStatusWithRole(Status status, Pageable pageable);
}