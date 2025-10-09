package my.project.ChatHub.serviceImpl;


import lombok.RequiredArgsConstructor;
import my.project.ChatHub.dtos.LoginRequest;
import my.project.ChatHub.dtos.RegisterRequest;
import my.project.ChatHub.entity.CustomUserDetails;
import my.project.ChatHub.entity.RefreshToken;
import my.project.ChatHub.repository.RefreshTokenRepository;
import my.project.ChatHub.repository.UserRepository;
import my.project.ChatHub.security.JwtUtil;
import my.project.ChatHub.service.UserService;
import my.project.ChatHub.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;


    public String register(RegisterRequest request) {


        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return "User-i u regjistrua me sukses";
    }

    @Override
    public Map<String, String> verify(LoginRequest user) {
        Map<String, String> response = new HashMap<>();

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            User userEntity = userDetails.getUserEntity();

            String accessToken = jwtUtil.generateToken(userEntity.getEmail());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userEntity.getId());

            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken.getToken());
            return response;

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Gabim gjatë procesit të login-it");
        }
    }


}