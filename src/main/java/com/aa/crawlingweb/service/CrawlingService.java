package com.aa.crawlingweb.service;

import com.aa.crawlingweb.model.request.CrawlingRequest;
import com.aa.crawlingweb.model.response.CrawlingResponse;

import java.io.IOException;

public interface CrawlingService {

    CrawlingResponse runCrawling(CrawlingRequest urls) throws IOException;

}
