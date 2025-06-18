package com.ecober.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.UserRepository;


@Service
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(User user)
    {
        Optional<User> result=userRepository.findByUsername(user.getUsername());
        if(result.isPresent())
        {
            
            throw new RuntimeException("User already exists with username: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("RIDER");
        userRepository.save(user);
        }
}
    

