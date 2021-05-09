package com.aa.crawlingweb.model.response;

import com.aa.crawlingweb.model.store.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class CrawlingResponse {

    private List<CrawlingChoices> stores = new ArrayList<>();
    private List<Long> fails = new ArrayList<>();

}
