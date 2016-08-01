package com.bskyb.internettv.spike;

import com.bskyb.internettv.spike.util.JerseyServerRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;

import static com.bskyb.internettv.spike.util.ExceptionTesting.expect;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.assertj.core.api.Assertions.assertThat;

public class DemoResourceTest {

    private final DemoResource resource = new DemoResource();

    @Rule
    public final JerseyServerRule jerseyServerRule = new JerseyServerRule(resource);

    @Test
    public void get_shouldReturnHelloWorld() throws Exception {
        //when
        String result = resource.getEndpoint(null);

        //then
        assertThat(result).isEqualTo("Hello world");
    }

    @Test
    public void get_shouldThrowForbiddenException() throws Exception {
        //when
        expect(ForbiddenException.class, () -> resource.getEndpoint(403));
    }


    @Test
    public void get_shouldThrowWebApplicationException() throws Exception {
        //given
        int failWith = 415;

        //when
        WebApplicationException exception = expect(WebApplicationException.class, () -> resource.getEndpoint(failWith));

        //then
        assertThat(exception).hasMessage("HTTP 415 Unsupported Media Type");
        assertThat(exception.getResponse().getStatus()).isEqualTo(failWith);
    }

    @Test
    public void get_wiringIsCorrect() throws Exception {
        //when
        String result = jerseyServerRule.target()
            .path("/test/something")
            .request(TEXT_PLAIN)
            .get(String.class);

        //then
        assertThat(result).isEqualTo("Hello world");
    }
}