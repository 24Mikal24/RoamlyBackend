package com.roamly.itineraries.stops.api;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateItineraryStopRequestTest {

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

        assertThat(violations).extracting("message").contains("Stop id is required");
    }

    @Test
    void failsValidationWhenIdIsNegative() {
        var request = request().id(-1L).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Stop id must be positive");
    }

    @Test
    void failsValidationWhenItineraryIdIsNull() {
        var request = request().itineraryId(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Itinerary id is required");
    }

    @Test
    void failsValidationWhenItineraryIdIsNegative() {
        var request = request().itineraryId(-1L).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Itinerary id must be positive");
    }

    @Test
    void failsValidationWhenLocationIsBlank() {
        var request = request().location("").build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Location is required");
    }

    @Test
    void failsValidationWhenLocationIsNull() {
        var request = request().location(null).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Location is required");
    }

    @Test
    void failsValidationWhenLocationIsGreaterThan255Characters() {
        var request = request().location(faker.lorem().characters(256)).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Location cannot exceed 255 characters");
    }

    @Test
    void failsValidationWhenDescriptionIsGreaterThan255Characters() {
        var request = request().description(faker.lorem().characters(256)).build();

        var violations = validator.validate(request);

        assertThat(violations).extracting("message").contains("Description cannot exceed 255 characters");
    }

    private UpdateItineraryStopRequest.UpdateItineraryStopRequestBuilder request() {
        return UpdateItineraryStopRequest.builder()
                .id(1L)
                .itineraryId(1L)
                .location("location")
                .description("description");
    }
}
