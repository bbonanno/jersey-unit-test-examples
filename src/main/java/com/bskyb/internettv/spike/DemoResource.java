package com.bskyb.internettv.spike;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/test")
public class DemoResource {

    @GET
    @Path("something")
    @Produces(TEXT_PLAIN)
    public String getEndpoint(@QueryParam("failWith") Integer failWith) {

        if (failWith == null) {
            return "Hello world";
        }

        if (failWith == 403) {
            throw new ForbiddenException();
        }

        throw new WebApplicationException(failWith);
    }


}
