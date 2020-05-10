/**
 * A simple generic type hashMap implementation based on array of linked list
 * which supports basic operations including put, get, containsKey and size.
 *
 * @author Maiqi Ding, maiqid@andrew.cmu.edu
 * @version Nov 21, 2019
 */


public class hashMap<k, v> {

    private MapLinkedList<k, v>[] buckets;
    private int size;

    /**
     * Constructor, initialize an array of LinkedList
     *
     * @param n number of estimated keys
     */
    public hashMap(int n) {
        buckets = (MapLinkedList<k, v>[]) new MapLinkedList[n];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new MapLinkedList<>();
        }
        size = 0;
    }

    /**
     * if the key is mapped
     *
     * @param key
     * @return
     */
    public boolean containsKey(k key) {
        int pos = findPos(key);
        return buckets[pos].contains(key);
    }

    /**
     * map key -> value
     *
     * @param key   key
     * @param value value
     */
    public void put(k key, v value) {

        int pos = findPos(key);
        if (!containsKey(key)) {
            size += 1;
            buckets[pos].add(key, value);
        } else {
            buckets[pos].changeValue(key, value);
        }

    }

    /**
     * hash the key and get the insert pos;
     *
     * @param key
     * @return
     */
    public int findPos(k key) {
        int returnItem;
        returnItem = Math.abs(key.hashCode()) % buckets.length;
        return returnItem;
    }

    /**
     * VALUE last mapped to by SOMEKEY
     *
     * @param key
     * @return value last mapped to key
     */
    public v get(k key) {
        int pos = findPos(key);
        return buckets[pos].getValue(key);
    }

    /**
     * Return the total keys in the map
     *
     * @return size
     */
    public int size() {
        return size;
    }


    public static void main(String[] args) {
        hashMap<String, Integer> map = new hashMap<>(6);

        map.containsKey("john");
        map.put("john", 1);
        map.put("may", 2);
        map.put("amy", 3);
        map.put("chris", 1);
        map.put("john", 4);

        System.out.println(map.get("john"));
        System.out.println(map.get("yue"));
    }
}
