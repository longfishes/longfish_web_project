package com.longfish;

import com.longfish.po.Kebiao;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationTest {

    @Test
    public void testInjectTime() throws InterruptedException {

        while (true) {
            LocalDateTime targetTime = LocalDateTime.of(2024, 9, 6, 13, 0, 0);
            if (LocalDateTime.now().isAfter(targetTime)) break;
            Thread.sleep(10);
        }
    }

    // find 32 string likes 20CA34901CE49E53E063778926D2B4E8
    @Test
    public void testFindXkkId() {
        String filePath = "E:\\Administrator\\longfish_web_project\\jw-script\\src\\main\\resources\\test-html-text.txt";
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String payload = content.toString();

        String regex = "[A-Z0-9]{32}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(payload);

        String groupId = "10";
        String keywords = "选修";

        while (matcher.find()) {
            String xkkId = matcher.group();
            System.out.println("找到xkkId: " + xkkId);
            String kklxdm = payload.substring(matcher.start() - 5, matcher.start() - 3);
            System.out.println("类别编号为：" + kklxdm);
            if (kklxdm.equals(groupId)) {
                if (Pattern.compile(keywords).matcher(payload.substring(matcher.end(), matcher.end() + 100)).find()) {
                    System.out.println("=========== find target ============\n" + xkkId);
                    System.out.println("====================================");
                }
            }
        }
    }

    @Test
    void test() {
        System.out.println("hello junit");
    }

    @Test
    public void test2() {
        String sksj = "1,1-2";
        System.out.println(Kebiao.available(sksj));
        Kebiao.set(sksj);
        System.out.println(Kebiao.available(sksj));
    }

    @Test
    public void test3() {
        Kebiao.parseAndPut("星期二第1-2节fdijfid星期六第7-8节<br\\/>星期四第7-8节{6-18周}");
        System.out.println(Kebiao.available("2,1-2"));
        System.out.println(Kebiao.available("6,7-8"));
    }
}
