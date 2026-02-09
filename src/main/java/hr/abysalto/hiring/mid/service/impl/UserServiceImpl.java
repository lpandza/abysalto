package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.AuthResponse;
import hr.abysalto.hiring.mid.entity.User;
import hr.abysalto.hiring.mid.repository.UserRepository;
import hr.abysalto.hiring.mid.request.LoginRequest;
import hr.abysalto.hiring.mid.request.RegisterRequest;
import hr.abysalto.hiring.mid.service.JwtService;
import hr.abysalto.hiring.mid.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed - email already registered: {}", request.email());
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .role("ROLE_USER")
                        .enabled(true)
                        .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        log.info("Authentication successful for email: {}", request.email());

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }
}
