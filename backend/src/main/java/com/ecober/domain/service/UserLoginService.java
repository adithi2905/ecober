package com.ecober.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
@Service
public class UserLoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> userLogin(@RequestBody User userDetails,HttpSession session) {
        Optional<User> userRepositoryResults = userRepository.findByUsername(userDetails.getUsername());

        if (userRepositoryResults.isPresent()) {
            User user = userRepositoryResults.get();

            if (passwordEncoder.matches(userDetails.getPassword(), user.getPassword())) {
                session.setAttribute("riderId", user.getId());
                return ResponseEntity.ok("Successfully logged in");
            } else {
                return ResponseEntity.badRequest().body("Invalid password");
            }
        }

        return ResponseEntity.badRequest().body("Invalid username");
    }
}
