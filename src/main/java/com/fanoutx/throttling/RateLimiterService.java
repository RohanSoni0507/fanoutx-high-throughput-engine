package com.fanoutx.throttling;

import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RateLimiterService {

    private final int permitsPerSecond;
    private final Semaphore semaphore;

    public RateLimiterService(int permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.semaphore = new Semaphore(permitsPerSecond);

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    semaphore.release(
                        permitsPerSecond - semaphore.availablePermits()
                    );
                }, 1, 1, TimeUnit.SECONDS);
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }
}
