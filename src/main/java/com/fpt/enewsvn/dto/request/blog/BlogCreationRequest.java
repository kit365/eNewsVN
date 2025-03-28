package com.fpt.enewsvn.dto.request.blog;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BlogCreationRequest implements Serializable {
    String title;
    String content;
    String author;
    List<MultipartFile> thumbnail;
    Integer position;
    String status;
    Long categoryID;
}
