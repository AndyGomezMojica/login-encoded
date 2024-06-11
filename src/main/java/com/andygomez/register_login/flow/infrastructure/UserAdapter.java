package com.andygomez.register_login.flow.infrastructure;

import com.andygomez.register_login.flow.application.TokenUseCase;
import com.andygomez.register_login.flow.model.UserModel;
import com.andygomez.register_login.flow.model.repository.UserRepository;
import com.andygomez.register_login.flow.web.model.UserInput;
import com.andygomez.register_login.flow.web.model.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@Slf4j
public class UserAdapter {

    @Autowired
    private TokenUseCase tokenUseCase;

    @Autowired
    private UserRepository repository;

    public UserModel inputToModel(UserInput input, String uuidPassword){
        return UserModel.builder()
                .name(input.getName())
                .lastName(input.getLastName())
                .email(input.getEmail())
                .password(tokenUseCase.encrypt(uuidPassword))
                .createdBy("ADMIN")
                .createdAt(new Date())
                .isActive(true)
                .build();
    }

    public UserResponse inputToResponse(UserInput input, String uuidPassword){
        return UserResponse.builder()
                .username(generateUserName(input.getName(), input.getLastName()))
                .password(uuidPassword)
                .build();

    }

    public String generateUserName(String name, String lastName){
        String actualName = name.substring(0,1);
        String cleanedLastName = lastName.replace(" ", "");
        String newUserName = actualName + cleanedLastName;
        return newUserName;
    }
}
