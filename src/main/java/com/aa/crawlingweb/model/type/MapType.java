package com.aa.crawlingweb.model.type;

public enum MapType {

    A("biz_name_area 가 있는 경우"),
    B("/entry/place/ 가 URL에 포함되는 경우"),
    INVALID("허용되지 않은 Map Type")
    ;

    private final String description;

    MapType(String description) {
        this.description = description;
    }

    public static MapType findMapType(String code) {
        return MapType.valueOf(code);
    }

}
