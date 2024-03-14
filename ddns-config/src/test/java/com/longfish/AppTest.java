package com.longfish;

import com.longfish.util.ConfigReader;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;

import static com.longfish.constant.ConfigConstant.*;

public class AppTest {

    @Test
    public void testIO1() {
        File f = new File("src/main/resources/app.yml");
        System.out.println(f.getAbsolutePath());
        System.out.println(f.exists());
    }

    @Test
    public void testIO2() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(new FileInputStream("src/main/resources/app.yml"));
        System.out.println(data);

        System.out.println(data.get(tencent));
        System.out.println(((Map) data.get(tencent)).get(secretId));
        System.out.println(((Map) data.get(tencent)).get(secretKey));

        System.out.println(((Map) ((Map) data.get(customer)).get(config)).get(recordId));
        System.out.println(((Map) ((Map) data.get(customer)).get(config)).get(domain));
    }

    @Test
    public void testIO3() {
        Arrays.asList(
                ConfigReader.getSecretId(), ConfigReader.getSecretKey(),
                ConfigReader.getRecordId(), ConfigReader.getDomain()).forEach(System.out::println);
    }

    @Test
    public void test1() {
        System.out.println("helloworld");
    }
}
