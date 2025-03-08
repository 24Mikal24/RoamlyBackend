package com.roamly.itineraries;

import com.roamly.itineraries.api.CreateItineraryRequest;
import com.roamly.itineraries.api.UpdateItineraryRequest;
import com.roamly.itineraries.api.ItineraryDetails;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor(access = PACKAGE)
@RequestMapping("/api/itineraries")
class ItineraryController {

    private final ItineraryService itineraryService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItineraryDetails> createItineraryByUserId(@Valid @RequestBody CreateItineraryRequest request,
                                                                    @AuthenticationPrincipal Jwt jwt) {
        return status(CREATED).body(itineraryService.handle(request, jwt.getClaim("sub")));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItineraryDetails>> findItineraries() {
        return ok(itineraryService.findItineraries());
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ItineraryDetails> updateItineraryById(@Valid @RequestBody UpdateItineraryRequest request) {
        return ok(itineraryService.handle(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItineraryById(@PathVariable("id") @Positive Long id) {
        itineraryService.deleteItineraryById(id);
        return noContent().build();
    }
}
