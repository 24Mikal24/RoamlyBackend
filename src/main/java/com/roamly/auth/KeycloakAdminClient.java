package com.roamly.auth;

import com.roamly.users.Users;
import com.roamly.users.api.CreateUserRequest;

import com.roamly.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static lombok.AccessLevel.PACKAGE;

@Service
@RequiredArgsConstructor(access = PACKAGE)
public class KeycloakAdminClient {

    @Value("${keycloak.admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private RestTemplate restTemplate = new RestTemplate();
    private Users userRepository;

    KeycloakAdminClient(RestTemplate restTemplate, Users userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    String getAdminAccessToken() {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=client_credentials" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {});

        return (String) Objects.requireNonNull(response.getBody()).get("access_token");
    }

    public ResponseEntity<String> createUser(CreateUserRequest createUserRequest) {
        String token = getAdminAccessToken();
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve admin token");
        }

        String createUserUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", createUserRequest.username());
        userPayload.put("email", createUserRequest.email());
        userPayload.put("firstName", createUserRequest.firstName());
        userPayload.put("lastName", createUserRequest.lastName());
        userPayload.put("enabled", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", createUserRequest.password());
        credentials.put("temporary", false);

        userPayload.put("credentials", new Map[]{credentials});

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);

        ResponseEntity<String> response = restTemplate.exchange(createUserUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            String userId = getKeycloakUserId(createUserRequest.username(), token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve Keycloak user ID");
            }

            User newUser = User.builder()
                    .id(UUID.fromString(userId))
                    .username(createUserRequest.username())
                    .email(createUserRequest.email())
                    .firstName(createUserRequest.firstName())
                    .lastName(createUserRequest.lastName())
                    .role("USER")
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(newUser);
        }

        return response;
    }

    private String getKeycloakUserId(String username, String token) {
        String getUsersUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(getUsersUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && !response.getBody().isEmpty()) {
            return (String) response.getBody().getFirst().get("id");
        }

        return null;
    }
}