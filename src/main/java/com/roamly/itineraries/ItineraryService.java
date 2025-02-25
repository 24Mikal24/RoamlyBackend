package com.roamly.itineraries;

import com.roamly.auth.AuthenticatedUser;
import com.roamly.common.exceptions.ModelNotFoundException;
import com.roamly.itineraries.api.*;
import com.roamly.itineraries.api.request.CreateItineraryRequest;
import com.roamly.itineraries.api.request.UpdateItineraryRequest;
import com.roamly.itineraries.api.response.ItineraryDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;

@Service
@RequiredArgsConstructor(access = PACKAGE)
class ItineraryService implements CreateItinerary, FindItineraries, UpdateItinerary, DeleteItinerary {

    private final ItineraryRepository itinerariesRepository;

    @Override
    @Transactional
    public ItineraryDetails createItinerary(CreateItineraryRequest request, String userId) {
        return itinerariesRepository.save(Itinerary.createdFrom(request, userId)).toDetails();
    }

    @Override
    @Transactional
    public List<ItineraryDetails> findItineraries() {
        return itinerariesRepository.findByCreatedBy(AuthenticatedUser.getCurrentUserId())
                .stream()
                .map(Itinerary::toDetails)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("@itinerarySecurity.hasAccessToItinerary(#request.id)")
    public ItineraryDetails updateItinerary(UpdateItineraryRequest request) {
        Itinerary itinerary = itinerariesRepository.findById(request.id())
                .orElseThrow(() -> new ModelNotFoundException("Itinerary", request.id()));

        itinerary.setTitle(request.title());
        itinerary.setDestination(request.destination());
        itinerary.setDescription(request.description());

        return itinerariesRepository.save(itinerary).toDetails();
    }

    @Override
    @Transactional
    @PreAuthorize("@itinerarySecurity.hasAccessToItinerary(#id)")
    public void deleteItineraryById(Long id) {
        itinerariesRepository.findById(id).ifPresentOrElse(
                itinerariesRepository::delete,
                () -> { throw new ModelNotFoundException("Itinerary", id); }
        );
    }
}
