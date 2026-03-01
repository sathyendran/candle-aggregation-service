# 📈 Candle Aggregation Service

A high-performance backend service designed to transform real-time market **bid/ask streams** into multi-timeframe **OHLC (Open, High, Low, Close)** candlesticks.

This solution leverages:

* Apache Kafka for event streaming
* TimescaleDB for advanced time-series analytics
* Docker Compose for container orchestration

---

# 🏗 Architectural Design

The service follows a modern **Event-Driven Architecture (EDA)** pattern, decoupling ingestion from analytical computation.

```
Market Producer → Kafka → Candle Aggregation Service → TimescaleDB → REST API
```

### Why Event-Driven?

* High scalability
* Loose coupling
* Resilience against failures
* Replay capability for historical rebuilds

---

# 🧰 Tech Stack

| Component        | Technology                                                                             |
| ---------------- |----------------------------------------------------------------------------------------|
| Language         | Java 21 (using `record` types for immutability) and Virtual thread for Highperformance |
| Event Bus        | Apache Kafka (KRaft mode)                                                              |
| Database         | TimescaleDB                                                                            |
| Containerization | Docker Compose                                                                         |
| Build Tool       | Gradle                                                                                 |

---

# 🚀 Key Architectural Decisions

## 1️⃣ Continuous Aggregates (Materialized Views)

Instead of calculating candles in Java memory (which risks data loss on restart), the service relies on **TimescaleDB Continuous Aggregates**.

### Benefits:

* Exactly-once consistency for historical data
* Automatic background refresh
* Fault tolerance

---

## 2️⃣ Hierarchical Rollups

Higher timeframes are rolled up from smaller materialized views:

* 1s → 5s → 1m → 15m → 1h

This dramatically reduces:

* CPU load
* Disk I/O
* Query latency

---

## 3️⃣ Real-Time Transparency

Continuous aggregates are configured with:

```
materialized_only = false
```

This allows the History API to return:

* Materialized historical data
* Most recent raw ticks

Providing near real-time accuracy.

---

# 🛠 Project Components & Setup

---

## 📌 Prerequisites

* Docker & Docker Compose
* JDK 21
* Gradle

---

## 1️⃣ Infrastructure Setup

The `docker-compose.yml` initializes:

* Kafka broker (KRaft mode)
* Kafka-UI
* TimescaleDB (pre-configured schema)

```bash
docker-compose up -d
```

---

## 2️⃣ Database Schema

On startup, `migration.sql`:

### ✔ Creates Hypertable

```sql
SELECT create_hypertable('market_data', 'time');
```

### ✔ Establishes Continuous Aggregate Policies

* 1s view refreshed every 10 seconds
* Automatic rollups for higher intervals

### ✔ Handles Late Events

* Start/End offsets configured
* Accepts out-of-order Kafka events (up to 1 hour late)

---

# ⚖ Assumptions & Trade-offs

### 📌 Mid-Price Logic

OHLC values are calculated using:

```
(bid + ask) / 2
```

---

### 📌 Volume Logic

Volume is derived as:

```
count(*) per bucket
```

(Synthetic tick count volume)

---

### 📌 Performance Trade-off

**Pros:**

* Reduced Java heap pressure
* No complex in-memory aggregation
* Crash-safe

**Cons:**

* Increased reliance on database compute

Mitigation:

* TimescaleDB indexing
* Background worker optimizations
* Hierarchical rollups

---

### 📌 Latency Consideration

`schedule_interval` introduces a small deterministic lag (e.g., 10s).

This is standard in high-accuracy historical systems.

---

# 🌐 API Reference

## Get Candle History

```
GET /history
```

### Query Parameters

| Parameter | Description                   |
| --------- | ----------------------------- |
| symbol    | Market symbol (e.g., BTC-USD) |
| interval  | 1s, 5s, 1m, 15m, 1h           |
| from      | Start time (Unix timestamp)   |
| to        | End time (Unix timestamp)     |

---

### Example

```bash
curl "http://localhost:8080/history?symbol=BTC-USD&interval=1m&from=1620000000&to=1620000600"
```

---

# 🧪 Testing & Observability

## 📊 Kafka UI

Accessible at:

```
http://localhost:8080
```

Monitor:

* Topic throughput
* Consumer lag
* Offset commits

---

## 🧪 Unit Tests

Focus Areas:

* `BidAskEvent` mapping
* REST controller validation
* Interval routing logic

---

## 🔗 Integration Tests

Validate:

* Kafka → Consumer → Persistence
* Continuous aggregate correctness
* API → Database consistency

---

# 📂 Project Structure (Recommended)

```
candle-aggregation-service/
│
├── docker-compose.yml
├── db/migration.sql
├── build.gradle
│
├── src/main/java/
│   ├── ingestion/
│   ├── service/
│   ├── controller/
│   └── model/
│
└── src/test/java/
```

---

# 🎯 Design Goals

* Exactly-once historical correctness
* Near real-time query capability
* Horizontal scalability
* Database-driven aggregation
* Clean, immutable domain model

---

# 🔮 Future Enhancements

* Dynamic interval support
* Distributed deployment with Kubernetes

---

# 📌 Summary

The Candle Aggregation Service is a production-grade, scalable, event-driven system built using:

* Apache Kafka
* TimescaleDB
* Java 21
* Docker-based infrastructure

It provides:

✔ High throughput ingestion

✔ Exactly-once historical aggregation

✔ Efficient multi-timeframe rollups

✔ Real-time API transparency

