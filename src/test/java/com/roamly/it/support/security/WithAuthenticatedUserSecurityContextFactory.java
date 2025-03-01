package com.roamly.it.support.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = PACKAGE)
class WithAuthenticatedUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthenticatedUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthenticatedUser withAuthenticatedUser) {
        assertThat(withAuthenticatedUser.username()).as("Authenticated user name").isNotBlank();
        assertThat(withAuthenticatedUser.role()).as("User role").isNotNull();

        SecurityContext sc = SecurityContextHolder.createEmptyContext();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", UUID.randomUUID().toString());

        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of(withAuthenticatedUser.role()));

        claims.put("realm_access", realmAccess);

        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claims(claimsMap -> claimsMap.putAll(claims)) // Properly applies claims
                .build();

        Map<String, Object> extractedRealmAccess = (Map<String, Object>) claims.get("realm_access");
        List<String> roleList = extractedRealmAccess != null ? (List<String>) extractedRealmAccess.get("roles") : List.of();

        List<GrantedAuthority> authorities = roleList.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);
        sc.setAuthentication(authentication);
        return sc;
    }
}