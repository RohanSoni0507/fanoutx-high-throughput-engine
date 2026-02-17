package com.fanoutx.ingestion;

import com.fanoutx.model.Record;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

public class FileReaderService {

    private final BlockingQueue<Record> queue;

    public FileReaderService(BlockingQueue<Record> queue) {
        this.queue = queue;
    }

    public void readFile(String path) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                Record record = Record.fromCsv(line);
                queue.put(record); // backpressure
            }
        }
    }
}



