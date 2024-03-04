package com.longfish;

import lombok.Builder;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class Detail {

    private String name;

    private Double percent;

    private Double score;

    public static List<Detail> parse(String detail){
        List<Detail> details = new ArrayList<>();
        List<Integer> namePos = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<Double> percent = new ArrayList<>();
        List<Double> score = new ArrayList<>();

        for (int i = 0; i < detail.length(); i++) {
            char c = detail.charAt(i);
            if (c == '【' || c == '】') namePos.add(i);
            if (c == '%'){
                List<Character> per = new ArrayList<>();
                int cnt = i - 1;
                while (detail.charAt(cnt) != ' ') {
                    per.add(detail.charAt(cnt));
                    cnt--;
                }
                Collections.reverse(per);
                StringBuilder sb = new StringBuilder();
                for (Character character : per) {
                    sb.append(character);
                }
                double item = Double.parseDouble(sb.toString());
                DecimalFormat df = new DecimalFormat("#.000000");
                percent.add(Double.parseDouble(df.format(item * 0.01)));

                StringBuilder sor = new StringBuilder();
                cnt = i + 2;
                while (detail.length() > cnt && detail.charAt(cnt) != ' ') {
                    sor.append(detail.charAt(cnt));
                    cnt++;
                }
                score.add(Double.parseDouble(sor.toString()));
            }
        }
        for (int i = 0; i < namePos.size() - 1; i += 2) {
            name.add(detail.substring(namePos.get(i) + 1, namePos.get(i + 1)).trim());
        }
        for (int i = 0; i < name.size(); i++) {
            details.add(builder()
                    .name(name.get(i))
                    .percent(percent.get(i))
                    .score(score.get(i))
                    .build());
        }

        return details;
    }
}
