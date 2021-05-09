package com.aa.crawlingweb.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CrawlingRequest {

    private List<Long> ids;
    private boolean useSecretMode;
    private boolean useHeadlessMode;

}
