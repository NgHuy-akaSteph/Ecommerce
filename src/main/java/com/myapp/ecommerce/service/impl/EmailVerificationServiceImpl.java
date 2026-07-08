package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.entity.EmailVerification;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.entity.enums.VerificationStatus;
import com.myapp.ecommerce.entity.enums.VerificationType;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.repository.EmailVerificationRepository;
import com.myapp.ecommerce.service.EmailVerificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailVerificationServiceImpl implements EmailVerificationService {

    EmailVerificationRepository repository;

    @Value("${app.email.verify-token-expiry-minutes:15}")
    @NonFinal
    int verifyTokenExpiryMinutes;

    @Value("${app.email.password-reset-token-expiry-minutes:15}")
    @NonFinal
    int passwordResetExpiryMinutes;

    @Override
    @Transactional
    public EmailVerification createToken(User user, String email, VerificationType type) {
        // 1. Expire tất cả pending token cùng type của user để tránh duplicate
        expirePendingFor(user, type);

        // 2. Tính thời hạn theo type
        int expiryMinutes = (type == VerificationType.PASSWORD_RESET)
                ? passwordResetExpiryMinutes
                : verifyTokenExpiryMinutes;
        Instant expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES);

        // 3. Tạo token mới
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .email(email)
                .token(UUID.randomUUID().toString())
                .type(type)
                .status(VerificationStatus.PENDING)
                .expiresAt(expiresAt)
                .build();

        return repository.save(verification);
    }

    @Override
    @Transactional
    public EmailVerification verify(String token, VerificationType type) {
        EmailVerification verification = repository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        // Validate type khớp
        if (verification.getType() != type) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        // Đã xử lý rồi
        if (verification.getStatus() != VerificationStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        // Hết hạn
        if (verification.getExpiresAt() == null
                || Instant.now().isAfter(verification.getExpiresAt())) {
            verification.setStatus(VerificationStatus.EXPIRED);
            repository.save(verification);
            throw new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        verification.setStatus(type == VerificationType.PASSWORD_RESET
                ? VerificationStatus.USED
                : VerificationStatus.VERIFIED);
        verification.setVerifiedAt(Instant.now());

        return repository.save(verification);
    }

    @Override
    @Transactional
    public void expirePendingFor(User user, VerificationType type) {
        List<EmailVerification> pending = repository
                .findByUserIdAndTypeAndStatus(user.getId(), type, VerificationStatus.PENDING);
        if (pending.isEmpty()) return;

        Instant now = Instant.now();
        for (EmailVerification v : pending) {
            v.setStatus(VerificationStatus.EXPIRED);
            v.setExpiresAt(now);
        }
        repository.saveAll(pending);
    }
}
