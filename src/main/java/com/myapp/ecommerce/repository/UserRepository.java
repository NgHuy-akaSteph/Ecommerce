package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Page<User> findAll(Specification<User> spec, Pageable pageable);

    User findByRefreshTokenAndUsername(String username, String refreshToken);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    default Optional<User> findByUsernameOrEmail(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }
        return identifier.contains("@")
                ? findByEmail(identifier.trim())
                : findByUsername(identifier.trim());
    }

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.failedLoginAttempts = COALESCE(u.failedLoginAttempts, 0) + 1 WHERE u.username = :username")
    int incrementFailedAttempts(@Param("username") String username);

    // 2. Khóa tài khoản và reset bộ đếm về 0
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lockedUntil = :lockedUntil, u.failedLoginAttempts = 0 WHERE u.username = :username")
    int lockAccount(@Param("username") String username, @Param("lockedUntil") Instant lockedUntil);

    // 3. Reset trạng thái khi đăng nhập thành công
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.failedLoginAttempts = 0, u.lockedUntil = null WHERE u.username = :username")
    int resetFailedAttempts(@Param("username") String username);

    // 4. Lấy riêng số lần đăng nhập sai hiện tại một cách gọn nhẹ nhất
    @Query("SELECT u.failedLoginAttempts FROM User u WHERE u.username = :username")
    Integer findFailedLoginAttemptsByUsername(@Param("username") String username);
}
