package com.roamly.itineraries.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateItineraryRequest (
       @Positive(message = "Itinerary id must be positive")
       @NotNull(message = "Itinerary id is required")
       Long id,

       @NotBlank(message = "Title is required")
       @Size(max = 100, message = "Title cannot exceed 100 characters")
       String title,

       @NotBlank(message = "Destination is required")
       @Size(max = 100, message = "Destination cannot exceed 100 characters")
       String destination,

       @Size(max = 255, message = "Description cannot exceed 255 characters")
       String description) { }
