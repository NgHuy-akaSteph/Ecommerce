package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.service.EmailService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${resend.from-name}")
    private String fromName;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Async
    public void sendVerificationEmail(String to, String name, String token) {
        String verifyUrl = baseUrl + "/verify-email?token=" + token;
        Map<String, Object> variables = Map.of(
                "userName", name != null ? name : "Customer",
                "verifyUrl", verifyUrl
        );
        sendEmail(to, "Xác thực email - E-Commerce Shop", "email/verification", variables);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String name, String token) {
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        Map<String, Object> variables = Map.of(
                "userName", name != null ? name : "Customer",
                "resetUrl", resetUrl
        );
        sendEmail(to, "Đặt lại mật khẩu - E-Commerce Shop", "email/password-reset", variables);
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }

        String htmlContent;
        try {
            htmlContent = templateEngine.process(template, context);
        } catch (Exception e) {
            log.error("Failed to render template {}: {}", template, e.getMessage());
            return;
        }

        try {
            Resend resend = new Resend(apiKey);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email '{}' sent to: {}, id: {}", subject, to, response.getId());
        } catch (Exception e) {
            log.error("Failed to send email to {} (subject='{}'): {}", to, subject, e.getMessage());
        }
    }
}
