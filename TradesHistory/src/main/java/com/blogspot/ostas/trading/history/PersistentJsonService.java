package com.blogspot.ostas.trading.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class PersistentJsonService{
    private static final Logger LOG = LoggerFactory.getLogger(PersistentJsonService.class);
    public void persist(String file, byte[] content) {
        try {
            Files.write(Paths.get(file),content);
        } catch (IOException e) {
            LOG.error("Unable to write results to file",e);
        }
    }
}
