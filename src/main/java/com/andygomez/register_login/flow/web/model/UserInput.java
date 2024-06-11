package com.andygomez.register_login.flow.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInput {

    private String name;
    private String lastName;
    private String email;
}
