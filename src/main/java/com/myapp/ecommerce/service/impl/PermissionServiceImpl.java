package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.PermissionRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.PermissionResponse;
import com.myapp.ecommerce.entity.Permission;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.PermissionMapper;
import com.myapp.ecommerce.repository.PermissionRepository;
import com.myapp.ecommerce.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        log.info("Create a permission with name: {}", request.getName());

        Permission newPermission = permissionMapper.toPermission(request);
        newPermission.setActive(true);

        return permissionMapper.toPermissionResponse(permissionRepository.save(newPermission));
    }

    @Override
    public PermissionResponse update(String id, PermissionRequest request) {
        log.info("Update a permission with id: {}", id);

        Permission entityDB = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        permissionMapper.updatePermission(entityDB, request);
        if(request.isActive()){
            entityDB.setActive(true);
        }
        return permissionMapper.toPermissionResponse(permissionRepository.save(entityDB));
    }

    @Override
    public void delete(String id) {
        log.info("Delete a permission with id: {}", id);

        Permission entityDB = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        entityDB.setActive(false);

        permissionRepository.delete(entityDB);
    }

    @Override
    public PermissionResponse getDetail(String id) {
        log.info("Get detail a permission with id: {}", id);

        Permission entityDB = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        return permissionMapper.toPermissionResponse(entityDB);
    }

    @Override
    public ApiPagination<PermissionResponse> getAll(Specification<Permission> spec, Pageable pageable) {
        log.info("Get all permissions");

        Page<Permission> page = permissionRepository.findAll(spec, pageable);

        List<PermissionResponse> permissionList = page.getContent().stream().
                map(permissionMapper::toPermissionResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(page.getTotalElements());
        mt.setPages(page.getTotalPages());

        return ApiPagination.<PermissionResponse>builder()
                .meta(mt)
                .result(permissionList)
                .build();
    }

    @Override
    public List<Permission> fetchPermissionsByIds(List<String> ids) {
        return permissionRepository.findAllById(ids);
    }
}
