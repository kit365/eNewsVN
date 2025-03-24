package com.fpt.enewsvn.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {

    Long reviewId;

    Long productId;

    UserResponseDTO user;

    int rating;

    String comment;

    List<ReviewResponse> replies; // Danh sách phản hồi

    @JsonIgnore
    Date createdAt;

    ReviewResponse parent;

    @JsonProperty("createdAt") // Chỉ định tên JSON
    public String getFormattedCreatedAt() {
        if (createdAt == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm | yyyy-MM-dd");
        return formatter.format(createdAt);
    }


}
