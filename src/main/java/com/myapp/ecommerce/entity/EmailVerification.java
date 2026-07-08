package com.myapp.ecommerce.entity;

import com.myapp.ecommerce.entity.enums.VerificationStatus;
import com.myapp.ecommerce.entity.enums.VerificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "email_verifications")
public class EmailVerification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    String email;

    @Column(nullable = false, unique = true)
    String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    VerificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    VerificationStatus status;

    @Column(nullable = false)
    Instant expiresAt;

    Instant verifiedAt;
}
