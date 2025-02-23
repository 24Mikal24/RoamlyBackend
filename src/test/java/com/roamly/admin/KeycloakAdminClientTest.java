package com.roamly.admin;

import com.roamly.admin.users.api.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakAdminClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KeycloakAdminClient keycloakAdminClient;

    @BeforeEach
    void setUp() {
        keycloakAdminClient = new KeycloakAdminClient(
                "https://keycloak.example.com",
                "roamly",
                "admin-client",
                "admin-secret",
                restTemplate
        );
    }

    @Test
    void shouldReturnAdminAccessTokenWhenValidResponse() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token", "mock-token");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        String token = keycloakAdminClient.getAdminAccessToken();

        assertEquals("mock-token", token);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }

    @Test
    void shouldReturnNullWhenAdminAccessTokenFails() {
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(new HashMap<>(), HttpStatus.UNAUTHORIZED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        String token = keycloakAdminClient.getAdminAccessToken();

        assertNull(token);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("John", "Doe", "johndoe", "john@example.com", "password123");

        KeycloakAdminClient spyClient = spy(keycloakAdminClient);
        doReturn("mock-token").when(spyClient).getAdminAccessToken();

        ResponseEntity<String> responseEntity = new ResponseEntity<>("User created", HttpStatus.CREATED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        ResponseEntity<String> response = spyClient.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created", response.getBody());

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenRetrievalFails() {
        KeycloakAdminClient spyClient = spy(keycloakAdminClient);
        doReturn(null).when(spyClient).getAdminAccessToken();

        CreateUserRequest request = new CreateUserRequest("John", "Doe", "johndoe", "john@example.com", "password123");

        ResponseEntity<String> response = spyClient.createUser(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Failed to retrieve admin token", response.getBody());
    }
}