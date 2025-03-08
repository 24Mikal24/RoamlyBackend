package com.roamly.itineraries.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItineraryDetails {

    private final Long id;
    private final String title;
    private final String destination;
    private final String description;
}
