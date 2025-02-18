package com.myapp.ecommerce.configuration;

import com.myapp.ecommerce.entity.Permission;
import com.myapp.ecommerce.entity.Role;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.repository.PermissionRepository;
import com.myapp.ecommerce.repository.RoleRepository;
import com.myapp.ecommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitializer implements CommandLineRunner {

    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START: INITIALIZING SAMPLE DATA <<<");
        long countPermissions = permissionRepository.count();
        long countRoles = roleRepository.count();
        long countUsers = userRepository.count();

        if(countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a product", "/api/v1/products", "POST", "PRODUCTS", true));
            arr.add(new Permission("Import product from excel", "/api/v1/products/excel/import", "POST", "PRODUCTS",
                    true));
            arr.add(new Permission("Update a product", "/api/v1/products/{id}", "PUT", "PRODUCTS", true));
            arr.add(new Permission("Delete a product", "/api/v1/products/{id}", "DELETE", "PRODUCTS", true));
            arr.add(new Permission("Get a product by id", "/api/v1/products/{id}", "GET", "PRODUCTS", true));
            arr.add(new Permission("Get products with pagination", "/api/v1/products", "GET", "PRODUCTS", true));

            arr.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES", true));
            arr.add(new Permission("Update a category", "/api/v1/categories/{id}", "PUT", "CATEGORIES", true));
            arr.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES", true));
            arr.add(new Permission("Get a category by id", "/api/v1/categories/{id}", "GET", "CATEGORIES", true));
            arr.add(new Permission("Get categories with pagination", "/api/v1/categories", "GET", "CATEGORIES", true));

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS", true));
            arr.add(new Permission("Update a permission", "/api/v1/permissions/{id}", "PUT", "PERMISSIONS", true));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS", true));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS", true));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS",
                    true));

            arr.add(new Permission("Create a tag", "/api/v1/tags", "POST", "TAGS", true));
            arr.add(new Permission("Update a tag", "/api/v1/tags/{id}", "PUT", "TAGS", true));
            arr.add(new Permission("Delete a tag", "/api/v1/tags/{id}", "DELETE", "TAGS", true));
            arr.add(new Permission("Get a tag by id", "/api/v1/tags/{id}", "GET", "TAGS", true));
            arr.add(new Permission("Get tags with pagination", "/api/v1/tags", "GET", "TAGS", true));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES", true));
            arr.add(new Permission("Update a role", "/api/v1/roles/{id}", "PUT", "ROLES", true));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES", true));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES", true));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES", true));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS", true));
            arr.add(new Permission("Import user", "/api/v1/users/excel/import", "POST", "USERS", true));
            arr.add(new Permission("Export user", "/api/v1/users/excel/export", "GET", "USERS", true));
            arr.add(new Permission("Update a user", "/api/v1/users/{id}", "PUT", "USERS", true));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS", true));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS", true));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS", true));

            arr.add(new Permission("Create a order", "/api/v1/orders", "POST", "ORDERS", true));
            arr.add(new Permission("Export order", "/api/v1/orders/excel/export", "GET", "ORDERS", true));
            arr.add(new Permission("Update a order", "/api/v1/orders/{id}", "PUT", "ORDERS", true));
            arr.add(new Permission("Delete a order", "/api/v1/orders/{id}", "DELETE", "ORDERS", true));
            arr.add(new Permission("Get a order by id", "/api/v1/orders/{id}", "GET", "ORDERS", true));
            arr.add(new Permission("Get orders with pagination", "/api/v1/orders", "GET", "ORDERS", true));

            arr.add(new Permission("Upload a file", "/api/v1/file/upload", "POST", "FILES", true));
            permissionRepository.saveAll(arr);
        }

        if(countRoles == 0) {

            List<Permission> allPermissions = permissionRepository.findAll();

            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("Admin has all permissions")
                    .active(true)
                    .permissions(allPermissions)
                    .build();

            roleRepository.save(adminRole);
        }

        if(countUsers == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .name("Admin")
                    .build();

            Role adminRole = roleRepository.findByName("ADMIN").
                    orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

            if(adminRole != null) {
                admin.setRole(adminRole);
            }

            userRepository.save(admin);
        }

        if(countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SAMPLE DATA ALREADY INITIALIZED <<<");
        }
        else {
            System.out.println(">>> END: SAMPLE DATA INITIALIZED <<<");
        }
    }
}
