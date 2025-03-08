package com.roamly.itineraries.api;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateItineraryRequestTest {

    private Validator validator;
    private static final Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void failsValidationWhenIdIsNull() {
        var request = request().id(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Itinerary id is required");
    }

    @Test
    void failsValidationWhenIdIsNegative() {
        var request = request().id(-1L).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Itinerary id must be positive");
    }

    @Test
    void failsValidationWhenTitleIsBlank() {
        var request = request().title("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Title is required");
    }

    @Test
    void failsValidationWhenTitleIsNull() {
        var request = request().title(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Title is required");
    }

    @Test
    void failsValidationWhenTitleIsGreaterThan100Characters() {
        var request = request().title(faker.lorem().characters(101)).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Title cannot exceed 100 characters");
    }

    @Test
    void failsValidationWhenDestinationIsBlank() {
        var request = request().destination("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Destination is required");
    }

    @Test
    void failsValidationWhenDestinationIsNull() {
        var request = request().destination(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Destination is required");
    }

    @Test
    void failsValidationWhenDestinationIsGreaterThan100Characters() {
        var request = request().destination(faker.lorem().characters(101)).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Destination cannot exceed 100 characters");
    }

    @Test
    void failsValidationWhenDescriptionIsGreaterThan255Characters() {
        var request = request().description(faker.lorem().characters(256)).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Description cannot exceed 255 characters");
    }

    private UpdateItineraryRequest.UpdateItineraryRequestBuilder request() {
        return UpdateItineraryRequest.builder()
                .id(1L)
                .title("title")
                .destination("destination")
                .description("description");
    }
}
