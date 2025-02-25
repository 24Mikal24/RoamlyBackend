package com.roamly.itineraries.api;

import com.roamly.itineraries.api.response.ItineraryDetails;

import java.util.List;

public interface FindItineraries {

    List<ItineraryDetails> findItineraries();
}
