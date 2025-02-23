package com.roamly.admin.users;

import com.roamly.admin.KeycloakAdminClient;
import com.roamly.admin.users.api.CreateUserRequest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final KeycloakAdminClient keycloakAdminClient;

    public UserController(KeycloakAdminClient keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequest request) {
        return keycloakAdminClient.createUser(request);
    }
}