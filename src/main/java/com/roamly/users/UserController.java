package com.roamly.users;

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
class UserController {

    private final UserService userService;

    @PostMapping("/api/create-user")
    public ResponseEntity<UserDetails> createUser(@Valid @RequestBody CreateUserRequest request) {
        return status(CREATED).body(userService.handle(request));
    }
}