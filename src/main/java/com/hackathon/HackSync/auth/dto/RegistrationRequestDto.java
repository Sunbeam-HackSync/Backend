package com.hackathon.HackSync.auth.dto;

import com.hackathon.HackSync.auth.entity.ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationRequestDto {

    @Email(message = "Invalid Email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private ROLE role;

}
