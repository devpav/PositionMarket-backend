package by.psu.security.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JwtAuthenticationResponse implements Serializable {

    private String token;

}
