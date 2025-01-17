package com.example.backend.global.auth.dto;

import static com.example.backend.global.validation.ValidationGroups.*;

import com.example.backend.global.validation.annotation.ValidPassword;
import com.example.backend.global.validation.annotation.ValidUsername;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthForm {

    @ValidUsername(groups = PatternGroup.class)
    private String username;

    @ValidPassword(groups = PatternGroup.class)
    private String password;
}
