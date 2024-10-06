package io.jefrajames.loombench;

import java.util.concurrent.TimeUnit;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import lombok.extern.java.Log;

@Path("/loombench")
@Log
public class LoomBenchResource {

    @Inject
    LoomBenchService svc;

    @GET
    @Path("/platform")
    @Produces("text/plain")
    public String loomBenchWithPlatformThread() {
        return svc.runBusinessLogic();
    }

    @GET
    @Path("/virtual")
    @Produces("text/plain")
    @RunOnVirtualThread
    public String loomBenchWithVirtualThread() {
        return svc.runBusinessLogic();
    }

    @GET
    @Path("/vs")
    @Produces("text/plain")
    @RunOnVirtualThread
    public synchronized String loomBenchVs() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(25);
        return "Synchronized hello from loombench";
    }

}
