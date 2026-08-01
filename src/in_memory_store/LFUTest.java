package in_memory_store;

public class LFUTest {
    public static void main(String[] args) {
        // Instantiating store with LFU Strategy
        EvictionStrategy<String, String> lfuStrategy = new LFUEvictionStrategy<>();
        KeyValueStore<String, String> store = new KeyValueStore<>(2, lfuStrategy);

        store.put("Key1", "Val1");
        store.put("Key2", "Val2");

        // Access Key1 twice to increase its frequency to 3
        store.get("Key1");
        store.get("Key1");

        // Insert Key3 -> Store is full. Key2 (freq=1) evicted instead of Key1 (freq=3)
        store.put("Key3", "Val3");

        System.out.println("Key1: " + store.get("Key1")); // "Val1"
        System.out.println("Key2: " + store.get("Key2")); // null (Evicted)
        System.out.println("Key3: " + store.get("Key3")); // "Val3"
    }
}
