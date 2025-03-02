package com.roamly.auth.api.request;

import com.roamly.users.api.request.CreateUserRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakCreateUserRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private List<Map<String, Object>> credentials;

    public static KeycloakCreateUserRequest from(CreateUserRequest createUserRequest) {
        return new KeycloakCreateUserRequest(
                createUserRequest.username(),
                createUserRequest.email(),
                createUserRequest.firstName(),
                createUserRequest.lastName(),
                true,
                List.of(Map.of(
                        "type", "password",
                        "value", createUserRequest.password(),
                        "temporary", false
                ))
        );
    }
}