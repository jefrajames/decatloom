package io.jefrajames.loombench;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/loombench")
public class LoomBenchResource {

    @Inject
    LoomBenchService svc;

    @GET
    @Produces("text/plain")
    public String loomBench() {
        return svc.runBusinessLogic();
    }

}
