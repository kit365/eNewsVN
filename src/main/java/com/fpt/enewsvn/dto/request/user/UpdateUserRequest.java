package com.fpt.enewsvn.dto.request.user;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
public class UpdateUserRequest implements Serializable {

    @Size(min = 5, max = 20, message = "PASSWORD_INVALID")
    String password;
    Long role;
    String fullName;
    String email;
    String phone;
    MultipartFile avatar;
    String token;
    String address;
    String status;   // ACTIVE / INACTIVE
    String typeUser; // NORMAL / VIP
}

