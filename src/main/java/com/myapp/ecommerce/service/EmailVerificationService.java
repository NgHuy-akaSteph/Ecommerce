package com.myapp.ecommerce.service;

import com.myapp.ecommerce.entity.EmailVerification;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.entity.enums.VerificationType;

public interface EmailVerificationService {

    /**
     * Tạo một token xác thực mới cho user, đánh dấu tất cả các token PENDING cũ
     * cùng loại thành EXPIRED (mỗi user chỉ có 1 token active tại 1 thời điểm).
     *
     * @return token string đã tạo (chưa gửi email)
     */
    EmailVerification createToken(User user, String email, VerificationType type);

    /**
     * Verify token, đánh dấu status = VERIFIED. Trả về EmailVerification nếu hợp lệ.
     */
    EmailVerification verify(String token, VerificationType type);

    /**
     * Đánh dấu tất cả token PENDING của user theo type thành EXPIRED.
     */
    void expirePendingFor(User user, VerificationType type);
}
