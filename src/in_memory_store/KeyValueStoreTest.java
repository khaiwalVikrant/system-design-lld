package in_memory_store;

public class KeyValueStoreTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. Testing LRU Eviction (Capacity = 3) ===");

        EvictionStrategy<String, String> lruStrategy = new LRUEvictionStrategy<>();
        KeyValueStore<String, String> store = new KeyValueStore<>(3, lruStrategy);

        // Fill capacity
        store.put("A", "Value A");
        store.put("B", "Value B");
        store.put("C", "Value C");

        System.out.println("Get A: " + store.get("A")); // Accessing A makes B the Least Recently Used

        // Insert 'D' -> Should trigger eviction of 'B'
        store.put("D", "Value D");

        System.out.println("Get B (Should be null - evicted): " + store.get("B"));
        System.out.println("Get A (Should exist): " + store.get("A"));
        System.out.println("Get C (Should exist): " + store.get("C"));
        System.out.println("Get D (Should exist): " + store.get("D"));

        System.out.println("\n=== 2. Testing TTL Expiration ===");

        // Insert key 'temp' with 2000ms (2 seconds) TTL
        store.put("tempKey", "tempValue", 2000);

        System.out.println("Immediate Get tempKey: " + store.get("tempKey"));

        System.out.println("Waiting 2.5 seconds for TTL to expire...");
        Thread.sleep(2500);

        // Passive eviction check triggers here upon access
        System.out.println("Get tempKey after 2.5s (Should be null): " + store.get("tempKey"));

        // Gracefully shut down background TTL scheduler thread
        store.shutdown();
        System.out.println("\nStore shutdown successfully.");
    }
}
