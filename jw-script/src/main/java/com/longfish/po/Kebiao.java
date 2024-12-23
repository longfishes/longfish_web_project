package com.longfish.po;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Kebiao {

    private static final Map<String, Boolean> kebiao = new HashMap<>();

    static {
        for (int i = 0; i < 8; i++) {
            kebiao.put((i + 1) + ",1-2", true);
            kebiao.put((i + 1) + ",3-4", true);
            kebiao.put((i + 1) + ",5-6", true);
            kebiao.put((i + 1) + ",7-8", true);
        }
    }

    public static void parseAndPut(String origin) {
        int iindex1 = origin.indexOf("星期一");
        int iindex2 = origin.indexOf("星期二");
        int iindex3 = origin.indexOf("星期三");
        int iindex4 = origin.indexOf("星期四");
        int iindex5 = origin.indexOf("星期五");
        int iindex6 = origin.indexOf("星期六");
        int iindex7 = origin.indexOf("星期日");
        if (iindex7 == -1) iindex7 = origin.indexOf("星期天");
        if (iindex1 != -1) {
            kebiao.put(1 + "," + origin.substring(iindex1 + 4, iindex1 + 7), false);
        }
        if (iindex2 != -1) {
            kebiao.put(2 + "," + origin.substring(iindex2 + 4, iindex2 + 7), false);
        }
        if (iindex3 != -1) {
            kebiao.put(3 + "," + origin.substring(iindex3 + 4, iindex3 + 7), false);
        }
        if (iindex4 != -1) {
            kebiao.put(4 + "," + origin.substring(iindex4 + 4, iindex4 + 7), false);
        }
        if (iindex5 != -1) {
            kebiao.put(5 + "," + origin.substring(iindex5 + 4, iindex5 + 7), false);
        }
        if (iindex6 != -1) {
            kebiao.put(6 + "," + origin.substring(iindex6 + 4, iindex6 + 7), false);
        }
        if (iindex7 != -1) {
            kebiao.put(7 + "," + origin.substring(iindex7 + 4, iindex7 + 7), false);
        }
    }

    public static boolean available(String sksj) {
        Boolean b = kebiao.get(sksj);
        if (b == null) {
            throw new RuntimeException("上课时间格式错误！");
        }
        return b;
    }

    public static void set(String sksj) {
        Boolean b = kebiao.get(sksj);
        if (b == null) {
            throw new RuntimeException("上课时间格式错误！");
        }
        kebiao.put(sksj, false);
    }
}
