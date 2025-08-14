package com.nelumbo.parking.dto;

import com.nelumbo.parking.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
