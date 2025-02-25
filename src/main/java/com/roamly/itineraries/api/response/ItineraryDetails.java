package com.roamly.itineraries.api.response;

import com.roamly.itineraries.stops.ItineraryStop;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ItineraryDetails {

    private final Long id;
    private final String title;
    private final String destination;
    private final String description;
    private final List<ItineraryStop> stops;
}
