package com.myapp.ecommerce.controller;


import com.myapp.ecommerce.dto.request.RoleRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiString;
import com.myapp.ecommerce.dto.response.RoleResponse;
import com.myapp.ecommerce.entity.Role;
import com.myapp.ecommerce.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    ResponseEntity<RoleResponse> createRole(@RequestBody @Valid RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @GetMapping
    ResponseEntity<ApiPagination<RoleResponse>> getRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok().body(roleService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    ResponseEntity<RoleResponse> getRole(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(roleService.getDetails(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiString> deleteRole(@PathVariable("id") String id) {
        roleService.delete(id);
        return ResponseEntity.ok().body(ApiString.builder().message("success").build());
    }

    @PutMapping("/{id}")
    ResponseEntity<RoleResponse> updateRole(@PathVariable("id") String id,
                                            @RequestBody @Valid RoleRequest request) {
        return ResponseEntity.ok().body(roleService.update(id, request));
    }
}
