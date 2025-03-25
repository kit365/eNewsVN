package com.fpt.enewsvn.service;

import com.fpt.enewsvn.dto.request.user.CreateUserRequest;
import com.fpt.enewsvn.entity.RoleEntity;
import com.fpt.enewsvn.entity.UserEntity;
import com.fpt.enewsvn.mapper.UserMapper;
import com.fpt.enewsvn.repository.RoleRepository;
import com.fpt.enewsvn.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    RoleRepository roleRepository;

    public boolean create(CreateUserRequest createUserRequest) {
        UserEntity userEntity = userMapper.toUserEntity(createUserRequest);
        userEntity.setRole(roleRepository.findById(createUserRequest.getRoleId()).get());
        userMapper.toUserResponseDTO(userRepository.save(userEntity));
        return true;
    }



}
