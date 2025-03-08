package com.roamly.itineraries;

import com.roamly.itineraries.api.request.CreateItineraryRequest;
import com.roamly.itineraries.api.response.ItineraryDetails;
import com.roamly.itineraries.stops.ItineraryStop;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.Instant.now;

@Entity
@Table(name = "itineraries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Itinerary {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "itinerary", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<ItineraryStop> stops = new ArrayList<>();

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedBy
    private String modifiedBy;

    @LastModifiedDate
    private Instant modifiedDate;

    ItineraryDetails toDetails() {
        return ItineraryDetails.builder()
                .id(id)
                .title(title)
                .destination(destination)
                .description(description)
                .build();
    }

    static Itinerary createdFrom(CreateItineraryRequest request, String createdBy) {
        return builder()
                .createdBy(createdBy)
                .createdDate(now())
                .modifiedBy(createdBy)
                .modifiedDate(now())
                .title(request.title())
                .destination(request.destination())
                .description(request.description())
                .build();
    }
}