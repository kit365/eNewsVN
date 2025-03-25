package com.fpt.enewsvn.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    String username;
    String password;
    String fullName;
    String email;
    String phone;
    String avatar;
    String address;
    Long roleId;
}