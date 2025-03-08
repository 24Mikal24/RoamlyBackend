package com.roamly.itineraries;

import com.roamly.auth.AuthenticatedUser;
import com.roamly.common.exceptions.ModelNotFoundException;
import com.roamly.itineraries.api.CreateItineraryRequest;
import com.roamly.itineraries.api.UpdateItineraryRequest;
import com.roamly.itineraries.api.ItineraryDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;

@Service
@RequiredArgsConstructor(access = PACKAGE)
class ItineraryService {

    private final Itineraries itineraries;

    @Transactional
    public ItineraryDetails handle(CreateItineraryRequest request, String userId) {
        return itineraries.save(Itinerary.createdFrom(request, userId)).toDetails();
    }

    public List<ItineraryDetails> findItineraries() {
        return itineraries.findByCreatedBy(AuthenticatedUser.getCurrentUserId())
                .stream()
                .map(Itinerary::toDetails)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@itineraryPermissions.userOwnsItinerary(#request.id)")
    public ItineraryDetails handle(UpdateItineraryRequest request) {
        Itinerary itinerary = itineraries.findById(request.id())
                .orElseThrow(() -> new ModelNotFoundException("Itinerary", request.id()));

        itinerary.setTitle(request.title());
        itinerary.setDestination(request.destination());
        itinerary.setDescription(request.description());

        return itineraries.save(itinerary).toDetails();
    }

    @Transactional
    @PreAuthorize("@itineraryPermissions.userOwnsItinerary(#id)")
    public void deleteItineraryById(Long id) {
        itineraries.findById(id).ifPresentOrElse(
                itineraries::delete,
                () -> { throw new ModelNotFoundException("Itinerary", id); }
        );
    }
}
