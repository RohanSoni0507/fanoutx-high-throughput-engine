package com.fanoutx.sink;

import com.fanoutx.model.Record;
import com.fanoutx.throttling.RateLimiterService;

public class RestSink implements Sink {

    private final RateLimiterService rateLimiter;

    public RestSink(RateLimiterService rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void send(Record record) throws Exception {
        rateLimiter.acquire();
        Thread.sleep(10); // simulate HTTP call
    }

    @Override
    public String name() {
        return "REST";
    }
}

