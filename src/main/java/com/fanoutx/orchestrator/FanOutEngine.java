package com.fanoutx.orchestrator;

import com.fanoutx.model.Record;
import com.fanoutx.sink.Sink;
import com.fanoutx.metrics.MetricsCollector;
import com.fanoutx.resilience.RetryHandler;

import java.util.List;
import java.util.concurrent.*;

public class FanOutEngine {

    private final BlockingQueue<Record> queue;
    private final List<Sink> sinks;
    private final MetricsCollector metrics;

    public FanOutEngine(
            BlockingQueue<Record> queue,
            List<Sink> sinks,
            MetricsCollector metrics
    ) {
        this.queue = queue;
        this.sinks = sinks;
        this.metrics = metrics;
    }

    public void start() {

        ExecutorService executor =
                Executors.newVirtualThreadPerTaskExecutor();

        while (true) {
            try {
                Record record = queue.take();

                executor.submit(() -> {
                    for (Sink sink : sinks) {
                        try {
                            RetryHandler.executeWithRetry(
                                    () -> {
                                        try {
                                            sink.send(record);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }, 3);

                            metrics.success.incrementAndGet();

                        } catch (Exception e) {
                            metrics.failure.incrementAndGet();
                        }
                    }
                    metrics.processed.incrementAndGet();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
