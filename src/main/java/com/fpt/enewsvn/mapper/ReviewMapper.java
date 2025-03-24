package com.fpt.enewsvn.mapper;


import com.fpt.enewsvn.dto.request.review.ReviewCreateRequest;
import com.fpt.enewsvn.dto.request.review.ReviewUpdateRequest;
import com.fpt.enewsvn.dto.response.ReviewResponse;
import com.fpt.enewsvn.entity.ReviewEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "parent", ignore = true)
        //tự set product vào feedback, mapper không hiểu đc
    ReviewEntity toReviewEntity(ReviewCreateRequest reviewEntity);


    @Mapping(target = "replies", ignore = true)
        //tự set product vào entity, chỉ trả cho FE Id
    ReviewResponse toReviewResponse(ReviewEntity reviewEntity);


    //khi người dùng cập nhập bình luận -> thường user chỉ cần cập nhập lại comment, lượt sao, lượt like/dislike
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReviewEntity(ReviewUpdateRequest reviewUpdateRequest, @MappingTarget ReviewEntity reviewEntity);

}
