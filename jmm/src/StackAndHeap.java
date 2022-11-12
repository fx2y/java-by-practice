public class StackAndHeap {
    public static void main(String[] args) {
        int x = 0;
        Person joeBloggs = new Person("Joe Bloggs", 23);
        System.out.println(x);
        System.out.println(joeBloggs.toString());
    }

    interface Walkable {
    }

    static abstract class Human {
    }

    static class Person extends Human implements Walkable {
        private String name;
        private int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            String decoratedName = "My name is " + name + " and I am " + age + " years old.";
            return decoratedName;
        }
    }
}
