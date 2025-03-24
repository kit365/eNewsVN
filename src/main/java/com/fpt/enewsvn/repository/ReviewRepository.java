package com.fpt.enewsvn.repository;


import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findAllByParentIsNull();


    Page<ReviewEntity> findAllByParentIsNullAndStatusAndDeleted(Pageable pageable, Status active, boolean b);


}
