package com.fanoutx;

import com.fanoutx.ingestion.FileReaderService;
import com.fanoutx.metrics.MetricsCollector;
import com.fanoutx.orchestrator.FanOutEngine;
import com.fanoutx.sink.RestSink;
import com.fanoutx.throttling.RateLimiterService;

import java.util.List;
import java.util.concurrent.*;

public class App {

    public static void main(String[] args) throws Exception {

        BlockingQueue<com.fanoutx.model.Record> queue =
                new ArrayBlockingQueue<>(1000);

        MetricsCollector metrics = new MetricsCollector();

        RestSink restSink =
                new RestSink(new RateLimiterService(50));

        FanOutEngine engine =
                new FanOutEngine(queue, List.of(restSink), metrics);

        new Thread(engine::start).start();

        new FileReaderService(queue)
                .readFile("sample-input.csv");

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    System.out.println("Processed: " +
                            metrics.processed.get());
                }, 5, 5, TimeUnit.SECONDS);
    }
}
