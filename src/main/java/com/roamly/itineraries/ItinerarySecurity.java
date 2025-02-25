package com.roamly.itineraries;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItinerarySecurity {

    private final ItineraryRepository itineraryRepository;

    public boolean hasAccessToItinerary(Long itineraryId) {
        String userId = getCurrentUserId();

        return itineraryRepository.findById(itineraryId)
                .map(itinerary -> itinerary.getCreatedBy().equals(userId))
                .orElse(false);
    }

    private String getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaim("sub");
    }
}