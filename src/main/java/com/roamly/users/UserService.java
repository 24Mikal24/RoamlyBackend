package com.roamly.users;

import com.roamly.auth.api.Keycloak;
import com.roamly.users.api.CreateUserRequest;
import com.roamly.users.api.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static lombok.AccessLevel.PACKAGE;


@Service
@RequiredArgsConstructor(access = PACKAGE)
class UserService {

    private final Keycloak keycloakAdminClient;
    private final Users userRepository;

    public UserDetails handle(CreateUserRequest request) {
            var userId = keycloakAdminClient.createUser(request);

            User newUser = User.builder()
                    .id(UUID.fromString(userId))
                    .username(request.username())
                    .email(request.email())
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .role("USER")
                    .createdAt(now())
                    .build();

            return userRepository.save(newUser).toDetails();
    }
}
