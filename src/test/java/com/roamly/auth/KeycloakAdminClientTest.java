package com.roamly.auth;

import com.roamly.common.exceptions.KeycloakClientUnreachableException;
import com.roamly.users.Users;
import com.roamly.users.api.CreateUserRequest;
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

import static java.util.UUID.randomUUID;
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

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Users userRepository;

    @InjectMocks
    private KeycloakAdminClient keycloakAdminClient;

    private final String tokenRetrievalEndpoint = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";;
    private final CreateUserRequest createUserRequest = new CreateUserRequest("John", "Doe", "johndoe", "john@example.com", "password123");

    @BeforeEach
    void setUp() {
        keycloakAdminClient = new KeycloakAdminClient(userRepository, restTemplate);
    }

    @Test
    void shouldReturnAdminAccessTokenWhenValidResponse() {
        givenKeycloakTokenRetrievalIsSuccessful();
        var token = keycloakAdminClient.getAdminAccessToken();
        assertEquals("mock-token", token);
        verify(restTemplate, times(1)).exchange(anyString(), eq(POST), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        givenKeycloakTokenRetrievalIsSuccessful();

        String userCreationEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users";
        String baseUserRetrievalEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users?username=";


        when(restTemplate.exchange(eq(userCreationEndpoint), eq(POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(status(CREATED).body("User created"));

        var getUserResponse = ok(List.of(Map.of("id", randomUUID().toString())));

        when(restTemplate.exchange(eq(baseUserRetrievalEndpoint + createUserRequest.username()), eq(GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(getUserResponse);

        var response = keycloakAdminClient.createUser(createUserRequest);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals("User created", response.getBody());
    }

    @Test
    void shouldThrowExceptionWhenTokenRetrievalFails() {
        when(restTemplate.exchange(eq(tokenRetrievalEndpoint), eq(POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(noContent().build());

        assertThrows(KeycloakClientUnreachableException.class, () -> keycloakAdminClient.getAdminAccessToken());
    }

    private void givenKeycloakTokenRetrievalIsSuccessful() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock-token");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, OK);

        when(restTemplate.exchange(eq(tokenRetrievalEndpoint), eq(POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);
    }
}