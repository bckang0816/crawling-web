package com.aa.crawlingweb.controller;

import com.aa.crawlingweb.model.request.CrawlingRequest;
import com.aa.crawlingweb.model.response.CrawlingResponse;
import com.aa.crawlingweb.model.store.Store;
import com.aa.crawlingweb.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CrawlingController {

    private final CrawlingService crawlingService;

    @PostMapping(value = "/crawling", produces="application/json;charset=UTF-8")
    @ResponseBody
    public CrawlingResponse crawling(@RequestBody CrawlingRequest request) {
        log.info("request : {}", request);
        CrawlingResponse result = new CrawlingResponse();
        try {
            result = crawlingService.runCrawling(request);
        } catch (Exception e) {
            log.info("{}", e);
        }
        return result;
    }

}
