package in_memory_store;

public class CacheNode<K, V> {
    final K key;
    V value;
    long expiryTime; // Timestamp in epoch millis (-1 if no TTL)

    // Strategy-specific metrics
    int frequency; // For LFU
    long lastAccessed; // For LRU tie-breaking

    // Pointers for Doubly Linked List
    CacheNode<K, V> prev;
    CacheNode<K, V> next;

    public CacheNode(K key, V value, long ttlInMs) {
        this.key = key;
        this.value = value;
        this.expiryTime = (ttlInMs > 0) ? System.currentTimeMillis() + ttlInMs : -1;
        this.frequency = 1;
        this.lastAccessed = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return expiryTime != -1 && System.currentTimeMillis() > expiryTime;
    }
}
