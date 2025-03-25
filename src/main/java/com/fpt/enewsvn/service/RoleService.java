package com.fpt.enewsvn.service;


import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.dto.request.role.AddPermissionRequest;
import com.fpt.enewsvn.dto.request.role.CreateRoleRequest;
import com.fpt.enewsvn.dto.request.role.UpdateRoleRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.dto.response.RoleResponseDTO;
import com.fpt.enewsvn.entity.RoleEntity;
import com.fpt.enewsvn.exception.AppException;
import com.fpt.enewsvn.exception.ErrorCode;
import com.fpt.enewsvn.mapper.RoleMapper;
import com.fpt.enewsvn.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public boolean add(CreateRoleRequest request) {
        if (roleRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        RoleEntity roleEntity = roleMapper.toRoleEntity(request);
        roleRepository.save(roleEntity);
        return true;
    }


    public RoleResponseDTO update(Long id, UpdateRoleRequest request) {
        RoleEntity roleEntity = getRoleEntityById(id);

        log.info("Tìm thấy role: {}", id);

        // Cập nhật thông tin từ request
        roleMapper.updateRole(roleEntity, request);
        log.info("Cập nhật role id: {}", id);
        return roleMapper.toRoleResponseDTO(roleRepository.save(roleEntity));
    }

    public String update(List<Long> id, String status) {
        return "";
    }

    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    public boolean delete(Long id) {
        RoleEntity roleEntity = getRoleEntityById(id);
        if (roleEntity == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        if (roleEntity != null) {
            log.info("Delete role id:{}", id);
            roleRepository.delete(roleEntity);
            return true;
        }
        return false;
    }

    public boolean delete(List<Long> longs) {
        List<RoleEntity> roleEntities = roleRepository.findAllById(longs);
        if (roleEntities.isEmpty()) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        roleRepository.deleteAll(roleEntities);
        return false;
    }

    public boolean deleteTemporarily(Long id) {
        return true;
    }

    public boolean restore(Long id) {
        return false;
    }

    public List<RoleResponseDTO> showAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponseDTO).collect(Collectors.toList());
    }

    public RoleResponseDTO showDetail(Long aLong) {
        return roleRepository.findById(aLong).map(roleMapper::toRoleResponseDTO).orElse(null);
    }


    public Page<RoleResponseDTO> getAll(int page, int size, String sortKey, String sortDirection, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortKey));
        Status roleStatus = Status.valueOf(status.toUpperCase());
        Page<RoleEntity> rolePage = roleRepository.findByTitleContainingAndStatus(keyword, roleStatus, pageable);
        return rolePage.map(roleMapper::toRoleResponseDTO);
    }

    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }


    public RoleEntity getRoleEntityById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }


}
