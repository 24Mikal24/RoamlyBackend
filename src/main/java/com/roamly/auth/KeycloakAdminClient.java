package com.roamly.auth;

import com.roamly.auth.api.Keycloak;
import com.roamly.auth.api.KeycloakCreateUserRequest;
import com.roamly.common.exceptions.KeycloakClientException;
import com.roamly.users.api.CreateUserRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor(access = PACKAGE)
class KeycloakAdminClient implements Keycloak {

    @Value("${keycloak.admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    @Override
    public String createUser(CreateUserRequest createUserRequest) {
        var userCreationEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        var token = getAdminAccessToken();
        var request = new HttpEntity<>(KeycloakCreateUserRequest.from(createUserRequest), headers(token));
        var response = restTemplate.exchange(userCreationEndpoint, POST, request, String.class);

        if (response.getStatusCode().equals(CREATED)) {
            return getKeycloakUserId(createUserRequest.username(), token);
        }

        throw new KeycloakClientException("KeycloakAdminClient: There was a problem creating the user");
    }

    private String getKeycloakUserId(String username, String token) {
        var keycloakBaseUserRetrievalEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=";

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(keycloakBaseUserRetrievalEndpoint + username, GET, new HttpEntity<>(headers(token)), new ParameterizedTypeReference<>() {});

        return (String) Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new KeycloakClientException("KeycloakAdminClient: There was a problem finding user id"))
                .getFirst().get("id");
    }

    private String getAdminAccessToken() {
        var tokenRetrievalEndpoint = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        var tokenRetrievalRequest = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(tokenRetrievalRequest, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenRetrievalEndpoint, POST, request, new ParameterizedTypeReference<>() {});

        return (String) Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new KeycloakClientException("KeycloakAdminClient: There was a problem retrieving admin access token"))
                .get("access_token");
    }

    private HttpHeaders headers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(APPLICATION_JSON);

        return headers;
    }
}