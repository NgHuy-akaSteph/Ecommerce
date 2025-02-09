package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.PermissionRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.PermissionResponse;
import com.myapp.ecommerce.entity.Permission;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {

    PermissionResponse create (PermissionRequest request);

    PermissionResponse update (String id, PermissionRequest request);

    void delete (String id);

    PermissionResponse getDetail(String id);

    ApiPagination<PermissionResponse> getAll(Specification<Permission> spec, Pageable pageable);

    List<Permission> fetchPermissionsByIds(List<String> ids);
}
