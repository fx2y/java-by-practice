package cyou.ithan.nullobject;

import lombok.extern.slf4j.Slf4j;

public class App {
    public static void main(String[] args) {
        var root = new NodeImpl("1",
                new NodeImpl("11",
                        new NodeImpl("111", NullNode.instance, NullNode.instance),
                        NullNode.instance),
                new NodeImpl("12",
                        NullNode.instance,
                        new NodeImpl("122", NullNode.instance, NullNode.instance)));
        root.walk();
    }

    interface Node {
        int treeSize();

        void walk();
    }

    @Slf4j
    record NodeImpl(String name, Node left, Node right) implements Node {
        @Override
        public int treeSize() {
            return 1 + left().treeSize() + right().treeSize();
        }

        @Override
        public void walk() {
            log.info(name);
            if (left.treeSize() > 0)
                left.walk();
            if (right.treeSize() > 0)
                right.walk();
        }
    }

    record NullNode() implements Node {
        static final NullNode instance = new NullNode();

        @Override
        public int treeSize() {
            return 0;
        }

        @Override
        public void walk() {
        }
    }
}
