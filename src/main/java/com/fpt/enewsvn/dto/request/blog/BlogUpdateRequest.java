package com.fpt.enewsvn.dto.request.blog;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BlogUpdateRequest implements Serializable {
    String title;
    String content;
    String author;
    Integer position;
    boolean featured;
    String status;
    boolean deleted;
    Long categoryID;
}
