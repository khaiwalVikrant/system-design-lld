package in_memory_store;

import java.util.HashMap;
import java.util.Map;

class DoublyLinkedList<K, V> {
    final CacheNode<K, V> head;
    final CacheNode<K, V> tail;
    int size;

    public DoublyLinkedList() {
        head = new CacheNode<>(null, null, -1);
        tail = new CacheNode<>(null, null, -1);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    public void addFirst(CacheNode<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        size++;
    }

    public void remove(CacheNode<K, V> node) {
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
            size--;
        }
    }

    public CacheNode<K, V> removeLast() {
        if (size == 0) return null;
        CacheNode<K, V> lruNode = tail.prev;
        remove(lruNode);
        return lruNode;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
