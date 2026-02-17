# FanOutX – High-Throughput Data Fan-Out Engine

## Overview

FanOutX is a Java-based Distributed Data Fan-Out & Transformation Engine.

It reads large flat files (up to 100GB) in a streaming manner and distributes each record to multiple specialized downstream sinks such as:

- REST APIs
- gRPC services
- Message Queues
- Wide-column databases

The system ensures:

- High concurrency
- Controlled rate limiting
- Backpressure protection
- Retry resilience
- Zero data loss accounting

---

## Architecture

### High-Level Flow

```
File Reader (Streaming)
        ↓
BlockingQueue (Backpressure)
        ↓
FanOutEngine (Virtual Threads)
        ↓
Transformation Layer (Strategy Pattern)
        ↓
Rate Limiter + Retry
        ↓
Mock Sink Dispatch
        ↓
Metrics & Observability
```

---

## Key Features

- Streaming ingestion (no full file load)
- Virtual Threads (Java 21)
- Strategy Pattern for transformations
- Config-driven rate limits
- Backpressure using BlockingQueue
- Retry logic (max 3 attempts)
- Observability every 5 seconds
- Extensible sink architecture

---

## Concurrency Model

- Uses `Executors.newVirtualThreadPerTaskExecutor()`
- Each record is processed independently
- Sinks operate in parallel
- No shared mutable state without atomic controls

This allows near-linear scalability with CPU cores.

---

## Memory Management

- Uses `BufferedReader`
- Processes file line-by-line
- Uses bounded `ArrayBlockingQueue`
- Safe to run with:

```
-Xmx512m
```

Even for files up to 100GB.

---

## Backpressure Strategy

- Producer writes into a bounded `BlockingQueue`
- If sinks slow down:
  - Queue fills
  - Producer blocks
  - Memory does not grow
  - Prevents OOM

---

## Throttling

Each sink has its own `RateLimiterService`:

- REST: configurable requests/sec
- DB: configurable writes/sec
- gRPC: configurable streaming rate

Implemented using Semaphore + Scheduled refill (Token Bucket style).

---

## Retry Logic

Each record-sink operation:

- Retries up to 3 times
- After 3 failures → counted as failure
- Ensures no silent drops

---

## Observability

Every 5 seconds prints:

- Total records processed
- Success count
- Failure count
- Throughput (records/sec)

---

## Extensibility

To add a new sink (e.g., Elasticsearch):

1. Implement `Sink` interface
2. Add transformation (if required)
3. Register in SinkFactory

No change required in core orchestrator.

---

## Setup Instructions

### 1. Build

```
mvn clean package
```

### 2. Run

```
java -Xmx512m -jar target/fanoutx-1.0.0.jar
```

---

## Sample Input Format (CSV)

```
1,John Doe
2,Jane Smith
3,Robert Brown
```

---

## Configuration

All endpoints, rate limits, and file paths are defined in:

```
application.yaml
```

Example:

```yaml
filePath: sample-input.csv

sinks:
  rest:
    rateLimit: 50
  grpc:
    rateLimit: 200
  db:
    rateLimit: 1000
```

---

## Design Decisions

### Why Virtual Threads?
- Lightweight concurrency
- Ideal for IO-heavy workloads
- Better scalability compared to fixed thread pools

### Why BlockingQueue?
- Simple and robust backpressure mechanism
- Prevents unbounded memory usage

### Why Strategy Pattern?
- Clean separation of transformation logic
- Easy to extend for new formats

---

## Assumptions

- CSV input format
- Mock sinks simulate network calls
- Network latency is simulated via sleep
- No real external infrastructure required

---

## Testing

Unit tests cover:

- Transformer logic
- Retry logic
- Sink execution

Integration tests simulate:

- Full ingestion-to-sink pipeline

Run tests:

```
mvn test
```

---

## Zero Data Loss Guarantee

Every record is:

- Counted as Success
- Or counted as Failure after 3 retries

No silent record drops.

---

## Future Improvements

- Dead Letter Queue (DLQ)
- Circuit Breaker
- Async metrics exporter (Prometheus)
- Real HTTP/gRPC implementations
- Horizontal scaling support

---

## Conclusion

FanOutX demonstrates:

- High-throughput streaming ingestion
- Parallel fan-out architecture
- Safe memory handling
- Resilient retry and throttling
- Clean extensible design

This solution meets all functional and non-functional requirements defined in the challenge.
