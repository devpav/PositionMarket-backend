package by.psu.security;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegistrationRequest {

    private String username;

    private String password;

    private String email;

    private String phone;

}
