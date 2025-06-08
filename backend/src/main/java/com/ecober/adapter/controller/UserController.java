package com.ecober.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecober.adapter.Dto.UserDTO;
import com.ecober.adapter.mapper.UserMapper;
import com.ecober.domain.model.User;
import com.ecober.domain.service.UserLoginService;
import com.ecober.domain.service.UserRegistrationService;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRegistrationService userRegService;

    @Autowired
    UserLoginService userLogin;

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

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO userDto,HttpSession session)
    {
        ResponseEntity<String>results=null;
        if(userDto.getPassword()!=null)
        {
        User user=userMapper.toEntity(userDto);
        session.setAttribute("riderId", user.getId());
        results = userLogin.userLogin(user, session);
        }
        return results;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }

}
