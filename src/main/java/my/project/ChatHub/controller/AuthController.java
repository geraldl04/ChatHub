package my.project.ChatHub.controller;


import lombok.AllArgsConstructor;
import my.project.ChatHub.dtos.LoginRequest;
import my.project.ChatHub.dtos.RegisterRequest;
import my.project.ChatHub.entity.CustomUserDetails;
import my.project.ChatHub.entity.RefreshToken;
import my.project.ChatHub.entity.User;
import my.project.ChatHub.repository.RefreshTokenRepository;
import my.project.ChatHub.repository.UserRepository;
import my.project.ChatHub.security.JwtUtil;
import my.project.ChatHub.service.UserService;
import my.project.ChatHub.serviceImpl.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class AuthController {

    @Autowired
    private final UserService userService;


    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final JwtUtil jwtUtils;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private final RefreshTokenService refreshTokenService;



    @PostMapping("registerUser")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {

        User useri = userRepository.findByEmail(request.getEmail()) ;

        if(useri != null) {
            throw new IllegalArgumentException("Ky email ekziston.");
        }
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Perdoruesi u regjistrua me sukses");
    }


    //heren e pare ne ogin nuk do gjenerohet token , do filter thjesht do vazhdoje derisa kerkesa te vije ketu
    //kur useri te logohet the te gjenerohet token per cdo kerkese me pas do ekz add filter before
    @PostMapping("/loginUser")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {

        try {
            Map<String,String> tokens = userService.verify(request) ;
            String accessToken = tokens.get("accessToken");
            String refreshToken = tokens.get("refreshToken");

            //krijimi i http only cookie

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false) // set true in production (HTTPS)
                    .path("/") //per te gjithe domain-in
                    .maxAge(15 * 60) // 15 minutes
                    .sameSite("Strict")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok()
                    .header("Set-Cookie", accessCookie.toString())
                    .header("Set-Cookie", refreshCookie.toString())
                    .body("Login successful");
        }
        catch(BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Email ose password i pasakte"
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }


//        try {
//            Map<String, String> tokens = userService.verify(request);
//            return ResponseEntity.ok(tokens);
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Email ose password i pasaktë");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Gabim gjatë autentikimit");
//        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nuk ka refresh token");
        }

        return refreshTokenRepository.findByToken(refreshToken)
                .map(token -> {
                    if (refreshTokenService.isTokenExpired(token)) {
                        refreshTokenRepository.delete(token);
                        return ResponseEntity.badRequest().body("Refresh token expired. Please login again.");
                    }
                    String newAccessToken = jwtUtils.generateToken(token.getUser().getEmail());

                    ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(15 * 60)
                            .sameSite("Strict")
                            .build();

                    return ResponseEntity.ok()
                            .header("Set-Cookie", newAccessCookie.toString())
                            .body("Access token refreshed");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token"));
    }


    @PostMapping("/auth/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
//        String requestToken = payload.get("refreshToken");
//
//        if (requestToken == null || requestToken.isBlank()) {
//            return ResponseEntity.badRequest().body("Refresh token is required.");
//        }
//
//        return refreshTokenRepository.findByToken(requestToken)
//                .map(token -> {
//                    refreshTokenRepository.delete(token);
//                    return ResponseEntity.ok("Logged out successfully.");
//                })
//                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
        ResponseCookie clearAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", clearAccess.toString())
                .header("Set-Cookie", clearRefresh.toString())
                .body("Logged out.");
    }
}
