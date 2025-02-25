package com.roamly.itineraries.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateItineraryRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Destination is required")
        String destination,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description) { }
