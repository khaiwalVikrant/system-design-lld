package in_memory_store;

import java.util.HashMap;
import java.util.Map;

public class LFUEvictionStrategy<K, V> implements EvictionStrategy<K, V> {
    private final Map<Integer, DoublyLinkedList<K, V>> freqToList;
    private int minFrequency;

    public LFUEvictionStrategy() {
        this.freqToList = new HashMap<>();
        this.minFrequency = 0;
    }

    @Override
    public void recordAccess(CacheNode<K, V> node) {
        int oldFreq = node.frequency;
        int newFreq = oldFreq + 1;
        node.frequency = newFreq;

        // 1. Remove node from current frequency bucket
        DoublyLinkedList<K, V> oldList = freqToList.get(oldFreq);
        if (oldList != null) {
            oldList.remove(node);

            // If minimum frequency list becomes empty, increment minFrequency
            if (oldList.isEmpty() && oldFreq == minFrequency) {
                minFrequency++;
            }
        }

        // 2. Add node to new frequency bucket
        freqToList.computeIfAbsent(newFreq, k -> new DoublyLinkedList<>()).addFirst(node);
    }

    @Override
    public void recordAdd(CacheNode<K, V> node) {
        // New node always starts with frequency = 1
        node.frequency = 1;
        minFrequency = 1; // Reset minFrequency to 1 for the new element

        freqToList.computeIfAbsent(1, k -> new DoublyLinkedList<>()).addFirst(node);
    }

    @Override
    public void recordRemove(CacheNode<K, V> node) {
        int freq = node.frequency;
        DoublyLinkedList<K, V> list = freqToList.get(freq);
        if (list != null) {
            list.remove(node);
        }
    }

    @Override
    public CacheNode<K, V> evict() {
        // Get the list corresponding to the minimum frequency
        DoublyLinkedList<K, V> minFreqList = freqToList.get(minFrequency);
        if (minFreqList == null || minFreqList.isEmpty()) {
            return null;
        }

        // Evict the least recently used node from the minimum frequency list
        return minFreqList.removeLast();
    }
}