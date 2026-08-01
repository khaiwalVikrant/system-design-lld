package in_memory_store;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KeyValueStore<K, V> {
    private final int capacity;
    private final ConcurrentHashMap<K, CacheNode<K, V>> store;
    private final EvictionStrategy<K, V> evictionStrategy;

    // Lock protecting structural mutations (eviction/LRU re-linking)
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    // Active TTL Cleanup Scheduler
    private final ScheduledExecutorService cleanupScheduler;

    public KeyValueStore(int capacity, EvictionStrategy<K, V> evictionStrategy) {
        this.capacity = capacity;
        this.store = new ConcurrentHashMap<>();
        this.evictionStrategy = evictionStrategy;

        // Background thread for active TTL purging every 5 seconds
        this.cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
        this.cleanupScheduler.scheduleAtFixedRate(this::activeTtlCleanup, 5, 5, TimeUnit.SECONDS);
    }

    public V get(K key) {
        CacheNode<K, V> node = store.get(key);
        if (node == null) return null;

        // Passive TTL Check
        if (node.isExpired()) {
            delete(key);
            return null;
        }

        // Record access safely under write lock (modifies list structure)
        rwLock.writeLock().lock();
        try {
            node.lastAccessed = System.currentTimeMillis();
            node.frequency++;
            evictionStrategy.recordAccess(node);
        } finally {
            rwLock.writeLock().unlock();
        }

        return node.value;
    }

    public void put(K key, V value, long ttlInMs) {
        rwLock.writeLock().lock();
        try {
            if (store.containsKey(key)) {
                CacheNode<K, V> node = store.get(key);
                node.value = value;
                node.expiryTime = (ttlInMs > 0) ? System.currentTimeMillis() + ttlInMs : -1;
                node.frequency++;
                node.lastAccessed = System.currentTimeMillis();
                evictionStrategy.recordAccess(node);
                return;
            }

            // Check capacity and evict if needed
            if (store.size() >= capacity) {
                CacheNode<K, V> evicted = evictionStrategy.evict();
                if (evicted != null) {
                    store.remove(evicted.key);
                }
            }

            CacheNode<K, V> newNode = new CacheNode<>(key, value, ttlInMs);
            store.put(key, newNode);
            evictionStrategy.recordAdd(newNode);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void put(K key, V value) {
        put(key, value, -1);
    }

    public void delete(K key) {
        rwLock.writeLock().lock();
        try {
            CacheNode<K, V> node = store.remove(key);
            if (node != null) {
                evictionStrategy.recordRemove(node);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // Active Expiration Task
    private void activeTtlCleanup() {
        for (K key : store.keySet()) {
            CacheNode<K, V> node = store.get(key);
            if (node != null && node.isExpired()) {
                delete(key);
            }
        }
    }

    public void shutdown() {
        cleanupScheduler.shutdown();
    }
}
