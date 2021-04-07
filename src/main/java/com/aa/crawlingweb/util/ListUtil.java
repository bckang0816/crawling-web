package com.aa.crawlingweb.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListUtil {

    public static void distinct(List<String> urls) {
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            if (!resultList.contains(urls.get(i))) {
                resultList.add(urls.get(i));
            }
        }
        urls = new ArrayList<>(resultList);
    }
}
