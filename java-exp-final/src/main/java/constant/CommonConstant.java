package constant;

import java.util.List;
import java.util.Vector;

public interface CommonConstant {

    String Title = "学生管理系统";

    String Filling = "*待填写*";

    Vector<String> Header = new Vector<>(List.of("学号", "姓名", "年龄", "班级"));

    Vector<Vector<String>> DefaultData = new Vector<>(List.of(
            new Vector<>(List.of("114514", "张三", "20", "计算机科学与技术")),
            new Vector<>(List.of("415411", "李四", "21", "软件工程")),
            new Vector<>(List.of("456789", "王五", "22", "信息安全")),
            new Vector<>(List.of("123456", "赵六", "23", "网络工程")),
            new Vector<>(List.of("654321", "孙七", "22", "人工智能")),
            new Vector<>(List.of("789123", "周八", "24", "数据科学")),
            new Vector<>(List.of("321987", "吴九", "21", "物联网工程")),
            new Vector<>(List.of("987654", "郑十", "20", "电子信息工程")),
            new Vector<>(List.of("135792", "王一", "23", "自动化")),
            new Vector<>(List.of("246813", "李二", "22", "软件工程"))
    ));

    String FilePath = ".\\data.txt";

}
