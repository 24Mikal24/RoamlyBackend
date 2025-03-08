package com.roamly.itineraries.stops;

import com.roamly.common.exceptions.ModelNotFoundException;
import com.roamly.itineraries.stops.api.CreateItineraryStopRequest;
import com.roamly.itineraries.stops.api.UpdateItineraryStopRequest;
import com.roamly.itineraries.stops.api.ItineraryStopDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;

@Service
@RequiredArgsConstructor(access = PACKAGE)
class ItineraryStopService {

    private final ItineraryStops itineraryStops;

    @Transactional
    @PreAuthorize("@itineraryPermissions.userOwnsItinerary(#request.itineraryId)")
    public ItineraryStopDetails handle(CreateItineraryStopRequest request) {
        return itineraryStops.save(ItineraryStop.createdFrom(request)).toDetails();
    }

    @PreAuthorize("@itineraryPermissions.userOwnsItinerary(#itineraryId)")
    public List<ItineraryStopDetails> findItineraryStopsByItineraryId(Long itineraryId) {
        return itineraryStops.findByItineraryId(itineraryId)
                .stream()
                .map(ItineraryStop::toDetails)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@itineraryPermissions.userOwnsItinerary(#request.itineraryId)")
    public ItineraryStopDetails handle(UpdateItineraryStopRequest request) {
        ItineraryStop itineraryStop = itineraryStops.findById(request.id())
                .orElseThrow(() -> new ModelNotFoundException("Itinerary Stop", request.id()));

        itineraryStop.setLocation(request.location());
        itineraryStop.setDescription(request.description());

        return itineraryStops.save(itineraryStop).toDetails();
    }

    @Transactional
    @PreAuthorize("@itineraryPermissions.userOwnsItinerary(#itineraryId)")
    public void deleteByItineraryIdAndStopId(Long itineraryId, Long stopId) {
        itineraryStops.findById(stopId).ifPresentOrElse(
                itineraryStops::delete,
                () -> { throw new ModelNotFoundException("Itinerary Stop", stopId); }
        );
    }
}
