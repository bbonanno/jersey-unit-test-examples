package com.bskyb.internettv.spike;

import lombok.RequiredArgsConstructor;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@RequiredArgsConstructor
public class DemoService {

    private final WebTarget dependency;

    public String loadSomeData(String someParameter, String anotherParam) {
        Response response = dependency.path("path/to/what/i/want")
            .path(someParameter)
            .queryParam("queryParam", anotherParam)
            .request(APPLICATION_JSON)
            .get();

        if(SUCCESSFUL != response.getStatusInfo().getFamily()) {
            response.close();
            throw new BusinessException("Boom: " + response.getStatus());
        }

        return response.readEntity(String.class);
    }


    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }
}
