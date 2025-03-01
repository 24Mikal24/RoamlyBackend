package com.roamly.it;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@Retention(RUNTIME)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql("/db/scripts/clean_roamly_db.sql")
@SqlMergeMode(MERGE)
@Target(TYPE)
public @interface RoamlyTest {

    String value() default "";
}
