import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private class Node {
        private final Item item;
        private Node next;
        private Node prev;

        public Node(Item item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    // construct an empty deque
    public Deque() {
        head = null;
        tail = null;
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null)
            throw new IllegalArgumentException("The added element cannot be null");
        Node first = new Node(item, null, head);
        if (head != null)
            head.prev = first;
        else
            tail = first;
        head = first;
        ++size;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null)
            throw new IllegalArgumentException("The added element cannot be null");
        Node last = new Node(item, tail, null);
        if (tail != null)
            tail.next = last;
        else
            head = last;
        tail = last;
        ++size;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (size == 0)
            throw new NoSuchElementException("The deque is empty");
        --size;
        Item item = head.item;
        if (size == 0) {
            head = null;
            tail = null;
        }
        else {
            head = head.next;
            head.prev = null;
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (size == 0)
            throw new NoSuchElementException("The deque is empty");
        --size;
        Item item = tail.item;
        if (size == 0) {
            head = null;
            tail = null;
        }
        else {
            tail = tail.prev;
            tail.next = null;
        }
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {

        private Node current = head;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (current == null)
                throw new NoSuchElementException("The deque is empty");
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("The remove() method is not supported");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> dq = new Deque<>();
        for (int i = 0; i < 10; ++i) {
            StdOut.println("add first " + i);
            dq.addFirst(i);
        }
        StdOut.println("Size is " + dq.size);
        StdOut.print("The items are: ");
        for (int i : dq)
            StdOut.print(i + " ");
        StdOut.println();
        for (int i = 10; i < 20; ++i) {
            StdOut.println("add last " + i);
            dq.addLast(i);
        }
        StdOut.println("Size is " + dq.size);
        StdOut.print("The items are: ");
        for (int i : dq)
            StdOut.print(i + " ");
        StdOut.println();
        for (int i = 0; i < 5; ++i)
            StdOut.println("remove first " + dq.removeFirst());
        StdOut.println("Size is " + dq.size);
        StdOut.print("The items are: ");
        for (int i : dq)
            StdOut.print(i + " ");
        StdOut.println();
        for (int i = 0; i < 5; ++i)
            StdOut.println("remove last " + dq.removeLast());
        StdOut.println("Size is " + dq.size);
        StdOut.print("The items are: ");
        for (int i : dq)
            StdOut.print(i + " ");
        StdOut.println();
        while (!dq.isEmpty())
            StdOut.println("remove last " + dq.removeLast());
        for (int i = 0; i < 10; ++i) {
            StdOut.println("add first " + i);
            dq.addFirst(i);
        }
        StdOut.println("Size is " + dq.size);
        StdOut.print("The items are: ");
        for (int i : dq)
            StdOut.print(i + " ");
        StdOut.println();
        while (!dq.isEmpty())
            StdOut.println("remove first " + dq.removeFirst());
    }
}
