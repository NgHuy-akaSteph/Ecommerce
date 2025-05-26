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
public class DatabaseInitializer implements CommandLineRunner {

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
            arr.add(new Permission("Create a product", "/products", "POST", "PRODUCTS", true));
            arr.add(new Permission("Import product from excel", "/products/excel/import", "POST", "PRODUCTS", true));
            arr.add(new Permission("Update a product", "/products/{id}", "PUT", "PRODUCTS", true));
            arr.add(new Permission("Delete a product", "/products/{id}", "DELETE", "PRODUCTS", true));
            arr.add(new Permission("Get a product by id", "/products/{id}", "GET", "PRODUCTS", true));
            arr.add(new Permission("Get products with pagination", "/products", "GET", "PRODUCTS", true));
            arr.add(new Permission("Delete all product in list", "/products/deleteAll", "DELETE", "PRODUCTS", true));

            arr.add(new Permission("Create a category", "/categories", "POST", "CATEGORIES", true));
            arr.add(new Permission("Update a category", "/categories/{id}", "PUT", "CATEGORIES", true));
            arr.add(new Permission("Delete a category", "/categories/{id}", "DELETE", "CATEGORIES", true));
            arr.add(new Permission("Get a category by id", "/categories/{id}", "GET", "CATEGORIES", true));
            arr.add(new Permission("Get categories with pagination", "/categories", "GET", "CATEGORIES", true));

            arr.add(new Permission("Create a permission", "/permissions", "POST", "PERMISSIONS", true));
            arr.add(new Permission("Update a permission", "/permissions/{id}", "PUT", "PERMISSIONS", true));
            arr.add(new Permission("Delete a permission", "/permissions/{id}", "DELETE", "PERMISSIONS", true));
            arr.add(new Permission("Get a permission by id", "/permissions/{id}", "GET", "PERMISSIONS", true));
            arr.add(new Permission("Get permissions with pagination", "/permissions", "GET", "PERMISSIONS", true));

            arr.add(new Permission("Create a tag", "/tags", "POST", "TAGS", true));
            arr.add(new Permission("Update a tag", "/tags/{id}", "PUT", "TAGS", true));
            arr.add(new Permission("Delete a tag", "/tags/{id}", "DELETE", "TAGS", true));
            arr.add(new Permission("Get a tag by id", "/tags/{id}", "GET", "TAGS", true));
            arr.add(new Permission("Get tags with pagination", "/tags", "GET", "TAGS", true));

            arr.add(new Permission("Create a role", "/roles", "POST", "ROLES", true));
            arr.add(new Permission("Update a role", "/roles/{id}", "PUT", "ROLES", true));
            arr.add(new Permission("Delete a role", "/roles/{id}", "DELETE", "ROLES", true));
            arr.add(new Permission("Get a role by id", "/roles/{id}", "GET", "ROLES", true));
            arr.add(new Permission("Get roles with pagination", "/roles", "GET", "ROLES", true));

            arr.add(new Permission("Create a user", "/users", "POST", "USERS", true));
            arr.add(new Permission("Import user", "/users/excel/import", "POST", "USERS", true));
            arr.add(new Permission("Export user", "/users/excel/export", "GET", "USERS", true));
            arr.add(new Permission("Update a user", "/users/{id}", "PUT", "USERS", true));
            arr.add(new Permission("Delete a user", "/users/{id}", "DELETE", "USERS", true));
            arr.add(new Permission("Get a user by id", "/users/{id}", "GET", "USERS", true));
            arr.add(new Permission("Get users with pagination", "/users", "GET", "USERS", true));
            arr.add(new Permission("Get my information", "/users/my-info", "GET", "USERS", true));

            arr.add(new Permission("Create a order", "/orders", "POST", "ORDERS", true));
            arr.add(new Permission("Export order", "/orders/excel/export", "GET", "ORDERS", true));
            arr.add(new Permission("Update a order", "/orders/{id}", "PUT", "ORDERS", true));
            arr.add(new Permission("Delete a order", "/orders/{id}", "DELETE", "ORDERS", true));
            arr.add(new Permission("Get a order by id", "/orders/{id}", "GET", "ORDERS", true));
            arr.add(new Permission("Get orders with pagination", "/orders", "GET", "ORDERS", true));

            arr.add(new Permission("Create a order details", "/orderdetails", "POST", "ORDERDETAILS", true));
            arr.add(new Permission("Get order details with pagination", "/orderdetails", "GET", "ORDERDETAILS", true));
            arr.add(new Permission("Get order details by id", "/orderdetails/{id}", "GET", "ORDERDETAILS", true));
            arr.add(new Permission("Update a order details", "/orderdetails/{id}", "PUT", "ORDERDETAILS", true));
            arr.add(new Permission("Delete a order details", "/orderdetails/{id}", "DELETE", "ORDERDETAILS", true));

            arr.add(new Permission("Get cart by user", "/carts", "GET", "CARTS", true));
            arr.add(new Permission("Add product to cart", "/carts/add", "POST", "CARTS", true));
            arr.add(new Permission("Change product quantity", "/carts/change", "POST", "CARTS", true));
            arr.add(new Permission("Delete cart detail", "/carts/delete/{id}", "DELETE", "CARTS", true));

            arr.add(new Permission("Upload a file", "/file/upload", "POST", "FILES", true));
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
