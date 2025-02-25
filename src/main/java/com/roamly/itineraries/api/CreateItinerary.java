package com.roamly.itineraries.api;

import com.roamly.itineraries.api.request.CreateItineraryRequest;
import com.roamly.itineraries.api.response.ItineraryDetails;

public interface CreateItinerary {
    ItineraryDetails createItinerary(CreateItineraryRequest request, String userId);
}
