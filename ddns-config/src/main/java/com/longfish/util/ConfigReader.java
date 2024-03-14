package com.longfish.util;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import static com.longfish.constant.ConfigConstant.*;

public class ConfigReader {

    private static final Yaml yaml = new Yaml();

    private static Map<String, Object> data = null;

    static {
        try {
            data = yaml.load(new FileInputStream("E:\\Administrator\\longfish_web_project\\ddns-config\\src\\main\\resources\\app.yml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getSecretId() {
        return String.valueOf (((Map) data.get(tencent)).get(secretId));
    }

    public static String getSecretKey() {
        return String.valueOf(((Map) data.get(tencent)).get(secretKey));
    }

    public static String getRecordId() {
        return String.valueOf(((Map) ((Map) data.get(customer)).get(config)).get(recordId));
    }

    public static String getDomain() {
        return String.valueOf(((Map) ((Map) data.get(customer)).get(config)).get(domain));
    }
}
