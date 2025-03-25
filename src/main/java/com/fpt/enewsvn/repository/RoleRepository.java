package com.fpt.enewsvn.repository;

import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    boolean existsByTitle(String title);

    Optional<RoleEntity> findByTitle(String title);

    Page<RoleEntity> findByTitleContainingAndStatus(String title, Status status, Pageable pageable);

}
