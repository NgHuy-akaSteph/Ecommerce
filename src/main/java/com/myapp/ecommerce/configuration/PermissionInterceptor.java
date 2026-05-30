package com.myapp.ecommerce.configuration;

import com.myapp.ecommerce.entity.Permission;
import com.myapp.ecommerce.entity.Role;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.service.UserService;
import com.myapp.ecommerce.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@Component
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public PermissionInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws AppException {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        log.debug("RUN preHandle");
        log.debug(">>> path: {}", path);
        log.debug(">>> httpMethod: {}", httpMethod);
        log.debug(">>> requestURI: {}", requestURI);

        //Check permission here
        String username = SecurityUtil.getCurrentUserLogin().orElse(null);
        if(username != null && !username.isEmpty()){
            User user = this.userService.getUserByUsername(username);
            if(user != null){
                Role role = user.getRole();
                if(role != null){
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllowed = permissions.stream().anyMatch(permission ->
                        permission.getApiPath().equals(path) && permission.getMethod().equals(httpMethod));
                    if(!isAllowed){
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                    }
                }
                else{
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }
            }
        }
        return true;
    }
}
