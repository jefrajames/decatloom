
package io.jefrajames.loombench;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.helidon.metrics.api.MetricsFactory;
import io.helidon.microprofile.testing.junit5.HelidonTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

@HelidonTest
class LoomBenchResourceTest {

    @Inject
    private MetricRegistry registry;

    @Inject
    private WebTarget target;

    @AfterAll
    static void clear() {
        MetricsFactory.closeAll();
    }

    // @Test
    void testMicroprofileMetrics() {

        Timer timer = registry.timer("loombenchGets");
        double before = timer.getCount();

        String message = target.path("loombench")
                .request()
                .get(String.class);
        assertThat(message, startsWith("Virtual thread"));

        double after = timer.getCount();
        assertEquals(1d, after - before, "Difference in bench counter between successive calls");
    }

    @Test
    void testHealth() {
        Response response = target
                .path("health")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
    }

}
