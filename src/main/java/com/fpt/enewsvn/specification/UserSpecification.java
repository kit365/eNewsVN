package com.fpt.enewsvn.specification;


import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;


public class UserSpecification {
    public static Specification<UserEntity> hasRoleZero() {
        return (root, query, builder) ->
                builder.equal(root.get("role").get("id"), 0L);
    }

    public static Specification<UserEntity> filterByStatus(Status status) {
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }

    public static Specification<UserEntity> searchByKeyword(String keyword) {
        return (root, query, builder) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("firstName")), likePattern),
                    builder.like(builder.lower(root.get("lastName")), likePattern),
                    builder.like(root.get("phone"), likePattern)
            );
        };
    }
}
