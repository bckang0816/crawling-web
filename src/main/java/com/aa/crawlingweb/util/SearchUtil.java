package com.aa.crawlingweb.util;

import com.aa.crawlingweb.model.naver.Detail;
import com.aa.crawlingweb.model.naver.StoreInfo;
import com.aa.crawlingweb.model.response.CrawlingChoices;
import com.aa.crawlingweb.model.response.CrawlingResponse;
import com.aa.crawlingweb.model.store.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

    private static final String NAVER_URL_PREFIX = "https://map.naver.com/v5/entry/place/";

    public CrawlingResponse getMapInfo(List<Long> ids, WebDriver driver) {
        CrawlingResponse database = new CrawlingResponse();
        for (long id : ids) {
            driver.get(NAVER_URL_PREFIX + id);

            getStoreInfo(driver, database, id);
        }
        return database;
    }

    private void getStoreInfo(WebDriver driver, CrawlingResponse database, long id) {
        Store store = new Store();
        store.setMapUrl(driver.getCurrentUrl());
        try {
            Thread.sleep(1000);
            driver.switchTo().frame(driver.findElement(By.id("entryIframe")));
            store.setId(id);
            store.setPlaceName(driver.findElement(By.className("_3XamX")).getText());
            store.setCategory(driver.findElement(By.className("_3ocDE")).getText());
            try {
                driver.findElement(By.cssSelector("li._1M_Iz._3__3i .WoYOw")).click();
                store.setDescription(driver.findElement(By.cssSelector("li._1M_Iz._3__3i .WoYOw")).getText());
            } catch (NoSuchElementException e) {
            }
            try {
                store.setAddress(driver.findElement(By.className("_2yqUQ")).getText());
            } catch (NoSuchElementException e) {
            }

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

                if(store.getPlaceName() == null || !getDetailInfo(driver, store, database)) {
                    loggingUtil.writeFile("Invalid Place : " + store.getMapUrl());
                    database.getFails().add(id);
                    return;
                }
            } catch (Exception e) {
                loggingUtil.writeFile("Error1 : " + driver.getCurrentUrl());
                e.printStackTrace();
                database.getFails().add(id);
            }
        } catch (Exception e) {
            loggingUtil.writeFile("Error2 : " + driver.getCurrentUrl());
            e.printStackTrace();
            database.getFails().add(id);
        }
    }

    private boolean getDetailInfo(WebDriver driver, Store storeData, CrawlingResponse database) {
        String storeInfoUrl = "https://map.naver.com/v5/api/search?query=" + storeData.getPlaceName().replace(" ", "%20");

        List<Store> storeList = getStoreData(driver, storeData, storeInfoUrl);

        if(storeList.size() == 0) {
            storeList.addAll(getDetailInfoByStoreLocation(driver, storeData));
        }
        CrawlingChoices choices = CrawlingChoices.builder()
                .placeName(storeData.getPlaceName())
                .mapUrl(storeData.getMapUrl())
                .choices(storeList)
                .uuid(UUID.randomUUID().toString().replaceAll("-", ""))
                .build();
        database.getStores().add(choices);
        return true;
    }

    public List<Store> getDetailInfoByStoreLocation(WebDriver driver, Store storeData) {
        String searchName = (storeData.getPlaceName() + " " + storeData.getAddress()).replace(" ", "%20");
        String storeInfoUrl = "https://map.naver.com/v5/api/search?query=" + searchName;
        return getStoreData(driver, storeData, storeInfoUrl);
    }

    public List<Store> getStoreData(WebDriver driver, Store storeData, String storeInfoUrl) {
        try {
            driver.get(storeInfoUrl);

            String infoJson = driver.findElement(By.tagName("pre")).getText();
            ObjectMapper om = new ObjectMapper();
            StoreInfo info = om.readValue(infoJson, StoreInfo.class);
            List<Detail> details = info.getResult().getPlace().getList();

            List<Store> storeList = new ArrayList<>();
            for(Detail detail : details) {
                // 아이디가 동일한 업체 선택
                if(detail.getId() != storeData.getId()) {
                    continue;
                }
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
                if(StringUtils.isEmpty(storeData.getDescription())) {
                    store.setDescription((detail.getMicroReview() != null && detail.getMicroReview().size() > 0) ? detail.getMicroReview().get(0) : null);
                } else {
                    store.setDescription(storeData.getDescription());
                }
                store.setConvenience(storeData.getConvenience());
                store.setShortAddress((detail.getShortAddress() != null && detail.getShortAddress().size() > 0) ? detail.getShortAddress().get(0) : null);
                store.setAddress(!StringUtils.isEmpty(detail.getAddress()) ? detail.getAddress() : null);
                store.setRoadAddress(!StringUtils.isEmpty(detail.getRoadAddress()) ? detail.getRoadAddress() : null);
                store.setLatitude(detail.getY());
                store.setLongitude(detail.getX());
                storeList.add(store);
                loggingUtil.writeFile("[Store] " + store);
            }
            return storeList;
        } catch (Exception e) {
            loggingUtil.writeFile("[Failed Get Store Info] API URL - " + storeInfoUrl);
            return new ArrayList<>();
        }
    }

}
