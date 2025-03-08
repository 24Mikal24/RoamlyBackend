package com.roamly.it.itineraries.stops;

import com.roamly.it.RoamlyContainersIT;
import com.roamly.it.RoamlyTest;
import com.roamly.it.support.data.RoamlyDbSeeder;
import com.roamly.it.support.security.WithAuthenticatedUser;
import com.roamly.itineraries.stops.api.request.CreateItineraryStopRequest;
import com.roamly.itineraries.stops.api.request.UpdateItineraryStopRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.roamly.it.TestUtils.toJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RoamlyTest
class ItineraryStopsApiIT extends RoamlyContainersIT {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private RoamlyDbSeeder seeder;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @WithAuthenticatedUser
    void createsItineraryStop() throws Exception {
        var itineraryId = seeder.insertItinerary();

        var request = new CreateItineraryStopRequest(
                itineraryId,
                "location",
                "description");

        mvc.perform(post(String.format("/api/itineraries/%s/stops", itineraryId))
                    .contentType(APPLICATION_JSON)
                    .content(toJson(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itineraryId").value(request.itineraryId()))
                .andExpect(jsonPath("$.location").value(request.location()))
                .andExpect(jsonPath("$.description").value(request.description()));
    }

    @Test
    @WithAuthenticatedUser
    void findsItineraryStops() throws Exception {
        var stop = seeder.insertItineraryStop();

        mvc.perform(get(String.format("/api/itineraries/%s/stops", stop.get("itineraryId"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(stop.get("id")))
                .andExpect(jsonPath("$[0].itineraryId").hasJsonPath())
                .andExpect(jsonPath("$[0].location").hasJsonPath())
                .andExpect(jsonPath("$[0].description").hasJsonPath());
    }

    @Test
    @WithAuthenticatedUser
    void updatesItineraryStop() throws Exception {
        var stop = seeder.insertItineraryStop();

        var request = new UpdateItineraryStopRequest(
                stop.get("id"),
                stop.get("itineraryId"),
                "new location",
                "new description");

        mvc.perform(put(String.format("/api/itineraries/%s/stops/%s", stop.get("itineraryId"), stop.get("id")))
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stop.get("id")))
                .andExpect(jsonPath("$.itineraryId").value(stop.get("itineraryId")))
                .andExpect(jsonPath("$.location").value(request.location()))
                .andExpect(jsonPath("$.description").value(request.description()));
    }

    @Test
    @WithAuthenticatedUser
    void deleteItineraryStop() throws Exception {
        var stop = seeder.insertItineraryStop();

        mvc.perform(delete(String.format("/api/itineraries/%s/stops/%s", stop.get("itineraryId"), stop.get("id"))))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
