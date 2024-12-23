package com.longfish;

import com.longfish.po.Result;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<String> kchIds = Arrays.asList("70090471", "70090191", "70092171", "70091941", "70070204");
        GccUtil.getCookie();

//        GccUtil.preCheckCookie();
//        System.out.println("校验成功！");

        int timer = 500;
        while (true) {
            LocalDateTime targetTime = LocalDateTime.of(2024, 9, 6, 13, 0, 0);
            if (LocalDateTime.now().isAfter(targetTime)) {
                System.out.println("选课时间已到！正在进入...");
                break;
            }
            Thread.sleep(10);
            if (timer == 500) {
                System.out.println("距离开始还剩 " + (targetTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() -
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()) + " 秒" +
                        "，请不要关闭窗口");
                timer = 0;
            }
            timer++;
        }

        String xkkzId = GccUtil.findXkkzId("10", "选修");

        while (xkkzId == null) {
            xkkzId = GccUtil.findXkkzId("10", "选修");
            System.out.println("选课还未开放！");
        }
        for (int i = 0; i < 5; i++) {
            System.out.println("已成功加载！");
        }

        while (true) {
            try {
                for (String kchId : kchIds) {
                    List<String> selectList = GccUtil.getDetail(kchId, "10", xkkzId);
                    assert selectList != null;
                    for (String s : selectList) {
                        Result selectR = GccUtil.select(s, kchId, "10", xkkzId);
                        System.out.println(selectR);
                    }
                }
            } catch (Exception ignore) {}
        }

    }
}
