package com.myapp.ecommerce.service;

import java.util.Map;

public interface EmailService {

    void sendVerificationEmail(String to, String name, String token);

    void sendPasswordResetEmail(String to, String name, String token);

    /**
     * Send a templated email. Variables are passed to the Thymeleaf engine.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param template Thymeleaf template name (e.g. "email/verification" without extension)
     * @param variables map of variables exposed to the template
     */
    void sendEmail(String to, String subject, String template, Map<String, Object> variables);
}
