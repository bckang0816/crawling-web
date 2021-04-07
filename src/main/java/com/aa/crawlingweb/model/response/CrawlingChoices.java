package com.aa.crawlingweb.model.response;

import com.aa.crawlingweb.model.store.Store;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class CrawlingChoices {

    private String placeName;
    private String mapUrl;
    private String uuid;
    private List<Store> choices = new ArrayList<>();

}
