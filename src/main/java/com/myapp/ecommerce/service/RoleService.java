package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.RoleRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.RoleResponse;
import com.myapp.ecommerce.entity.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleService {

    RoleResponse create (RoleRequest request);

    RoleResponse update (String id, RoleRequest request);

    RoleResponse getDetails (String id);

    ApiPagination<RoleResponse> getAll(Specification<Role> spec, Pageable pageable);

    String delete (String id); // temporary delete

    Role findByName(String name);
    Role findById(String id);
}
