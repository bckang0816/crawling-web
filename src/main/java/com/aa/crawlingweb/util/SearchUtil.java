package com.aa.crawlingweb.util;

import com.aa.crawlingweb.model.naver.Detail;
import com.aa.crawlingweb.model.naver.StoreInfo;
import com.aa.crawlingweb.model.response.CrawlingChoices;
import com.aa.crawlingweb.model.response.CrawlingResponse;
import com.aa.crawlingweb.model.store.Store;
import com.aa.crawlingweb.model.type.MapType;
import com.aa.crawlingweb.validation.MapTypeValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchUtil {

    private final LoggingUtil loggingUtil;

    private final MapTypeValidation mapTypeValidation;

    public CrawlingResponse getMapInfo(List<String> urls, WebDriver driver) {
        CrawlingResponse database = new CrawlingResponse();
        for (String url : urls) {
            driver.get(url);

            MapType mapType = mapTypeValidation.checkMapType(driver);
            if(mapType == MapType.INVALID) {
                loggingUtil.writeFile("[Invalid Map Type] " + ", Map URL : " + driver.getCurrentUrl());
                database.getFails().add(driver.getCurrentUrl());
                continue;
            }
            getStoreInfo(driver, database, mapType);
        }
        return database;
    }

    private void getStoreInfo(WebDriver driver, CrawlingResponse database, MapType mapType) {
        Store store = new Store();
        store.setMapUrl(driver.getCurrentUrl());
        if (mapType == MapType.A) {
            WebElement nameAreaElement = driver.findElement(By.className("biz_name_area"));

            // 업체명
            store.setPlaceName(nameAreaElement.findElement(By.cssSelector("strong[class='name']")).getText());
            // 카테고리
            store.setCategory(nameAreaElement.findElement(By.xpath("//span[contains(@class,'category')]")).getText());

            // 편의시설
            List<WebElement> elements = driver.findElements(By.className("list_item"));
            for(WebElement element : elements) {
                try {
                    if(element.getText().contains("편의시설")) {
                        String rawConvenience = element.findElement(By.cssSelector("div.txt")).getText();
                        store.setConvenience("#" + rawConvenience.replace(", ", " #"));
                    }
                } catch (Exception e) {
                    loggingUtil.writeFile("Map A Convenience Error : {}" + driver.getCurrentUrl());
                    loggingUtil.writeFile(e.getMessage());
                }
            }

        } else if(mapType == MapType.B) {
            try {
                Thread.sleep(1000);
                driver.switchTo().frame(driver.findElement(By.id("entryIframe")));
                store.setPlaceName(driver.findElement(By.className("_3XamX")).getText());
                store.setCategory(driver.findElement(By.className("_3ocDE")).getText());
                try {
                    List<WebElement> elemUndefineds = driver.findElements(By.cssSelector("li._1M_Iz.undefined"));
                    for(WebElement elemUndefined : elemUndefineds) {
                        if(elemUndefined.getText().contains("편의")) {
                            String convinience = elemUndefined.getText().replace("편의\n", "");
                            if(!"".equals(convinience)) {
                                store.setConvenience("#" + convinience.replace(", ", " #"));
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    loggingUtil.writeFile("Map B Error1 : " + driver.getCurrentUrl());
                    e.printStackTrace();
                    database.getFails().add(driver.getCurrentUrl());
                }
            } catch (Exception e) {
                loggingUtil.writeFile("Map B Error2 : " + driver.getCurrentUrl());
                e.printStackTrace();
                database.getFails().add(driver.getCurrentUrl());
            }
        } else if(mapType == MapType.INVALID) {
            loggingUtil.writeFile("Invalid Map Type : Map URL : " + store.getMapUrl());
            database.getFails().add(driver.getCurrentUrl());
            return;
        }

        if(store.getPlaceName() == null || !getDetailInfo(driver, store, database)) {
            loggingUtil.writeFile("Invalid Place : " + store.getMapUrl());
            database.getFails().add(driver.getCurrentUrl());
            return;
        }
    }

    private boolean getDetailInfo(WebDriver driver, Store storeData, CrawlingResponse database) {
        String storeInfoUrl = "https://map.naver.com/v5/api/search?query=" + storeData.getPlaceName().replace(" ", "%20");
        try {
            driver.get(storeInfoUrl);

            String infoJson = driver.findElement(By.tagName("pre")).getText();
            ObjectMapper om = new ObjectMapper();
            StoreInfo info = om.readValue(infoJson, StoreInfo.class);
            List<Detail> details = info.getResult().getPlace().getList();

            List<Store> storeList = new ArrayList<>();
            for(Detail detail : details) {
                // 검색어와 이름이 동일한 업체만 노출
//                if(!detail.getName().equals(storeData.getPlaceName())) {
//                   continue;
//                }
                String tel = null;
                if(!StringUtils.isEmpty(detail.getVirtualTel())) {
                    tel = detail.getVirtualTel();
                } else if(!StringUtils.isEmpty(detail.getTel())) {
                    tel = detail.getTel();
                }
                Store store = new Store();
                store.setPlaceName(detail.getName());
                store.setCategory(storeData.getCategory());
                store.setMapUrl(storeData.getMapUrl());
                store.setId(detail.getId());
                store.setTel(tel);
                store.setBusinessHour(!StringUtils.isEmpty(detail.getBizhourInfo()) ? detail.getBizhourInfo() : null);
                store.setHomepage(!StringUtils.isEmpty(detail.getHomePage()) ? detail.getHomePage() : null);
                store.setDescription((detail.getMicroReview() != null && detail.getMicroReview().size() > 0) ? detail.getMicroReview().get(0) : null);
                store.setConvenience(storeData.getConvenience());
                store.setShortAddress((detail.getShortAddress() != null && detail.getShortAddress().size() > 0) ? detail.getShortAddress().get(0) : null);
                store.setAddress(!StringUtils.isEmpty(detail.getAddress()) ? detail.getAddress() : null);
                store.setRoadAddress(!StringUtils.isEmpty(detail.getRoadAddress()) ? detail.getRoadAddress() : null);
                store.setLatitude(detail.getX());
                store.setLongitude(detail.getY());
                storeList.add(store);
                loggingUtil.writeFile("[Store] " + store);
            }
            CrawlingChoices choices = CrawlingChoices.builder()
                    .placeName(storeData.getPlaceName())
                    .mapUrl(storeData.getMapUrl())
                    .choices(storeList)
                    .uuid(UUID.randomUUID().toString().replaceAll("-", ""))
                    .build();
            database.getStores().add(choices);
            return true;
        } catch (Exception e) {
            loggingUtil.writeFile("[Failed Get Store Info] API URL - " + storeInfoUrl);
            return false;
        }
    }

}
