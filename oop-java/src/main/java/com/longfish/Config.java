package com.longfish;

public class Config {

    private static final String SERVER_ADDRESS = "r.longfish.site";

    private static final String SERVER_API_BASE = "http://r.longfish.site:7799";

    public static String getServerAddress() {
        return SERVER_ADDRESS;
    }

    public static String getServerApiBase() {
        return SERVER_API_BASE;
    }
}
