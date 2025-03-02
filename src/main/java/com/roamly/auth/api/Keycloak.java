package com.roamly.auth.api;

import com.roamly.users.api.request.CreateUserRequest;

public interface Keycloak {

    String createUser(CreateUserRequest request);
}
