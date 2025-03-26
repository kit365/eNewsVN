package com.fpt.enewsvn.dto.request.user;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
public class UpdateUserRequest implements Serializable {
    Long userID;

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String password;
    Long roleId;
    String fullName;
    String email;
    String phone;
    String avatar;
    String token;
    String address;
    String status;   // ACTIVE / INACTIVE
    String typeUser; // NORMAL / VIP
}

