package com.fpt.enewsvn.repository;


import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategoryEntity, Long> {
    Page<BlogCategoryEntity> findByTitleContainingIgnoreCaseAndDeleted(String keyword, boolean b, Pageable pageable);

    Page<BlogCategoryEntity> findByTitleContainingIgnoreCaseAndStatusAndDeleted(String keyword, Status statusEnum, Pageable pageable, boolean b);

    Page<BlogCategoryEntity> findAllByDeleted(boolean b, Pageable pageable);

    Page<BlogCategoryEntity> findAllByStatusAndDeleted(Status statusEnum, boolean b, Pageable pageable);


    List<BlogCategoryEntity> findTop5ByOrderByPosition();

    <T> ScopedValue<T> findBySlug(String slug);
}
