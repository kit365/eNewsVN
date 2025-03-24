package com.fpt.enewsvn.dto.request.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoleRequest  {
    Long roleId;
    String title;
    String description;
    List<String> permission;
    String status;

}