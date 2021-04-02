package com.aa.crawlingweb.config;

import com.aa.crawlingweb.model.type.LoggingMode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "crawler")
@Slf4j
public class CrawlerConfig {

    @Getter
    @Setter
    public static class Driver {
        private String id;
        private String path;
    }

    @Getter
    @Setter
    public static class Files {
        private String log;
    }

    @Getter
    @Setter
    public static class Logging {
        private LoggingMode mode;
    }

    private Driver driver = new Driver();

    private Files files = new Files();

    private Logging logging = new Logging();

    private boolean ignoreProtectedModeSettings;

    private boolean useAllowedIps;

    private boolean useSecretMode;

    private boolean useHeadlessMode;

    @PostConstruct
    private void init() {
        StringBuilder builder = new StringBuilder();

        builder.append(System.lineSeparator()).append("[Configurations]").append(System.lineSeparator());
        builder.append("\t").append("Driver ID").append(" = ").append(driver.getId()).append(System.lineSeparator());
        builder.append("\t").append("Driver Path").append(" = ").append(driver.getPath()).append(System.lineSeparator());
        builder.append("\t").append("Ignore Protected Mode Settings").append(" = ").append(ignoreProtectedModeSettings).append(System.lineSeparator());
        builder.append("\t").append("Use Allowed IPs").append(" = ").append(useAllowedIps).append(System.lineSeparator());
        builder.append("\t").append("Use Secret Mode").append(" = ").append(useSecretMode).append(System.lineSeparator());
        builder.append("\t").append("Use Headless Mode").append(" = ").append(useHeadlessMode).append(System.lineSeparator());
        builder.append("\t").append("Log File Path").append(" = ").append(files.getLog()).append(System.lineSeparator());
        builder.append("\t").append("Logging Mode").append(" = ").append(logging.getMode()).append(System.lineSeparator());

        log.info("{}", builder.toString());
    }

}
