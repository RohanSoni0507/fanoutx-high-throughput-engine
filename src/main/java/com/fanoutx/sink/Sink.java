package com.fanoutx.sink;

import com.fanoutx.model.Record;

public interface Sink {
    void send(Record record) throws Exception;
    String name();
}

