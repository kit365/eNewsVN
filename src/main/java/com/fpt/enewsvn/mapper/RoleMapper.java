package com.fpt.enewsvn.mapper;


import com.fpt.enewsvn.dto.request.role.CreateRoleRequest;
import com.fpt.enewsvn.dto.request.role.UpdateRoleRequest;
import com.fpt.enewsvn.dto.response.RoleResponseDTO;
import com.fpt.enewsvn.entity.RoleEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")

public interface RoleMapper {

    RoleEntity toRoleEntity(CreateRoleRequest role);

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    RoleEntity toRole(CreateRoleRequest role);

    RoleResponseDTO toRoleResponseDTO(RoleEntity role);

    List<RoleResponseDTO> toRoleResponseDTO (List<RoleEntity> RoleEntities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(@MappingTarget RoleEntity user, UpdateRoleRequest userDTO);
}
