package com.bskyb.internettv.spike.util;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bskyb.internettv.spike.util.ExceptionTesting.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

public class JerseyClientRule implements TestRule {

    private final Object resource;
    private final Server server;
    private final WebTargetWrapper webTarget = new WebTargetWrapper();
    private final CheckResponseFilter responseFilter = new CheckResponseFilter();

    @SneakyThrows
    public JerseyClientRule(Object resource) {
        this.resource = resource;
        this.server = new Server();
    }

    public WebTarget target() {
        return webTarget;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    server.setUp();
                    webTarget.setDelegate(server.target().register(responseFilter));
                    base.evaluate();
                    responseFilter.getResponses().forEach(JerseyClientRule::assertClosedResponse);
                } finally {
                    server.tearDown();
                }
            }
        };
    }

    private static void assertClosedResponse(ClientResponseContext responseContext) {
        try {
            responseContext.getEntityStream().read();
            fail("Response hasn't been closed");
        } catch (Exception exception) {
            assertThat(exception)
                .withFailMessage("Response hasn't been closed")
                .hasMessage("Entity input stream has already been closed.");
        }
    }

    private class Server extends JerseyTest {

        @Override
        protected Application configure() {
            return new ResourceConfig()
                .register(resource);
        }
    }

    private static class CheckResponseFilter implements ClientResponseFilter {

        @Getter
        private final List<ClientResponseContext> responses = new ArrayList<>();

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            responses.add(responseContext);
        }
    }

    private static class WebTargetWrapper implements WebTarget {

        @Setter
        @Delegate
        private WebTarget delegate;

    }
}

