package com.fpt.enewsvn.mapper;

import com.fpt.enewsvn.dto.request.blog.BlogCreationRequest;
import com.fpt.enewsvn.dto.request.blog.BlogUpdateRequest;
import com.fpt.enewsvn.dto.response.BlogResponse;
import com.fpt.enewsvn.entity.BlogEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "user", ignore = true)
    BlogEntity toBlogEntity(BlogCreationRequest request);

    @Mapping(target = "blogCategory", source = "blogCategory")
    BlogResponse toBlogResponse(BlogEntity request);

    List<BlogResponse> toBlogsResponseDTO(List<BlogEntity> request);

    @Mapping(target = "blogCategory", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBlogEntity(@MappingTarget BlogEntity entity, BlogUpdateRequest request);
}
