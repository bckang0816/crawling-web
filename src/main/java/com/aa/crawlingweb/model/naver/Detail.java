package com.aa.crawlingweb.model.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Detail {

    private String name;
    private String virtualTel;
    private String tel;
    private String address;
    private String roadAddress;
    private List<String> shortAddress;
    private String x;
    private String y;
    private String bizhourInfo;
    private String homePage;
    private List<String> microReview;

    public Detail(Detail detail) {
        this.name = detail.getName();
        this.virtualTel = detail.getVirtualTel();
        this.tel = detail.getTel();
        this.address = detail.getAddress();
        this.roadAddress = detail.getRoadAddress();
        this.shortAddress = detail.getShortAddress();
        this.x = detail.getX();
        this.y = detail.getY();
        this.bizhourInfo = detail.getBizhourInfo();
        this.homePage = detail.getHomePage();
        this.microReview = detail.getMicroReview();
    }

}
