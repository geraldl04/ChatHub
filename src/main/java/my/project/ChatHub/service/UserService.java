package my.project.ChatHub.service;

import my.project.ChatHub.dtos.LoginRequest;
import my.project.ChatHub.dtos.RegisterRequest;
import my.project.ChatHub.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

    String register(RegisterRequest request) ;

    Map<String, String> verify(LoginRequest user);
}