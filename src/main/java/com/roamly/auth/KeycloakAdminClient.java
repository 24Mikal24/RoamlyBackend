package com.roamly.auth;

import com.roamly.common.exceptions.KeycloakClientUnreachableException;
import com.roamly.users.Users;
import com.roamly.users.api.CreateUserRequest;

import com.roamly.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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

    private final Users userRepository;

    private final RestTemplate restTemplate;

    public ResponseEntity<String> createUser(CreateUserRequest createUserRequest) {
        String token = getAdminAccessToken();

        var keycloakResponse = createUserInKeycloak(createUserRequest, token);

        if (keycloakResponse.getStatusCode() == CREATED) {
            String userId = getKeycloakUserId(createUserRequest.username(), token);

            User newUser = User.builder()
                    .id(UUID.fromString(userId))
                    .username(createUserRequest.username())
                    .email(createUserRequest.email())
                    .firstName(createUserRequest.firstName())
                    .lastName(createUserRequest.lastName())
                    .role("USER")
                    .createdAt(now())
                    .build();

            userRepository.save(newUser);
        }

        return keycloakResponse;
    }

    String getAdminAccessToken() {
        String tokenRetrievalEndpoint = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String tokenRetrievalRequest = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(tokenRetrievalRequest, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenRetrievalEndpoint, POST, request, new ParameterizedTypeReference<>() {});

        return (String) Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new KeycloakClientUnreachableException("Could not reach Keycloak client."))
                .get("access_token");
    }

    private ResponseEntity<String> createUserInKeycloak(CreateUserRequest createUserRequest, String token) {
        String userCreationEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        var userCreationRequest = keycloakUserCreationRequestFrom(createUserRequest, token);

        return restTemplate.exchange(userCreationEndpoint, POST, userCreationRequest, String.class);
    }


    private String getKeycloakUserId(String username, String token) {
        String keycloakBaseUserRetrievalEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(keycloakBaseUserRetrievalEndpoint + username, GET, entity, new ParameterizedTypeReference<>() {});

        return (String) requireNonNull(response.getBody()).getFirst().get("id");
    }

    private HttpEntity<Map<String, Object>> keycloakUserCreationRequestFrom(CreateUserRequest createUserRequest, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(APPLICATION_JSON);

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

        return new HttpEntity<>(userPayload, headers);
    }
}