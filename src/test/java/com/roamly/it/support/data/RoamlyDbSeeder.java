package com.roamly.it.support.data;

import com.roamly.auth.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PACKAGE;

@Component
@Profile("test")
@RequiredArgsConstructor(access = PACKAGE)
public class RoamlyDbSeeder {

    private final Faker faker = new Faker();
    private final NamedParameterJdbcTemplate jdbc;

    public Map<String, String> insertItinerary() {
        var createdBy = insertUser();
        var title = faker.address().cityName() + " " + faker.options().option("Adventure", "Escape", "Getaway", "Vacation");
        var destination = faker.address().country();
        var description = faker.lorem().sentence();
        var params = new MapSqlParameterSource()
                .addValue("title", title)
                .addValue("destination", destination)
                .addValue("description", description)
                .addValue("created_by", createdBy)
                .addValue("modified_by", createdBy);

        var id = new SimpleJdbcInsert(jdbc.getJdbcTemplate())
                .withTableName("itineraries")
                .usingGeneratedKeyColumns("id")
                .usingColumns(params.getParameterNames())
                .executeAndReturnKey(params)
                .longValue();

        return Map.of("id", String.valueOf(id), "title", title, "destination", destination, "description", description);
    }

    public String insertUser() {
        var id = AuthenticatedUser.getCurrentUserId();
        var username = faker.internet().username();
        var email = faker.internet().emailAddress();
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("username", username)
                .addValue("email", email);

        new SimpleJdbcInsert(jdbc.getJdbcTemplate())
                .withTableName("users")
                .usingColumns(params.getParameterNames())
                .execute(params);

        return id;
    }
}
