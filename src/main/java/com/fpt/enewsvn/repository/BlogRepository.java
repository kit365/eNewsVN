package com.fpt.enewsvn.repository;

import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.entity.BlogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogEntity, Long> {
    Page<BlogEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    Page<BlogEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum,
            Pageable pageable, boolean b);

    Page<BlogEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<BlogEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);

    BlogEntity searchBySlug(String slug);

    long countByStatusAndDeleted(Status status, boolean b);

    List<BlogEntity> findByBlogCategory_Name(String name);

}
