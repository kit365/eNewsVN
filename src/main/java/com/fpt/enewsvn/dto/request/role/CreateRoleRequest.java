package com.fpt.enewsvn.dto.request.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoleRequest {
    String title;
    String description;
    List<String> permission = new ArrayList<>();
}