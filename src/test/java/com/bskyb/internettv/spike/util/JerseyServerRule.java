package com.bskyb.internettv.spike.util;

import lombok.SneakyThrows;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

public class JerseyServerRule implements TestRule {

    private final Object resource;
    private final Server server;

    @SneakyThrows
    public JerseyServerRule(Object resource) {
        this.resource = resource;
        this.server = new Server();
    }

    public WebTarget target() {
        return server.target();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    server.setUp();
                    base.evaluate();
                } finally {
                    server.tearDown();
                }
            }
        };
    }

    private class Server extends JerseyTest {

        @Override
        protected Application configure() {
            return new ResourceConfig()
                .register(resource);
        }
    }

}

