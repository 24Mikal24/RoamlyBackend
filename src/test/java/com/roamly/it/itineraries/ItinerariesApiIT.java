package com.roamly.it.itineraries;

import com.roamly.it.RoamlyContainersIT;
import com.roamly.it.RoamlyTest;
import com.roamly.it.support.data.RoamlyDbSeeder;
import com.roamly.it.support.security.WithAuthenticatedUser;
import com.roamly.itineraries.api.CreateItineraryRequest;
import com.roamly.itineraries.api.UpdateItineraryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.roamly.it.TestUtils.toJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RoamlyTest
class ItinerariesApiIT extends RoamlyContainersIT {

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
    void createsItinerary() throws Exception {
        var request = new CreateItineraryRequest("title", "destination", "description");

        mvc.perform(post("/api/itineraries")
                    .contentType(APPLICATION_JSON)
                    .content(toJson(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.destination").value(request.destination()))
                .andExpect(jsonPath("$.description").value(request.description()));
    }

    @Test
    @WithAuthenticatedUser
    void findsItineraries() throws Exception {
        var itineraryId = seeder.insertItinerary();

        mvc.perform(get("/api/itineraries"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itineraryId))
                .andExpect(jsonPath("$[0].title").hasJsonPath())
                .andExpect(jsonPath("$[0].destination").hasJsonPath())
                .andExpect(jsonPath("$[0].description").hasJsonPath());
    }

    @Test
    @WithAuthenticatedUser
    void updatesItinerary() throws Exception {
        var itineraryId = seeder.insertItinerary();
        var request = new UpdateItineraryRequest(
                itineraryId,
                "newTitle",
                "newDestination",
                "newDescription"
        );

        mvc.perform(put(String.format("/api/itineraries/%s", itineraryId))
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.id().toString()))
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.destination").value(request.destination()))
                .andExpect(jsonPath("$.description").value(request.description()));
    }

    @Test
    @WithAuthenticatedUser
    void deletesItinerary() throws Exception {
        var itineraryId = seeder.insertItinerary();

        mvc.perform(delete(String.format("/api/itineraries/%s", itineraryId)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
