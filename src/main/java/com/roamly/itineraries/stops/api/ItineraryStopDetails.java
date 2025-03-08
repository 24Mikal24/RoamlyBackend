package com.roamly.itineraries.stops.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItineraryStopDetails {

    private final Long id;
    private final Long itineraryId;
    private final String location;
    private final String description;
}
