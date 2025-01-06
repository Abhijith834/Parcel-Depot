package model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

/**
 * A queue implementation for storing customers.
 */
public class QueueOfCustomers {
    private final Queue<Customer> queue;

    public QueueOfCustomers() {
        queue = new LinkedList<>();
    }

    public void enqueue(Customer c) {
        queue.offer(c);
    }

    public Customer dequeue() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(queue);
    }
}
