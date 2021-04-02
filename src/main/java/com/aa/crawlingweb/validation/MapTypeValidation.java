package com.aa.crawlingweb.validation;

import com.aa.crawlingweb.model.type.MapType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MapTypeValidation {

    public MapType checkMapType(WebDriver driver) {
        if(isTypeA(driver)) return MapType.A;
        if(isTypeB(driver)) return MapType.B;
        return MapType.INVALID;
    }

    public boolean isTypeA(WebDriver driver) {
        try {
            driver.findElement(By.className("biz_name_area"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTypeB(WebDriver driver) {
        try {
            if(driver.getCurrentUrl().contains("/entry/place")) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
