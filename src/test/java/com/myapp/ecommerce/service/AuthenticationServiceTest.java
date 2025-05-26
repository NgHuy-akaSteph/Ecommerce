//package com.myapp.ecommerce.service;
//
//import com.myapp.ecommerce.dto.request.AuthenticationRequest;
//import com.myapp.ecommerce.dto.request.IntrospectRequest;
//import com.myapp.ecommerce.dto.response.AuthenticationResponse;
//import com.myapp.ecommerce.dto.response.IntrospectResponse;
//import com.myapp.ecommerce.entity.InvalidatedToken;
//import com.myapp.ecommerce.entity.User;
//import com.myapp.ecommerce.exception.AppException;
//import com.myapp.ecommerce.exception.ErrorCode;
//import com.myapp.ecommerce.repository.InvalidatedTokenRepository;
//import com.myapp.ecommerce.repository.UserRepository;
//import com.myapp.ecommerce.util.SecurityUtil;
//import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jwt.SignedJWT;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.text.ParseException;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthenticationServiceTest {
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private InvalidatedTokenRepository invalidatedTokenRepository;
//
//    @Mock
//    private RoleService roleService;
//
//    @Mock
//    private SecurityUtil securityUtil;
//
//    @InjectMocks
//    private AuthenticationService authenticationService;
//
//    private User user;
//    private AuthenticationRequest authenticationRequest;
//    private IntrospectRequest introspectRequest;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId("1");
//        user.setUsername("testuser");
//        user.setPassword("password");
//        user.setEmail("test@example.com");
//
//        authenticationRequest = new AuthenticationRequest();
//        authenticationRequest.setUsername("testuser");
//        authenticationRequest.setPassword("password");
//
//        introspectRequest = new IntrospectRequest();
//        introspectRequest.setToken("valid_token");
//    }
//
//    @Test
//    void authenticate_Success() throws JOSEException {
//        // Given
//        Authentication authentication = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//            .thenReturn(authentication);
//        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//        when(securityUtil.generateToken(any(User.class))).thenReturn("generated_token");
//
//        // When
//        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("generated_token", response.getToken());
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//    }
//
//    @Test
//    void authenticate_InvalidCredentials_ThrowsException() {
//        // Given
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//            .thenThrow(new AppException(ErrorCode.UNAUTHENTICATED));
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> authenticationService.authenticate(authenticationRequest));
//        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
//    }
//
//    @Test
//    void introspect_ValidToken_ReturnsValidResponse() throws JOSEException, ParseException {
//        // Given
//        when(securityUtil.verifyToken(anyString())).thenReturn(true);
//        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);
//
//        // When
//        IntrospectResponse response = authenticationService.introspect(introspectRequest);
//
//        // Then
//        assertNotNull(response);
//        assertTrue(response.isValid());
//    }
//
//    @Test
//    void introspect_InvalidToken_ReturnsInvalidResponse() throws JOSEException, ParseException {
//        // Given
//        when(securityUtil.verifyToken(anyString())).thenReturn(false);
//
//        // When
//        IntrospectResponse response = authenticationService.introspect(introspectRequest);
//
//        // Then
//        assertNotNull(response);
//        assertFalse(response.isValid());
//    }
//
//    @Test
//    void introspect_InvalidatedToken_ReturnsInvalidResponse() throws JOSEException, ParseException {
//        // Given
//        when(securityUtil.verifyToken(anyString())).thenReturn(true);
//        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(true);
//
//        // When
//        IntrospectResponse response = authenticationService.introspect(introspectRequest);
//
//        // Then
//        assertNotNull(response);
//        assertFalse(response.isValid());
//    }
//
//    @Test
//    void logout_Success() {
//        // Given
//        String token = "valid_token";
//        doNothing().when(invalidatedTokenRepository).save(any(InvalidatedToken.class));
//
//        // When
//        authenticationService.logout(token);
//
//        // Then
//        verify(invalidatedTokenRepository, times(1)).save(any(InvalidatedToken.class));
//    }
//}