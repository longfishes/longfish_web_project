import com.longfish.Detail;
import org.junit.jupiter.api.Test;

public class ParseTest {

    @Test
    public void test1() {
        System.out.println(Detail.parse("【 平时 】 40% 93.60 【 期末 】 60% 91 【 总评 】 0% 92"));
    }

    @Test
    public void test2() {
        System.out.println(System.currentTimeMillis());
    }
}
