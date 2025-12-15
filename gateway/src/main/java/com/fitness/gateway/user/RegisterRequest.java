package com.fitness.gateway.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid format")
    private String email ;

    @NotBlank(message = "Pass required ")
    @Size(min = 6 , message = "Atleast 6 chars required")

    private String keyCloakId ;
    private String password ;
    private String firstname ;
    private String lastname ;
}
