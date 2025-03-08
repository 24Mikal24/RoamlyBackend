package com.roamly.itineraries;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface Itineraries extends JpaRepository<Itinerary, Long> {

    List<Itinerary> findByCreatedBy(String createdBy);
}