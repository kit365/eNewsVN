package com.fpt.enewsvn.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "BlogCategory")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogCategoryEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", insertable = false, updatable = false)
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "Position")
    Integer position;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "image")
    List<String> image;

    @Column(name = "Slug")
    String slug;

    @JsonManagedReference
    @OneToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY, mappedBy = "blogCategory", orphanRemoval = false)
    List<BlogEntity> blog = new ArrayList<>();


}
