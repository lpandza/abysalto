package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.AuthResponse;
import hr.abysalto.hiring.mid.request.LoginRequest;
import hr.abysalto.hiring.mid.request.RegisterRequest;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
