package com.myapp.ecommerce.service;

import com.myapp.ecommerce.entity.Role;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService") // Ghi de len UserDetailsService mac dinh cua Spring
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDetailsServiceCustom implements UserDetailsService {

    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.myapp.ecommerce.entity.User user = userService.getUserByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
//        Role role = user.getRole();
//        List<GrantedAuthority> authorities = role.getPermissions().stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.getApiPath() + "_" + permission.getMethod()))
//                .collect(Collectors.toList());
        return new User (user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
