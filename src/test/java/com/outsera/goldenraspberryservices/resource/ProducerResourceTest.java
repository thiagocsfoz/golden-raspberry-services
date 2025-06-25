package com.outsera.goldenraspberryservices.resource;

import com.outsera.goldenraspberryservices.util.TestDataSetup;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class ProducerResourceTest {

    @Inject
    TestDataSetup testDataSetup;

    @BeforeEach
    public void setup() {
        testDataSetup.cleanup();
    }

    @Test
    public void getAwardIntervals_shouldReturnCorrectMinAndMax() {
        testDataSetup.setupWinningMovie(2001, "Min Movie 1", List.of("Min Producer"));
        testDataSetup.setupWinningMovie(2002, "Min Movie 2", List.of("Min Producer"));

        testDataSetup.setupWinningMovie(2005, "Mid Movie 1", List.of("Mid Producer"));
        testDataSetup.setupWinningMovie(2010, "Mid Movie 2", List.of("Mid Producer"));

        testDataSetup.setupWinningMovie(1990, "Max Movie 1", List.of("Max Producer"));
        testDataSetup.setupWinningMovie(2020, "Max Movie 2", List.of("Max Producer"));

        testDataSetup.setupWinningMovie(2018, "Single Win Movie", List.of("Single Win Producer"));

        given()
                .when().get("/producers/award-intervals")
                .then()
                .statusCode(200)
                .body("min", hasSize(1))
                .body("min[0].producer", is("Min Producer"))
                .body("min[0].interval", is(1))
                .body("min[0].previousWin", is(2001))
                .body("min[0].followingWin", is(2002))
                .body("max", hasSize(1))
                .body("max[0].producer", is("Max Producer"))
                .body("max[0].interval", is(30))
                .body("max[0].previousWin", is(1990))
                .body("max[0].followingWin", is(2020));
    }

    @Test
    public void getAwardIntervals_whenMultipleProducersShareMinInterval_shouldReturnBoth() {
        testDataSetup.setupWinningMovie(2001, "Movie A1", List.of("Producer A"));
        testDataSetup.setupWinningMovie(2002, "Movie A2", List.of("Producer A")); // Intervalo 1

        testDataSetup.setupWinningMovie(2010, "Movie B1", List.of("Producer B"));
        testDataSetup.setupWinningMovie(2011, "Movie B2", List.of("Producer B")); // Intervalo 1

        testDataSetup.setupWinningMovie(2000, "Movie C", List.of("Producer C"));
        testDataSetup.setupWinningMovie(2020, "Movie C2", List.of("Producer C")); // Intervalo 20 (max)

        given()
                .when().get("/producers/award-intervals")
                .then()
                .statusCode(200)
                .body("min", hasSize(2))
                .body("min.producer", hasItems("Producer A", "Producer B")) // Valida que ambos estão na lista
                .body("max", hasSize(1))
                .body("max[0].producer", is("Producer C"));
    }

    @Test
    public void getAwardIntervals_whenNoProducerHasMultipleWins_shouldReturnEmptyLists() {
        testDataSetup.setupWinningMovie(2001, "Movie A", List.of("Producer A"));
        testDataSetup.setupWinningMovie(2010, "Movie B", List.of("Producer B"));

        given()
                .when().get("/producers/award-intervals")
                .then()
                .statusCode(200)
                .body("min", hasSize(0))
                .body("max", hasSize(0));
    }
}