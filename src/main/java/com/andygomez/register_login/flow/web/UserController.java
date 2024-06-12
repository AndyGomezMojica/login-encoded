package com.andygomez.register_login.flow.web;

import com.andygomez.register_login.flow.application.UserUseCase;
import com.andygomez.register_login.flow.web.model.UserInput;
import com.andygomez.register_login.flow.web.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserUseCase userUseCase;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserInput input){
        return ResponseEntity.ok(userUseCase.createUser(input));
    }

    @PutMapping("/updatePassword")
    public void updateGenericPassword(@RequestBody UserResponse response){
        userUseCase.updateGenericPassword(response.getUsername(), response.getPassword());
    }

    @PostMapping("/login")
    public String loginCredential(@RequestBody UserResponse response){
        return userUseCase.loginWithCredentials(response.getUsername(), response.getPassword());
    }
}
