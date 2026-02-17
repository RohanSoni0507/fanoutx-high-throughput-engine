package com.fanoutx.transformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fanoutx.model.Record;

public class JsonTransformer implements Transformer {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] transform(Record record) throws Exception {
        return mapper.writeValueAsBytes(record);
    }
}

