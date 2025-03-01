package com.roamly.it.support.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@WithSecurityContext(factory = WithAuthenticatedUserSecurityContextFactory.class)
public @interface WithAuthenticatedUser {

    String username() default "roamlyadmin";
    String role() default "USER";
}
