package com.roamly.itineraries.stops;

import com.roamly.itineraries.Itinerary;
import com.roamly.itineraries.stops.api.request.CreateItineraryStopRequest;
import com.roamly.itineraries.stops.api.response.ItineraryStopDetails;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.Instant.now;

@Entity
@Table(name = "itinerary_stops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ItineraryStop {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    private Instant modifiedDate;

    ItineraryStopDetails toDetails() {
        return ItineraryStopDetails.builder()
                .id(id)
                .itineraryId(itinerary.getId())
                .location(location)
                .description(description)
                .build();
    }

    static ItineraryStop createdFrom(CreateItineraryStopRequest request) {
        return builder()
                .createdDate(now())
                .modifiedDate(now())
                .itinerary(Itinerary.builder().id(request.itineraryId()).build())
                .location(request.location())
                .description(request.description())
                .build();
    }
}