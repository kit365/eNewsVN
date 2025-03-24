package com.fpt.enewsvn.dto.request.blog_category;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlogCategoryRequest implements Serializable {
    String title;
    String description;
    Integer position;
    boolean featured;
    List<MultipartFile> image;
    String status;
    boolean deleted;
}
