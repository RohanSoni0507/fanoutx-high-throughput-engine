package com.fanoutx.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector {

    public final AtomicLong processed = new AtomicLong();
    public final AtomicLong success = new AtomicLong();
    public final AtomicLong failure = new AtomicLong();
}
