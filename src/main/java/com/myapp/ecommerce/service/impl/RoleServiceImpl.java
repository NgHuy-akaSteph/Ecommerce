package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.RoleRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.RoleResponse;
import com.myapp.ecommerce.entity.Permission;
import com.myapp.ecommerce.entity.Role;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.RoleMapper;
import com.myapp.ecommerce.repository.RoleRepository;
import com.myapp.ecommerce.service.PermissionService;
import com.myapp.ecommerce.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionService permissionService;

    @Override
    public RoleResponse create(RoleRequest request) {
        log.info("Creating role with name: {}", request.getName());

        Role role = roleMapper.toRole(request);
        if(request.getPerIds() != null){
            role.setPermissions(permissionService.fetchPermissionsByIds(request.getPerIds()));
        }
        role.setActive(true);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse update(String id, RoleRequest request) {
        log.info("Update role with id: {}", id);

        Role entityDB = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        if(request.getPerIds() != null){
            entityDB.setPermissions(permissionService.fetchPermissionsByIds(request.getPerIds()));
        }
        roleMapper.updateRole(entityDB, request);
        if(request.isActive()){
            entityDB.setActive(true);
        }
        return roleMapper.toRoleResponse(roleRepository.save(entityDB));
    }

    @Override
    public RoleResponse getDetails(String id) {
        log.info("Get role details with id: {}", id);

        Role entityDB = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        return roleMapper.toRoleResponse(entityDB);
    }

    @Override
    public ApiPagination<RoleResponse> getAll(Specification<Role> spec, Pageable pageable) {
        log.info("Get all roles");

        Page<Role> rolePage = roleRepository.findAll(spec, pageable);

        List<RoleResponse> roleList = rolePage.getContent().stream()
                .map(roleMapper::toRoleResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(rolePage.getTotalPages());
        mt.setTotal(rolePage.getTotalElements());

        return ApiPagination.<RoleResponse>builder()
                .meta(mt)
                .result(roleList)
                .build();
    }

    @Override
    public String delete(String id) {
        log.info("Delete role with id: {}", id);

        Role entityDB = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        entityDB.setActive(false);
        roleRepository.save(entityDB);
        return "Delete success";
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
    }

    @Override
    public Role findById(String id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
    }
}
