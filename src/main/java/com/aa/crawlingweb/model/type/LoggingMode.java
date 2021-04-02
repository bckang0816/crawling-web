package com.aa.crawlingweb.model.type;

public enum LoggingMode {

    NEW("신규"),
    APPEND("추가")
    ;

    private final String code;

    LoggingMode(String code) {
        this.code = code;
    }

    public static LoggingMode findLoggingMode(String code) {
        return LoggingMode.valueOf(code);
    }

}
