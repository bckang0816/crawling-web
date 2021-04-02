package com.aa.crawlingweb.util;

import com.aa.crawlingweb.config.CrawlerConfig;
import com.aa.crawlingweb.model.request.CrawlingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeleniumUtil {

    private final CrawlerConfig config;

    public WebDriver getInstance(CrawlingRequest request) {

        System.setProperty(config.getDriver().getId(), config.getDriver().getPath());
        ChromeOptions options = new ChromeOptions();
        options.setCapability("ignoreProtectedModeSettings", config.isIgnoreProtectedModeSettings());
        if(config.isUseAllowedIps()) options.addArguments("--allowed-ips");
        if(config.isUseSecretMode() && request.isUseSecretMode()) options.addArguments("--incognito");
        if(config.isUseHeadlessMode() && request.isUseHeadlessMode()) {
            log.info("Headless!!");
            options.addArguments("--headless");
        }
        return new ChromeDriver(options);

    }
}
