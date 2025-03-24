package com.fpt.enewsvn.mapper;


import com.fpt.enewsvn.dto.request.blog_category.CreateBlogCategoryRequest;
import com.fpt.enewsvn.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogCategoryMapper {


    @Mapping(target = "image", ignore = true)
    BlogCategoryEntity toBlogCategory(CreateBlogCategoryRequest request);

    @Mapping(target = "blogID", ignore = true)
    @Mapping(target = "blogs", ignore = true)
    BlogCategoryResponse toBlogCategoryResponse(BlogCategoryEntity request);

    @Mapping(target = "blogID", ignore = true)
    @Mapping(target = "blogs", ignore = true)
    List<BlogCategoryResponse> toBlogCateroiesResponseDTO(List<BlogCategoryEntity> request);

    @Mapping(target = "blog", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "image",ignore = true)
    void updateBlogCategory(@MappingTarget BlogCategoryEntity blogCategoryEntity, UpdateBlogCategoryRequest productRequestDTO);
}
