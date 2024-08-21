package com.example.tfgapp;

import com.example.tfgapp.Auth.AuthResponse;
import com.example.tfgapp.Auth.AuthService;
import com.example.tfgapp.Auth.LoginRequest;
import com.example.tfgapp.Auth.RegisterRequest;
import com.example.tfgapp.Entity.User;
import com.example.tfgapp.Jwt.JwtService;
import com.example.tfgapp.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

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
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        String username = "testUser";
        String password = "password";
        LoginRequest request = new LoginRequest(username, password);

        UserDetails userDetails = org.mockito.Mockito.mock(UserDetails.class);

        User user = User.builder()
                .username(username)
                .password(password)
                .build();


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, password));
        when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));

        AuthResponse response = authService.login(request);

        assertEquals(username, response.getUsername());
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenUserIsRegistered() {
        String username = "newUser";
        String password = "newPassword";
        RegisterRequest request = new RegisterRequest(username, password);
        User user = User.builder().username(username).password("encodedPassword").build();
        String token = "jwtToken";

        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.getToken(any(UserDetails.class))).thenReturn(token);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals(token, response.getToken());
    }
}
