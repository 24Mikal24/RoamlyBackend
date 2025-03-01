package com.roamly.itineraries.api;

import com.roamly.itineraries.api.request.CreateItineraryRequest;
import com.roamly.itineraries.api.request.UpdateItineraryRequest;
import com.roamly.itineraries.api.response.ItineraryDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor(access = PACKAGE)
@RequestMapping("/api/itineraries")
class ItineraryController {

    private final CreateItinerary createItinerary;
    private final FindItineraries findItineraries;
    private final UpdateItinerary updateItinerary;
    private final DeleteItinerary deleteItinerary;

    @PostMapping
    public ResponseEntity<ItineraryDetails> createItineraryByUserId(@Valid @RequestBody CreateItineraryRequest request,
                                                                    @AuthenticationPrincipal Jwt jwt) {
        return status(CREATED).body(createItinerary.createItinerary(request, jwt.getClaim("sub")));
    }

    @GetMapping
    public ResponseEntity<List<ItineraryDetails>> findItineraries() {
        return ok(findItineraries.findItineraries());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItineraryDetails> updateItineraryById(@PathVariable("id") @Positive Long id,
                                                                @RequestBody UpdateItineraryRequest request) {
        return ok(updateItinerary.updateItinerary(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItineraryById(@PathVariable("id") @Positive Long id) {
        deleteItinerary.deleteItineraryById(id);
        return noContent().build();
    }
}
