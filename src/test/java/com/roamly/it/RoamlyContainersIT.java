package com.roamly.it;


import com.github.dockerjava.api.model.Bind;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

@ActiveProfiles("test")
public abstract class RoamlyContainersIT {

    static {
        var postgreSQLVersion = Optional.ofNullable(System.getenv("POSTGRESQL_VERSION")).orElse("16.7");
        var postgreSQLImageName = String.format("postgres:%s", postgreSQLVersion);
        if (System.getProperty("os.arch").contains("aarch64")) {
            postgreSQLImageName = String.format("postgres:%s%s", postgreSQLVersion, "-alpine");
        }
        PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse(postgreSQLImageName))
                .withUsername("postgres")
                .withPassword("postgres")
                .withDatabaseName("roamly_db");

        POSTGRESQL_CONTAINER.start();
        System.setProperty("DB_URL", POSTGRESQL_CONTAINER.getJdbcUrl());
        System.setProperty("DB_USERNAME", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("DB_PASSWORD", POSTGRESQL_CONTAINER.getPassword());
    }
}
