package my.project.ChatHub.controller;


import lombok.AllArgsConstructor;
import my.project.ChatHub.entity.Users;
import my.project.ChatHub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @PostMapping("registerUser")
    public Users createUser(@RequestBody Users user) {
         if(userRepository.findByEmail(user.getEmail()) != null){
             throw new IllegalArgumentException("Ky email ekziston.");
         }
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         return userRepository.save(user);
    }

    @PostMapping("/loginUser")
    public ResponseEntity<?> loginUser(@RequestBody Users loginRequest) {
        Users user = userRepository.findByEmail(loginRequest.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email nuk ekziston.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password i gabuar.");
        }

        return ResponseEntity.ok(user);
    }
}
