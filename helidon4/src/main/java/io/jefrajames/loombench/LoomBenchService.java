package io.jefrajames.loombench;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoomBenchService {

    private static final AtomicLong counterStarted = new AtomicLong(0);
    private static final AtomicLong counterFinished = new AtomicLong(0);
    private static final AtomicLong maxDiff = new AtomicLong(0);

    @Inject
    @ConfigProperty(name = "db.pool.size", defaultValue = "100")
    private int dbPoolSize;

    @Inject
    DbPool dbPool;

    private static final int MIN_LATENCY = 10;
    private static final int MAX_LATENCY = 30;
    private static final Random randomLatency = new Random();

    private static void introduceSomeLatency() {
        try {
            Thread.sleep(randomLatency.nextInt(MAX_LATENCY - MIN_LATENCY) + MIN_LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void doSomeComputing() {
        var bi = BigInteger.ONE;
        for (int i = 0; i < 1_000; i++) {
            bi = bi.multiply(BigInteger.valueOf(i));
        }
    }

    private static long getUsedMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    }

    private static long getTotalMemory() {
        return (Runtime.getRuntime().totalMemory()) / 1024 / 1024;
    }

    private String formatCounters() {
        return String.format(
            "%s-Total %,d, Running %,d, Max %,d, DbPool %,d/%,d, Memory %,d/%,d MB",
            Thread.currentThread().isVirtual()?"VT":"PT",
            counterStarted.get(),
            counterStarted.get() - counterFinished.get(),
            maxDiff.get(),
            dbPool.getDBPoolUsed(),
            dbPool.getDbPoolSize(),
            getUsedMemory(),
            getTotalMemory());
    }

    // Simulate business logic here
    public String runBusinessLogic() {
        long started = counterStarted.incrementAndGet();
        long diff = started - counterFinished.get();
        maxDiff.updateAndGet(current -> Math.max(current, diff));
        dbPool.acquireCon();
        introduceSomeLatency();
        doSomeComputing();
        dbPool.releaseCon();

        String result = formatCounters();
        if (started % 1_000 == 0)
            System.out.println(result);

        counterFinished.incrementAndGet();
        return result;

    }

}
