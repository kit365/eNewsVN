package com.fpt.enewsvn.dto.request.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddPermissionRequest implements Serializable {

    Long roleId;
    List<String> permission;
}
