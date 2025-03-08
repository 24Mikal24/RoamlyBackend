package com.roamly.auth;

import com.roamly.common.exceptions.KeycloakClientException;
import com.roamly.users.api.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

@ExtendWith(MockitoExtension.class)
class KeycloakAdminClientTest {

    @Value("${keycloak.admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-itineraryId}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KeycloakAdminClient keycloakAdminClient;

    private String userId;

    private final String tokenRetrievalEndpoint = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    private final String userCreationEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users";
    private final String baseUserRetrievalEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=";
    private final CreateUserRequest createUserRequest = new CreateUserRequest("John", "Doe", "johndoe", "john@example.com", "password123");

    @BeforeEach
    void setUp() {
        keycloakAdminClient = new KeycloakAdminClient(restTemplate);
        userId = UUID.randomUUID().toString();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        givenKeycloakTokenRetrievalIsSuccessful();
        givenKeycloakUserCreationIsSuccessful();
        givenNewlyCreatedUsersIdCouldBeRetrieved();

        assertEquals(keycloakAdminClient.createUser(createUserRequest), userId);
    }

    @Test
    void shouldThrowExceptionWhenUserIdRetrievalFails() {
        givenKeycloakTokenRetrievalIsSuccessful();
        givenKeycloakUserCreationIsSuccessful();

        when(restTemplate.exchange(eq(baseUserRetrievalEndpoint + createUserRequest.username()), eq(GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(status(BAD_REQUEST).build());

        var exception = assertThrows(KeycloakClientException.class, () -> keycloakAdminClient.createUser(createUserRequest));
        assertEquals(exception.getMessage(), "KeycloakAdminClient: There was a problem finding user itineraryId");
    }

    @Test
    void shouldThrowExceptionWhenUserCreationFails() {
        givenKeycloakTokenRetrievalIsSuccessful();

        when(restTemplate.exchange(eq(userCreationEndpoint), eq(POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(status(BAD_REQUEST).build());

        var exception = assertThrows(KeycloakClientException.class, () -> keycloakAdminClient.createUser(createUserRequest));
        assertEquals(exception.getMessage(), "KeycloakAdminClient: There was a problem creating the user");
    }

    @Test
    void shouldThrowExceptionWhenTokenRetrievalFails() {
        when(restTemplate.exchange(eq(tokenRetrievalEndpoint), eq(POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(noContent().build());

        var exception = assertThrows(KeycloakClientException.class, () -> keycloakAdminClient.createUser(createUserRequest));
        assertEquals(exception.getMessage(), "KeycloakAdminClient: There was a problem retrieving admin access token");
    }

    private void givenKeycloakTokenRetrievalIsSuccessful() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock-token");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, OK);

        when(restTemplate.exchange(eq(tokenRetrievalEndpoint), eq(POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);
    }

    private void givenKeycloakUserCreationIsSuccessful() {
        when(restTemplate.exchange(eq(userCreationEndpoint), eq(POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(status(CREATED).build());
    }

    private void givenNewlyCreatedUsersIdCouldBeRetrieved() {
        var userIdResponse = ok(List.of(Map.of("id", userId)));

        when(restTemplate.exchange(eq(baseUserRetrievalEndpoint + createUserRequest.username()), eq(GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(userIdResponse);
    }
}