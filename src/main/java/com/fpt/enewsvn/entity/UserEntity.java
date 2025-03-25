package com.fpt.enewsvn.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "User")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "reviews", "orders"})
public class UserEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", insertable = false, updatable = false)
    Long userID;

    @Column(name = "Username", updatable = false, unique = true, nullable = false)
    String username;

    @Column(name = "Password", nullable = false)
    String password;

    @Column(name = "FullName")
    String fullName;


    @Column(name = "Email", unique = true)
    String email;

    @Column(name = "PhoneNumber")
    String phone;

    @Column(name = "Avatar")
    String avatar;

    @Column(name = "Token", nullable = false, unique = true)
    private String token = UUID.randomUUID().toString();

    @Column(name = "Address")
    String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "roleId", nullable = true)
    RoleEntity role;


}
