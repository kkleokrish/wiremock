package com.github.tomakehurst.wiremock.matching;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.common.Json;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class EqualToPatternTest {

    @Test
    public void returnsANonZeroScoreForPartialMatchOnEquals() {
        StringValuePattern pattern = StringValuePattern.equalTo("matchthis");
        assertThat(pattern.match("matchthisbadlydone").getDistance(), is(0.5));
    }

    @Test
    public void returns1ForNoMatchOnEquals() {
        StringValuePattern pattern = StringValuePattern.equalTo("matchthis");
        assertThat(pattern.match("924387348975923").getDistance(), is(1.0));
    }

    @Test
    public void returns0ForExactMatchOnEquals() {
        StringValuePattern pattern = StringValuePattern.equalTo("matchthis");
        assertThat(pattern.match("matchthis").getDistance(), is(0.0));
    }

    @Test
    public void correctlyDeserialisesEqualToFromJson() {
        StringValuePattern stringValuePattern = Json.read(
            "{                               \n" +
            "  \"equalTo\": \"something\"    \n" +
            "}",
            StringValuePattern.class);

        assertThat(stringValuePattern, instanceOf(EqualToPattern.class));
        assertThat(stringValuePattern.getValue(), is("something"));
    }

    @Test
    public void failsWithMeaningfulErrorWhenOperatorNotRecognised() {
        try {
            Json.read(
                "{                               \n" +
                "  \"munches\": \"something\"    \n" +
                "}",
                StringValuePattern.class);

            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(JsonMappingException.class));
            assertThat(e.getMessage(), is("munches is not a recognised operator"));
        }

    }


}