import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int size;
    private int capacity;
    private int head;
    private int tail;
    private Item[] items;

    // construct an empty randomized queue
    public RandomizedQueue() {
        size = 0;
        capacity = 0;
        head = 0;
        tail = 0;
        items = (Item[]) new Object[capacity];
    }

    private void resize(int newCapacity) {
        Item[] newItems = (Item[]) new Object[newCapacity];
        int i = 0;
        for (int j = 0; j < size; ++j)
            newItems[i++] = items[(head + j) % capacity];
        items = newItems;
        capacity = newCapacity;
        head = 0;
        tail = head + size;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("The items in RandomizedQueue cannot be null");
        if (size == capacity)
            resize(capacity * 2 + 1);
        ++size;
        int other = (head + StdRandom.uniform(size)) % capacity;
        items[tail] = items[other];
        items[other] = item;
        tail = (tail + 1) % capacity;
    }

    // remove and return a random item
    public Item dequeue() {
        if (size == 0)
            throw new NoSuchElementException("The RandomizedQueue is empty");
        if (size <= capacity / 4)
            resize(capacity / 2);
        Item item = items[head];
        items[head] = null;
        head = (head + 1) % capacity;
        --size;
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (size == 0)
            throw new NoSuchElementException("The RandomizedQueue is empty");
        return items[(head + StdRandom.uniform(size)) % capacity];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator(size);
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private final Item[] iteratorItems;
        private int current;

        public RandomizedQueueIterator(int size) {
            iteratorItems = (Item[]) new Object[size];
            current = 0;
            for (int i = 0; i < size; ++i)
                iteratorItems[i] = items[(head + i) % capacity];
            StdRandom.shuffle(iteratorItems);
        }

        public boolean hasNext() {
            return !(current == iteratorItems.length);
        }

        public Item next() {
            if (current == iteratorItems.length)
                throw new NoSuchElementException("The RandomizedQueue is empty");
            return iteratorItems[current++];
        }

        public void remove() {
            throw new UnsupportedOperationException("The remove() method is not supported");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<>();
        StdOut.println("Enqueing:");
        for (int i = 0; i < 20; ++i) {
            StdOut.println("Enqueue " + i);
            rq.enqueue(i);
        }
        StdOut.println("The size is " + rq.size());
        StdOut.println("Sampling:");
        for (int i = 0; i < 5; ++i)
            StdOut.println("Sample " + rq.sample());
        StdOut.println("Dequeing:");
        for (int i = 0; i < 20; ++i)
            StdOut.println("Dequeue " + rq.dequeue());
        StdOut.println("The size is " + rq.size());
        for (int i = 0; i < 10; ++i) {
            StdOut.println("Enqueue " + i);
            rq.enqueue(i);
        }
        StdOut.println("Iterating:");
        for (int i : rq)
            StdOut.println("Got " + i);
    }
}
