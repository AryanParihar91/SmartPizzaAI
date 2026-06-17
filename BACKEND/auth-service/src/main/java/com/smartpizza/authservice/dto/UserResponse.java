package com.smartpizza.authservice.dto;

import com.smartpizza.authservice.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private Role role;
}