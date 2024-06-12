package com.andygomez.register_login.flow.application;

import com.andygomez.register_login.flow.model.MailModel;
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
    private MailUseCase mailUseCase;

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

            MailModel newEmail = adapter.emailUserResponseStructure(input.getEmail(), newUser.getUserName(), uuidPassword);

            mailUseCase.sendMail(newUser.getEmail(),newEmail);

            tokenUseCase.encrypt(newUser.toString());

            return adapter.inputToResponse(input, uuidPassword);


        } catch (Exception e) {
            log.error("Error creating user: ", e);
            throw e;
        }
    }

    public String loginWithCredentials(String userName, String password){
        try {

            UserModel existingUser = repository.findByUserName(userName);

            validateUserName(existingUser.getUserName());

            String existingPassword = decodePassword(existingUser.getUserName());

            if (!existingPassword.equals(password)){
                return "Password incorrect";
            }

            return "Login successfully";

        }catch (Exception e){
            log.error("Error to login: ", e);
            throw e;
        }
    }

    public void updateGenericPassword(String userName, String password){
        try{
            UserModel existingUser = repository.findByUserName(userName);

            validateUserName(existingUser.getUserName());

            adapter.updatePasswordByResponse(existingUser, password);
            repository.save(existingUser);
        }catch (Exception e){
            log.error("Error updating password: ", e);
        }
    }

    public String decodePassword(String userName){

        try {
            UserModel actualUser = repository.findByUserName(userName);

            validateUserName(actualUser.getUserName());

            return tokenUseCase.decrypt(actualUser.getPassword());
        }catch (Exception e){
            log.error("Error to decode a password: ", e);
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

    private boolean validateUserName(String userName){
        if (userName == null){
            log.info("User with that username does not exist");
            return false;
        }
        return true;
    }
}
