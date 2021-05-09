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

    public static void distinct(List<Long> ids) {
        List<Long> resultList = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            if (!resultList.contains(ids.get(i))) {
                resultList.add(ids.get(i));
            }
        }
        ids = new ArrayList<>(resultList);
    }
}
