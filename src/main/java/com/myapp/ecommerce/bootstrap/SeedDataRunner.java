package com.myapp.ecommerce.bootstrap;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.Role;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.repository.CartRepository;
import com.myapp.ecommerce.repository.PermissionRepository;
import com.myapp.ecommerce.repository.RoleRepository;
import com.myapp.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Seeds default Roles, Permissions, and demo users on application startup.
 *
 * Idempotent: safe to run on every boot. Uses ON CONFLICT DO NOTHING for permissions
 * (unique constraint on api_path + method), and find-or-create for roles/users.
 *
 * Disabled in "test" profile to avoid polluting unit/integration test data.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SeedDataRunner implements CommandLineRunner {

    public static final String ADMIN_ROLE_ID = "00000000-0000-0000-0000-000000000001";
    public static final String USER_ROLE_ID  = "00000000-0000-0000-0000-000000000002";
    public static final String ADMIN_USER_ID = "00000000-0000-0000-0000-000000000003";
    public static final String DEMO_USER_ID  = "00000000-0000-0000-0000-000000000004";

    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    UserRepository userRepository;
    CartRepository cartRepository;
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager entityManager;

    @Value("${app.seed.default-admin-username:admin}")
    String adminUsername;

    @Value("${app.seed.default-admin-password:12345678}")
    String adminPassword;

    @Value("${app.seed.default-user-username:user01}")
    String demoUsername;

    @Value("${app.seed.default-user-password:12345678}")
    String demoPassword;

    @Value("${app.seed.default-user-email:user01@example.com}")
    String demoEmail;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding default data...");

        seedPermissions();
        Role adminRole = seedRole(ADMIN_ROLE_ID, "ADMIN", "Admin has all permissions");
        Role userRole  = seedRole(USER_ROLE_ID,  "USER",  "Default user role");
        assignAllPermissionsToRole(adminRole);
        assignUserPermissions(userRole);
        seedUser(ADMIN_USER_ID, adminUsername, adminPassword, "Admin", adminRole, null);
        seedUser(DEMO_USER_ID, demoUsername, demoPassword, "Demo User", userRole, demoEmail);

        log.info("Seeding default data completed");
    }

    private void seedPermissions() {
        List<EndpointDef> defs = EndpointDef.all();
        for (EndpointDef def : defs) {
            entityManager.createNativeQuery("""
                    INSERT INTO permissions (id, name, api_path, method, module, active)
                    VALUES (gen_random_uuid(), ?1, ?2, ?3, ?4, true)
                    ON CONFLICT (api_path, method) DO NOTHING
                    """)
                    .setParameter(1, def.name())
                    .setParameter(2, def.apiPath())
                    .setParameter(3, def.method())
                    .setParameter(4, def.module())
                    .executeUpdate();
        }
        log.info("Seeded {} permission definitions", defs.size());
    }

    private Role seedRole(String id, String name, String description) {
        Optional<Role> existing = roleRepository.findById(id);
        if (existing.isPresent()) {
            return existing.get();
        }
        // Insert with explicit deterministic ID to keep FK references stable
        entityManager.createNativeQuery("""
                INSERT INTO roles (id, name, description, active)
                VALUES (?1, ?2, ?3, true)
                """)
                .setParameter(1, id)
                .setParameter(2, name)
                .setParameter(3, description)
                .executeUpdate();
        log.info("Created role: {}", name);
        return roleRepository.findById(id).orElseThrow();
    }

    private void assignAllPermissionsToRole(Role role) {
        long total = permissionRepository.count();
        long already = countPermissionsForRole(role.getId());
        if (already >= total) {
            return;
        }
        entityManager.createNativeQuery("""
                INSERT INTO permission_role (role_id, permission_id)
                SELECT ?1, id FROM permissions
                ON CONFLICT DO NOTHING
                """)
                .setParameter(1, role.getId())
                .executeUpdate();
        log.info("Assigned all {} permissions to role {}", total, role.getName());
    }

    private void assignUserPermissions(Role role) {
        // Allowlist mirrors original V2 seed: GET-only on catalog endpoints, full cart/order access
        Set<String> allowed = Set.of(
                "GET /products", "GET /products/{id}", "GET /products/category/{id}",
                "GET /categories", "GET /categories/{id}",
                "GET /tags", "GET /tags/{id}",
                "GET /users/my-info",
                "POST /orders", "GET /orders/{id}", "PUT /orders/{id}", "GET /orders/history",
                "GET /carts", "POST /carts/add", "POST /carts/change", "DELETE /carts/delete/{id}",
                "GET /addresses", "POST /addresses", "GET /addresses/{id}", "PUT /addresses/{id}",
                "DELETE /addresses/{id}", "PUT /addresses/{id}/default"
        );
        for (String key : allowed) {
            String[] parts = key.split(" ", 2);
            String method = parts[0];
            String path = parts[1];
            entityManager.createNativeQuery("""
                    INSERT INTO permission_role (role_id, permission_id)
                    SELECT ?1, id FROM permissions
                    WHERE method = ?2 AND api_path = ?3
                    ON CONFLICT DO NOTHING
                    """)
                    .setParameter(1, role.getId())
                    .setParameter(2, method)
                    .setParameter(3, path)
                    .executeUpdate();
        }
        log.info("Assigned USER-role allowlist to role {}", role.getName());
    }

    private long countPermissionsForRole(String roleId) {
        Number n = (Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM permission_role WHERE role_id = ?1")
                .setParameter(1, roleId)
                .getSingleResult();
        return n.longValue();
    }

    private void seedUser(String deterministicId, String username, String rawPassword,
                          String name, Role role, String email) {
        Optional<User> existing = userRepository.findByUsername(username);
        User user;
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            // Insert with explicit deterministic ID via native SQL (skip JPA UUID generation)
            int rows = entityManager.createNativeQuery("""
                    INSERT INTO users (id, username, password, name, email, role_id, email_verified)
                    VALUES (?1, ?2, ?3, ?4, ?5, ?6, false)
                    """)
                    .setParameter(1, deterministicId)
                    .setParameter(2, username)
                    .setParameter(3, passwordEncoder.encode(rawPassword))
                    .setParameter(4, name)
                    .setParameter(5, email)
                    .setParameter(6, role.getId())
                    .executeUpdate();
            if (rows == 0) {
                log.warn("Failed to insert user '{}'", username);
                return;
            }
            user = userRepository.findByUsername(username).orElseThrow();
            log.info("Seeded user '{}' with role {}", username, role.getName());
        }
        if (cartRepository.findByUser(user) == null) {
            Cart cart = Cart.builder().sum(0).user(user).build();
            cartRepository.save(cart);
        }
    }
}
