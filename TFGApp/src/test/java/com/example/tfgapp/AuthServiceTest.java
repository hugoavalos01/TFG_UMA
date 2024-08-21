package com.example.tfgapp;

import com.example.tfgapp.Auth.AuthResponse;
import com.example.tfgapp.Auth.AuthService;
import com.example.tfgapp.Auth.LoginRequest;
import com.example.tfgapp.Auth.RegisterRequest;
import com.example.tfgapp.Entity.User;
import com.example.tfgapp.Jwt.JwtService;
import com.example.tfgapp.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreCorrect() {
        LoginRequest request = new LoginRequest("testUser", "testPassword");
        User user = User.builder().username("testUser").password("encodedPassword").build();
        String expectedToken = "jwtToken";

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.getToken(user)).thenReturn(expectedToken);

        AuthResponse response = authService.login(request);

        assertEquals(expectedToken, response.getToken());
        assertEquals(request.getUsername(), response.getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }


    @Test
    void register_ShouldReturnAuthResponse_WhenUserIsSuccessfullyRegistered() {
        RegisterRequest request = new RegisterRequest("testUser", "testPassword");
        User user = User.builder()
                .username(request.getUsername())
                .password("encodedPassword")
                .build();
        String expectedToken = "jwtToken";

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.getToken(user)).thenReturn(expectedToken);

        AuthResponse response = authService.register(request);

        assertEquals(expectedToken, response.getToken());
        assertEquals(request.getUsername(), response.getUsername());

        verify(userRepository).save(any(User.class));
    }
}