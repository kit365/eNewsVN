package com.fpt.enewsvn.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Review",
        indexes = {
                @Index(name = "idx_user_id", columnList = "UserID"),
                @Index(name = "idx_rating", columnList = "Rating"),
                @Index(name = "idx_created_at", columnList = "CreatedAt"),
                @Index(name = "idx_parent_id", columnList = "parent_id")
        })
public class ReviewEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    private Long reviewId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ProductID", nullable = false)
//    @JsonManagedReference
//    private ProductEntity product;



    @Column(name = "Rating", nullable = false)
    private int rating;

    @Column(name = "Comment", columnDefinition = "TEXT")
    private String comment;

    // Quản lý trả lời review
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ReviewEntity> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private ReviewEntity parent;
}
