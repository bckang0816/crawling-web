package com.aa.crawlingweb.service.impl;

import com.aa.crawlingweb.config.CrawlerConfig;
import com.aa.crawlingweb.model.request.CrawlingRequest;
import com.aa.crawlingweb.model.response.CrawlingResponse;
import com.aa.crawlingweb.model.store.Store;
import com.aa.crawlingweb.service.CrawlingService;
import com.aa.crawlingweb.util.LoggingUtil;
import com.aa.crawlingweb.util.SearchUtil;
import com.aa.crawlingweb.util.SeleniumUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingService")
public class CrawlingServiceImpl implements CrawlingService {

    private final CrawlerConfig config;

    private final SeleniumUtil seleniumUtil;

    private final LoggingUtil loggingUtil;

    private final SearchUtil searchUtil;

    @Override
    public CrawlingResponse runCrawling(CrawlingRequest request) {

        // 로그 초기화
        loggingUtil.init();

        // Selenium 드라이브 생성
        WebDriver driver = seleniumUtil.getInstance(request);

        CrawlingResponse database = searchUtil.getMapInfo(request.getUrls(), driver);

        // Selenium 드라이브 종료
        driver.close();
        driver.quit();

        return database;
    }

}
