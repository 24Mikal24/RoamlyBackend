package com.roamly.users.api;

import com.roamly.users.api.request.CreateUserRequest;
import com.roamly.users.api.response.UserDetails;

public interface CreateUser {

    UserDetails handle(CreateUserRequest request);
}
