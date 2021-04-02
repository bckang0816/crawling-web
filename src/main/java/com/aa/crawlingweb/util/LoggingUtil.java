package com.aa.crawlingweb.util;

import com.aa.crawlingweb.config.CrawlerConfig;
import com.aa.crawlingweb.model.type.LoggingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingUtil {

    private final CrawlerConfig config;

    public void init() {
        String text = "Start Crawling!!!";
        File file = new File(config.getFiles().getLog());
        if(!file.exists()) {
            file.mkdirs();
        }
        if(LoggingMode.NEW.equals(config.getLogging().getMode())) file.delete();
        writeFile(text);
    }

    public void writeFile(String data) {
        log.info("{}", data);
        File file = new File(config.getFiles().getLog());
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(data.concat("\n"));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
