import java.util.ArrayList;
import java.util.List;

public class OutOfMemoryExample {
    public static void main(String[] args) {
        List<Person> list = new ArrayList<>();
        int i = 1;
        while (true) {
            Person p = new Person("John");
            list.add(p);
            if (i == 1000) {
                list = new ArrayList<>();
                i = 0;
            }
            i++;
        }
    }

    static class Person {
        private String name;

        Person(String name) {
            this.name = name;
        }
    }
}
