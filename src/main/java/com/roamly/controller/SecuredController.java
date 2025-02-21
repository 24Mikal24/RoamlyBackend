package com.roamly.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SecuredController {

    @GetMapping("/private")
    public Map<String, Object> getProtectedInfo(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "You have accessed a protected endpoint!",
                "user", jwt.getClaim("preferred_username"),
                "email", jwt.getClaim("email"),
                "roles", jwt.getClaim("realm_access")
        );
    }
}