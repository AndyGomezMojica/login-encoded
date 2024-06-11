package com.andygomez.register_login.flow.web.model;

import com.andygomez.register_login.flow.application.UserUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserUseCase userUseCase;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserInput input){
        return ResponseEntity.ok(userUseCase.createUser(input));
    }
}
