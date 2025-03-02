package com.roamly.users.api;

import com.roamly.users.api.request.CreateUserRequest;
import com.roamly.users.api.response.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor(access = PACKAGE)
@RequestMapping("/api/admin")
class UserController {

    private final CreateUser createUser;

    @PostMapping("/create-user")
    public ResponseEntity<UserDetails> createUser(@Valid @RequestBody CreateUserRequest request) {
        return status(CREATED).body(createUser.handle(request));
    }
}