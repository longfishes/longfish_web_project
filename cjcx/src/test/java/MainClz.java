import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MainClz {

    public void func1() {
        System.out.println();
        System.out.println();
    }

    public MainClz() {
        System.out.println("父类构造函数");
    }

    static class son extends MainClz {
        public son() {
            System.out.println("子类构造函数");
        }
    }

    public static void main(String[] args) {
        // 实例化一个子类对象
        son obj = new son();
        // 获取子类的类对象
        Class<? extends son> clz = obj.getClass();
        // 获取子类中的所有构造器
        Constructor<?>[] constructors = clz.getConstructors();
        System.out.println("子类的构造器数组：" + Arrays.toString(constructors));
        // 获取子类的构造器数组中第一个构造器的返回值类型
        AnnotatedType returnType = constructors[0].getAnnotatedReturnType();
        // 打印返回值类型
        System.out.println("子类构造器的返回值类型：" + returnType); //MainClz$son 一个类，即this

        // 获取父类对象中的第一个声明的函数
        Method method = clz.getSuperclass().getMethods()[0];
        // 打印这个函数
        System.out.println(method); //public void MainClz.func1()
        // 获取并打印这个函数的返回值类型
        System.out.println(method.getReturnType()); // void
    }
}
