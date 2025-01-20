package com.example.backend.global.auth.dto;

import static com.example.backend.global.validation.ValidationGroups.*;

import com.example.backend.global.validation.annotation.ValidUsername;

import lombok.Builder;

@Builder
public record AuthForm(@ValidUsername(groups = PatternGroup.class) String username, String password) {}
