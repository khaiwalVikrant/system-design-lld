package in_memory_store;

import java.util.HashMap;
import java.util.Map;

public interface EvictionStrategy<K, V> {
    void recordAccess(CacheNode<K, V> node);
    void recordAdd(CacheNode<K, V> node);
    void recordRemove(CacheNode<K, V> node);
    CacheNode<K, V> evict();
}
