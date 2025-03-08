package com.roamly.users.api;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    private Validator validator;
    private static final Faker faker = new Faker();


    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void failsValidationWhenUsernameIsBlank() {
        var request = request().username("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Username is required");
    }

    @Test
    void failsValidationWhenUsernameIsNull() {
        var request = request().username(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Username is required");
    }

    @Test
    void failsValidationWhenUsernameIsLessThan3Characters() {
        var request = request().username("12").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Username must be between 3 and 50 characters");
    }

    @Test
    void failsValidationWhenUsernameIsGreaterThan50Characters() {
        var request = request().username(faker.lorem().characters(51)).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Username must be between 3 and 50 characters");
    }

    @Test
    void failsValidationWhenEmailIsBlank() {
        var request = request().email("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Email is required");
    }

    @Test
    void failsValidationWhenEmailIsNull() {
        var request = request().email(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Email is required");
    }

    @Test
    void failsValidationWhenEmailIsNotInAValidEmailFormat() {
        var request = request().email("not an email").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Invalid email format");
    }

    @Test
    void failsValidationWhenPasswordIsBlank() {
        var request = request().password("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Password is required");
    }

    @Test
    void failsValidationWhenPasswordIsNull() {
        var request = request().password(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Password is required");
    }

    @Test
    void failsValidationWhenPasswordIsLessThan6Characters() {
        var request = request().password("12345").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Password must be at least 6 characters long");
    }

    @Test
    void failsValidationWhenFirstNameIsBlank() {
        var request = request().firstName("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("First name is required");
    }

    @Test
    void failsValidationWhenFirstNameIsNull() {
        var request = request().firstName(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("First name is required");
    }

    @Test
    void failsValidationWhenLastNameIsBlank() {
        var request = request().lastName("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Last name is required");
    }

    @Test
    void failsValidationWhenLastNameIsNull() {
        var request = request().lastName(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Last name is required");
    }


    private CreateUserRequest.CreateUserRequestBuilder request() {
        return CreateUserRequest.builder()
                .username("username")
                .password("password")
                .email("email@test")
                .firstName("firstName")
                .lastName("lastName");
    }
}
