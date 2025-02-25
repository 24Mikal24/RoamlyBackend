package com.roamly.users.api;

import com.roamly.auth.KeycloakAdminClient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PACKAGE;

@RestController
@RequiredArgsConstructor(access = PACKAGE)
@RequestMapping("/api/admin")
class UserController {

    private final KeycloakAdminClient keycloakAdminClient;

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequest request) {
        return keycloakAdminClient.createUser(request);
    }
}