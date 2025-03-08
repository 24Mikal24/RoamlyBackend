package com.roamly.itineraries.stops;

import com.roamly.itineraries.stops.api.CreateItineraryStopRequest;
import com.roamly.itineraries.stops.api.UpdateItineraryStopRequest;
import com.roamly.itineraries.stops.api.ItineraryStopDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor(access = PACKAGE)
@RequestMapping("/api/itineraries/{itineraryId}/stops")
class ItineraryStopController {

    private final ItineraryStopService itineraryStopService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItineraryStopDetails> createItineraryStop(@Valid @RequestBody CreateItineraryStopRequest request) {
        return status(CREATED).body(itineraryStopService.handle(request));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItineraryStopDetails>> findItineraryStops(@PathVariable("itineraryId") @Positive Long itineraryId) {
        return ok(itineraryStopService.findItineraryStopsByItineraryId(itineraryId));
    }

    @PutMapping(value = "/{stopId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItineraryStopDetails> updateItineraryStop(@Valid @RequestBody UpdateItineraryStopRequest request) {
        return ok(itineraryStopService.handle(request));
    }

    @DeleteMapping("/{stopId}")
    public ResponseEntity<?> deleteItineraryStop(@PathVariable("itineraryId") @Positive Long itineraryId,
                                                 @PathVariable("stopId") @Positive Long stopId) {
        itineraryStopService.deleteByItineraryIdAndStopId(itineraryId, stopId);
        return noContent().build();
    }

}
