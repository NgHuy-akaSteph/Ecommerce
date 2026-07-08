package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.EmailVerification;
import com.myapp.ecommerce.entity.enums.VerificationStatus;
import com.myapp.ecommerce.entity.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {

    Optional<EmailVerification> findByToken(String token);

    Optional<EmailVerification> findByTokenAndStatus(String token, VerificationStatus status);

    List<EmailVerification> findByUserIdAndTypeAndStatus(
            String userId, VerificationType type, VerificationStatus status);

    Optional<EmailVerification> findFirstByUserIdAndTypeAndStatusOrderByCreatedAtDesc(
            String userId, VerificationType type, VerificationStatus status);
}
