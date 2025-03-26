package com.fpt.enewsvn.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "Role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    Long roleId;

    @Column(name = "Title")
    String title;

    @Column(name = "Description", columnDefinition = "MEDIUMTEXT")
    String description;

    @OneToMany(mappedBy = "role")
    @JsonManagedReference
    @JsonIgnoreProperties("users")
    private List<UserEntity> users = new ArrayList<>();



}
