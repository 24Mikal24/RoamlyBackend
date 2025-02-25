package com.roamly.itineraries.api;

import com.roamly.itineraries.api.request.UpdateItineraryRequest;
import com.roamly.itineraries.api.response.ItineraryDetails;

public interface UpdateItinerary {

    ItineraryDetails updateItinerary(UpdateItineraryRequest request);
}
