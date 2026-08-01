package in_memory_store;

public class LRUEvictionStrategy<K, V> implements EvictionStrategy<K, V> {
    private final CacheNode<K, V> head;
    private final CacheNode<K, V> tail;

    public LRUEvictionStrategy() {
        head = new CacheNode<>(null, null, -1);
        tail = new CacheNode<>(null, null, -1);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void recordAccess(CacheNode<K, V> node) {
        removeNode(node);
        addAtHead(node);
    }

    @Override
    public void recordAdd(CacheNode<K, V> node) {
        addAtHead(node);
    }

    @Override
    public void recordRemove(CacheNode<K, V> node) {
        removeNode(node);
    }

    @Override
    public CacheNode<K, V> evict() {
        CacheNode<K, V> lru = tail.prev;
        if (lru == head) return null; // Empty list
        removeNode(lru);
        return lru;
    }

    private void addAtHead(CacheNode<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(CacheNode<K, V> node) {
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}
