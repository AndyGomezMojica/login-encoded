package com.andygomez.register_login.flow.application;

import com.andygomez.register_login.flow.model.UserModel;
import com.andygomez.register_login.flow.model.repository.UserRepository;
import com.andygomez.register_login.flow.infrastructure.UserAdapter;
import com.andygomez.register_login.flow.web.model.UserInput;
import com.andygomez.register_login.flow.web.model.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserUseCase {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserAdapter adapter;

    @Autowired
    private TokenUseCase tokenUseCase;

    public UserResponse createUser(UserInput input) {
        try {
            if (!validateNullFields(input)) {
                throw new IllegalArgumentException("Input fields must not be empty");
            }

            UserModel userExist = repository.findByEmail(input.getEmail());

            if (!validateEmail(userExist)) {
                throw new IllegalArgumentException("Email already exists, register a new email");
            }

            String uuidPassword = UUID.randomUUID().toString();

            UserModel newUser = adapter.inputToModel(input, uuidPassword);

            repository.save(newUser);

            tokenUseCase.encrypt(newUser.toString());

            return adapter.inputToResponse(input, uuidPassword);


        } catch (Exception e) {
            log.error("Error creating user: ", e);
            throw e;
        }
    }

    private boolean validateEmail(UserModel user) {
        if (user != null) {
            log.info("Email {}, already exists, register a new email", user.getEmail());
            return false;
        }
        return true;
    }

    private boolean validateNullFields(UserInput input) {
        if (input.getName() == null || input.getLastName() == null || input.getEmail() == null) {
            log.info("The fields must not be empty");
            return false;
        }
        return true;
    }
}
