package com.fpt.enewsvn.service;

import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.dto.request.user.CreateUserRequest;
import com.fpt.enewsvn.dto.request.user.UpdateUserRequest;
import com.fpt.enewsvn.dto.response.UserResponseDTO;
import com.fpt.enewsvn.entity.RoleEntity;
import com.fpt.enewsvn.entity.UserEntity;
import com.fpt.enewsvn.exception.AppException;
import com.fpt.enewsvn.exception.ErrorCode;
import com.fpt.enewsvn.mapper.UserMapper;
import com.fpt.enewsvn.repository.RoleRepository;
import com.fpt.enewsvn.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    RoleRepository roleRepository;

    public boolean add(CreateUserRequest request) {
        UserEntity userEntity = userMapper.toUserEntity(request);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        } else {
            userEntity.setUsername(request.getUsername());
        }

        if (request.getEmail() == null) {
            userEntity.setEmail(request.getEmail());
        } else if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        } else if (request.getEmail() != null && !userRepository.existsByEmail(request.getEmail())) {
            userEntity.setEmail(request.getEmail());
        }

        if (request.getRoleId() != null) {
            RoleEntity role = roleRepository.findById(request.getRoleId())
                    .orElse(null);
            userEntity.setRole(role);
        } else {
            userEntity.setRole(null);
        }

        encodePassword(userEntity);

        userRepository.save(userEntity);
        return true;
    }

    private void encodePassword(UserEntity user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public Page<UserResponseDTO> getAll(int page, int size, String sortKey, String sortDirection, String keyword, String status) {
        try {
            // Validate and set default values
            sortKey = (sortKey == null || sortKey.isEmpty()) ? "userID" : sortKey;
            sortDirection = (sortDirection == null || sortDirection.isEmpty()) ? "asc" : sortDirection.toLowerCase();

            // Convert string status to Status enum
            Status statusEnum;
            try {
                statusEnum = (status == null || status.isEmpty())
                        ? Status.ACTIVE
                        : Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}. Using default ACTIVE status", status);
                statusEnum = Status.ACTIVE;
            }

            // Create sort object
            Sort sort;
            try {
                sort = "asc".equals(sortDirection)
                        ? Sort.by(sortKey).ascending()
                        : Sort.by(sortKey).descending();
            } catch (Exception e) {
                log.error("Invalid sort key: {}", sortKey);
                sort = Sort.by("userID").ascending();
            }

            // Create pageable
            Pageable pageable = PageRequest.of(page, size, sort);

            // Get user page based on filters (only users with roles)
            Page<UserEntity> userPage = keyword != null && !keyword.trim().isEmpty()
                    ? userRepository.findByKeywordAndStatusWithRole(keyword.trim(), statusEnum, pageable)
                    : userRepository.findByStatusWithRole(statusEnum, pageable);

            return userPage.map(user -> {
                UserResponseDTO dto = userMapper.toUserResponseDTO(user);
                if (dto == null) {
                    log.warn("Failed to map user entity to DTO: {}", user.getUserID());
                    return new UserResponseDTO();
                }
                return dto;
            });
        } catch (Exception e) {
            log.error("Error while getting users: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    public UserResponseDTO showDetail (Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (userEntity.getRole() != null) {
            return userMapper.toUserResponseDTO(userEntity);
        } else {
            throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
        }
    }
    public UserResponseDTO showDetailByRole(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (userEntity.getRole() != null) {
            return userMapper.toUserResponseDTO(userEntity);
        } else {
            throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
        }
    }

    public UserResponseDTO getUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (userEntity.getRole() != null) {
            return userMapper.toUserResponseDTO(userEntity);
        } else {
            throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
        }
    }

    public boolean deleteAccount(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getRole() != null) {
            userRepository.delete(user);
            return true;
        } else {
            throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
        }
    }

    public UserResponseDTO updateAccount(Long id, UpdateUserRequest request) {
        UserEntity userEntity = getUserEntityById(id);

        if (userEntity.getRole() != null) {

            // Cập nhật thông tin từ request (trừ password)
            userMapper.updateUser(userEntity, request);

            userEntity.setUsername(userEntity.getUsername());
            if (userRepository.existsByEmail(request.getEmail())) {
                log.info("Email exist");
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }

            if (userEntity.getRole() != null) {
                RoleEntity role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            }

            return userMapper.toUserResponseDTO(userRepository.save(userEntity));

        } else {
            throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
        }
    }

    public boolean saveUser(UpdateUserRequest request) {
        UserEntity userEntity = userRepository.findById(request.getUserID())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Only update and encode password if a new one is provided
        if (request.getPassword() != null) {
            userEntity.setPassword(request.getPassword());
            encodePassword(userEntity);
        }

        // Update other fields
        userMapper.updateUser(userEntity, request);

        // Validate phone number length
        if (request.getPhone() == null || request.getPhone().length() < 10) {
            throw new AppException(ErrorCode.INVALID_PHONE_NUMBER);
        }

        // Validate and format full name
        String fullName = request.getFullName().replaceAll("[^a-zA-Z\\s]", "").trim();
        if (fullName.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FULL_NAME);
        }

        // Check and set role
        if (request.getRoleId() != null) {
            RoleEntity role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            userEntity.setRole(role);
        } else {
            throw new AppException(ErrorCode.ROLE_IS_REQUIRED);
        }

        userRepository.save(userEntity);
        return true;
    }

    public boolean deleteSelectedAccount(List<Long> id) {
        List<UserEntity> userEntities = userRepository.findAllById(id);
        for (UserEntity userEntity : userEntities) {
            if (userEntity.getRole() != null) {
                userRepository.delete(userEntity);

            } else {
                throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
            }
        }
        return true;
    }

    public boolean login(String email, String password){
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity != null){
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            boolean result = passwordEncoder.matches(password, userEntity.getPassword());
            if(!result){
                throw new AppException(ErrorCode.PASSWORD_INCORRECT);
            }
            return true;
        }
        return false;
    }


}
