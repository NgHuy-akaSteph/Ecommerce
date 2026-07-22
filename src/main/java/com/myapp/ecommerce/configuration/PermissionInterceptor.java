package com.myapp.ecommerce.configuration;

import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.service.UserService;
import com.myapp.ecommerce.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public PermissionInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws AppException {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();

        log.debug("RUN preHandle");
        log.debug(">>> path: {}", path);
        log.debug(">>> httpMethod: {}", httpMethod);

        String username = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (username != null && !username.isEmpty()) {
            try {
                if (!userService.hasPermission(username, path, httpMethod)) {
                    log.warn("Permission denied for user '{}' on {} {}", username, httpMethod, path);
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }
            } catch (AppException e) {
                if (e.getErrorCode() == ErrorCode.UNAUTHORIZED) {
                    throw e;
                }
                log.warn("Error during permission check for user '{}': {}", username, e.getMessage());
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
        return true;
    }
}
