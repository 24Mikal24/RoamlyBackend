package com.roamly.itineraries.stops.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateItineraryStopRequest(
        @Positive(message = "Stop id must be positive")
        @NotNull(message = "Stop id is required")
        Long id,

        @Positive(message = "Itinerary id must be positive")
        @NotNull(message = "Itinerary id is required")
        Long itineraryId,

        @NotBlank(message = "Location is required")
        @Size(max = 255, message = "Location cannot exceed 255 characters")
        String location,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) { }
