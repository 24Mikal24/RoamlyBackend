package com.roamly.itineraries.stops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ItineraryStops extends JpaRepository<ItineraryStop, Long> {

    List<ItineraryStop> findByItineraryId(Long id);
}
