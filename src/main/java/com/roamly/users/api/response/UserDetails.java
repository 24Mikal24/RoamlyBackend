package com.roamly.users.api.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDetails {

    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
}
