/**
 * A simple LinkedList supply for the generic hashMap
 *
 * @param <k> type parameter
 * @param <v> type parameter
 * @author Maiqi Ding, maiqid@andrew.cmu.edu
 * @version Nov 11, 2019
 */

public class MapLinkedList<k, v> {

    /*class*/
    public class Node<k, v> {
        k key;
        v value;
        Node next;

        public Node(k key, v value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<k, v> head;
    private int size;

    public MapLinkedList() {
        head = null;
        size = 0;
    }

    public void add(k key, v value) {
        if (head == null) {
            head = new Node<>(key, value);
        } else {
            Node<k, v> node = new Node<>(key, value);
            head.next = node;
            node.next = null;
        }
        size += 1;
    }

    public boolean contains(k key) {
        for (Node cursor = head; cursor != null; cursor = cursor.next) {
            if (cursor.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public v getValue(k key) {
        for (Node cursor = head; cursor != null; cursor = cursor.next) {
            if (cursor.key.equals(key)) {
                return (v) cursor.value;
            }
        }
        return null;
    }

    public void changeValue(k key, v value) {
        for (Node cursor = head; cursor != null; cursor = cursor.next) {
            if (cursor.key.equals(key)) {
                cursor.value = value;
            }
        }
    }


    public int getSize() {
        return size;
    }

    public static void main(String[] args) {
        MapLinkedList<String, Integer> list = new MapLinkedList<>();

        list.add("John", 1);
        System.out.println(list.contains("May"));
        System.out.println(list.contains("John"));
        System.out.println(list.getValue("John"));
        list.changeValue("John", 2);
        System.out.println(list.getValue("John"));
    }

}
