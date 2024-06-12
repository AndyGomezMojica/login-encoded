package com.andygomez.register_login.flow.model.repository;

import com.andygomez.register_login.flow.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    UserModel findByEmail(String email);

    UserModel findByUserName(String userName);

}
