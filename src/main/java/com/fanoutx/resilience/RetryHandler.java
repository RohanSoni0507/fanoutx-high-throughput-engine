package com.fanoutx.resilience;

public class RetryHandler {

    public static void executeWithRetry(Runnable task, int maxRetries) {

        int attempts = 0;

        while (attempts < maxRetries) {
            try {
                task.run();
                return;
            } catch (Exception e) {
                attempts++;
                if (attempts >= maxRetries) {
                    throw e;
                }
            }
        }
    }
}
