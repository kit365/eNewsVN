package com.fpt.enewsvn.mapper;


import com.fpt.enewsvn.dto.request.user.CreateUserRequest;
import com.fpt.enewsvn.dto.request.user.UpdateUserRequest;
import com.fpt.enewsvn.dto.response.UserResponseDTO;
import com.fpt.enewsvn.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "address", ignore = true)
    UserEntity toUserEntity(CreateUserRequest userRequestDTO);

    @Mapping(target = "role", source = "role")
    UserResponseDTO toUserResponseDTO(UserEntity userEntity);


    @Mapping(target = "role", ignore = true)
    List<UserResponseDTO> toUserResponseDTO(List<UserEntity> userEntities);


    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget UserEntity user,  UpdateUserRequest userDTO);

}
