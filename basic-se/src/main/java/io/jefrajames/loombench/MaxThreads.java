package io.jefrajames.loombench;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Illustrates how long it takes to start (and join) 1 million Virtual
 * Threads
 * and how many carrier Platform Threads are used under the hood
 */
public class MaxThreads {

    private static final int MEGA_BYTE = 1024 * 1024;
    private static final int DEFAULT_THREAD_COUNT = 4_000;
    private static final String DEFAULT_THREAD_TYPE = "Platform";
    private static final Pattern WORKER_PATTERN = Pattern.compile("worker-[\\d?]");

    // Thread processing
    private static void process(Map<String, AtomicInteger> pThreads) {
        String threadName = Thread.currentThread().toString();
        Matcher workerMatcher = WORKER_PATTERN.matcher(threadName);
        if (workerMatcher.find()) {
            var cpt = pThreads.get(workerMatcher.group());
            if (cpt == null) {
                cpt = new AtomicInteger(1);
                pThreads.put(workerMatcher.group(), cpt);
            } else {
                cpt.incrementAndGet();
            }
        }
    }

    private static void printMemory(List<Thread> threads, String threadType) {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();

        System.err.printf("%s thread %,d: memory total=%,d MB, used=%,d MB, heap=%,d MB, non heap=%,d MB%n",
                threadType,
                threads.size(),
                Runtime.getRuntime().totalMemory() / MEGA_BYTE,
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MEGA_BYTE,
                mbean.getHeapMemoryUsage().getUsed() / MEGA_BYTE,
                mbean.getNonHeapMemoryUsage().getUsed() / MEGA_BYTE);

    }

    private static int determineLogInterval(int threadCount) {

        if (threadCount < 100)
            return 10;
        if (threadCount < 5_000)
            return 100;
        if (threadCount < 10_000)
            return 1_000;
        return 5_000;
    }

    public static void main(String... args) throws Exception {

        int threadCount = DEFAULT_THREAD_COUNT;
        String threadType = DEFAULT_THREAD_TYPE;

        int i = 0;
        while (i < args.length) {
            switch (args[i]) {
                case "-v" -> {
                    threadType = "Virtual";
                    i++;
                }
                case "-c" -> {
                    threadCount = Integer.parseInt(args[i + 1]);
                    i += 2;
                }
                default -> {
                    System.err.println("Usage: -v -c <threadCount>");
                    System.exit(1);
                }
            }
        }

        Map<String, AtomicInteger> carrierThreads = new ConcurrentHashMap<>();
        List<Thread> threads = new ArrayList<>(threadCount);
        CountDownLatch hold = new CountDownLatch(1);

        System.out.printf("Starting %,d %s threads in parallel%n", threadCount, threadType);
        ThreadFactory threadFactory = "Platform".equals(threadType) ? Thread.ofPlatform().factory()
                : Thread.ofVirtual().factory();

        Instant begin = Instant.now();

        int logInterval = determineLogInterval(threadCount);

        while (threads.size() < threadCount) {
            CountDownLatch started = new CountDownLatch(1);

            Thread thread = threadFactory.newThread(() -> {
                // Thread processing
                process(carrierThreads);
                started.countDown(); // decrement started
                // Wait until all thread have been started
                try {
                    hold.await();
                } catch (InterruptedException ignore) {
                }
            });

            // Back to main thread
            thread.start();

            threads.add(thread);
            started.await(); // block until started count equals 0: ensures the thread has been started
            if ((threads.size() % logInterval) == 0) {
                printMemory(threads, threadType);
            }
        } // End of thread creation & start main loop

        // Summary of threads creation
        Instant end = Instant.now();
        System.out.printf("%,d threads started in %,d ms%n",
                threadCount,
                Duration.between(begin, end).toMillis());

        // Unblock all created and waiting threads
        hold.countDown(); 

        // Start summary
        System.out.printf("Available cores: %d%n", Runtime.getRuntime().availableProcessors());
        if ("Virtual".equals(threadType))
            System.out.printf("Used carrier threads: %d%n", carrierThreads.size());
        carrierThreads.forEach((k, v) -> System.out.printf("Carrier thread %s called %,d times%n", k, v.intValue()));

        // Join all threads
        begin = Instant.now();
        System.out.println("Joining all threads...");
        for (Thread thread : threads) {
            thread.join();
        }
        end = Instant.now();
        System.err.printf("%,d threads joined in %,d ms%n",
                threadCount,
                Duration.between(begin, end).toMillis());
    }

}
