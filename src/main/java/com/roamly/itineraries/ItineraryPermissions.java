package com.roamly.itineraries;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItineraryPermissions {

    private final Itineraries itineraries;

    public boolean userOwnsItinerary(Long itineraryId) {
        String userId = getCurrentUserId();

        return itineraries.findById(itineraryId)
                .map(itinerary -> itinerary.getCreatedBy().equals(userId))
                .orElse(false);
    }

    private String getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaim("sub");
    }
}