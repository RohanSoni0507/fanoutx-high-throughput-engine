package com.fanoutx.model;

public class Record {

    private final String id;
    private final String payload;

    public Record(String id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    public static Record fromCsv(String line) {
        String[] parts = line.split(",");
        return new Record(parts[0], parts[1]);
    }

    public String getId() { return id; }
    public String getPayload() { return payload; }
}
