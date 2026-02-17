package com.fanoutx.transformation;

import com.fanoutx.model.Record;

public interface Transformer {
    byte[] transform(Record record) throws Exception;
}
