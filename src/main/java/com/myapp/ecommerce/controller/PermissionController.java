package com.myapp.ecommerce.controller;


import com.myapp.ecommerce.dto.request.PermissionRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiString;
import com.myapp.ecommerce.dto.response.PermissionResponse;
import com.myapp.ecommerce.entity.Permission;
import com.myapp.ecommerce.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    ResponseEntity<PermissionResponse> createPermission(@RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }

    @GetMapping
    ResponseEntity<ApiPagination<PermissionResponse>> getPermissions(@Filter Specification<Permission> spec,
                                                                     Pageable pageable) {
        return ResponseEntity.ok().body(permissionService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    ResponseEntity<PermissionResponse> getPermission(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(permissionService.getDetail(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiString> deletePermission(@PathVariable("id") String id) {
        permissionService.delete(id);
        return ResponseEntity.ok().body(ApiString.builder().message("success").build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<PermissionResponse> updatePermission(@PathVariable("id") String id,
                                                        @RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.ok().body(permissionService.update(id, request));
    }
}
