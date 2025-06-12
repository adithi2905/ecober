package com.ecober.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecober.adapter.Dto.LoginRequestDTO;
import com.ecober.adapter.Dto.LoginResponseDTO;
import com.ecober.adapter.Dto.UserDTO;
import com.ecober.adapter.mapper.UserMapper;
import com.ecober.domain.model.User;
import com.ecober.domain.service.UserLoginService;
import com.ecober.domain.service.UserRegistrationService;
import com.ecober.infrastructure.repository.UserRepository;
import com.ecober.security.JwtService;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRegistrationService userRegService;

    @Autowired
    UserLoginService userLogin;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticateManager;

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDto)
    {
        if(userDto.getPassword()!=null)
        {
        User user=userMapper.toEntity(userDto);
        userRegService.createUser(user);
        return ResponseEntity.ok("User Registered Successfully");
        }
        else
        {
            throw new IllegalArgumentException("Password is null");
        }
    }

    @PostMapping("/auth/login")
public ResponseEntity<?> login(@RequestBody LoginRequestDTO login) {
    authenticateManager.authenticate(
        new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
    );
    User user = userRepository.findByUsername(login.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

      String token = jwtService.generateToken(user.getId(), user.getRole());
        return ResponseEntity.ok(new LoginResponseDTO(token));
}

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Please clear token on client");
    }


}
