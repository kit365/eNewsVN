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
    Long user;
    //    List<MultipartFile> thumbnail;
    Integer position;
    boolean featured;
    String status;
    boolean deleted;
    String author;
//    BlogCategoryResponse blogCategory;
    Long categoryID;
}
