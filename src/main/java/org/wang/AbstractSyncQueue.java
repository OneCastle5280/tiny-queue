package org.wang;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wangjiabao
 */
public abstract class AbstractSyncQueue<T> implements TinyQueue<T> {

    /**
     * node
     */
    static class Node <T> {
        /**
         * data
         */
        private AtomicReference<T> value;

        /**
         * point to the next node
         */
        private AtomicReference<Node<T>> next;
    }

    /**
     * current length of the queue
     */
    private AtomicInteger curLength;
    /**
     * the max length of the queue
     */
    private int maxLength;
    /**
     * point to the head node
     */
    private AtomicReference<Node<T>> head;
    /**
     * point to the tail node
     */
    private AtomicReference<Node<T>> tail;


    protected abstract boolean allowPoll();

    protected abstract boolean allowAdd();

    protected AbstractSyncQueue (Integer maxLength) {
        this.maxLength = maxLength;
        this.head = null;
        this.tail = null;
    }

    private void addWhenFist(T value) {
        Node<T> node = new Node<>();
        node.value = value;

        this.head.next = node;
        this.tail.next = node;
        curLength ++;
    }

    private void doAdd(T value) {
        Node<T> node = new Node<>();
        node.value = value;
        // add node to queue tail
        Node<T> originalLastNode = tail.next;
        originalLastNode.next = node;
        tail.next = node;
        this.curLength ++;
    }

    @Override
    public void add(T value) {
        if (!allowAdd()) {
            throw new RuntimeException("not allowed add node to queue");
        }

        for (;;) {
            Node<T> n = head.get();
            if (n == null) {
                // init head
                if (compareAndSetHead(new Node<>())) {
                    // cas success
                }
            } else {
                // had ready init
                Node<T> t = tail.get();
                if (t == null) {
                    // init tail
                    if (compareAndSetTail(null, new Node<>())) {
                        // cas success
                    }
                } else {

                    Node<T> expectNode = t.next.get();
                    compareAndSetNext(expectNode,)
                }
            }
        }

        if (curLength == 0) {
            addWhenFist(value);
            return;
        }
        doAdd(value);
    }

    private boolean compareAndSetHead(Node<T> update) {
        return head.compareAndSet(null, update);
    }

    private boolean compareAndSetTail(Node<T> expect, Node<T> update) {
        return tail.compareAndSet(expect, update);
    }

    private boolean compareAndSetNext(Node<T> expect, Node<T> update) {
        return expect.next.compareAndSet(expect, update);
    }

    private boolean compareAndSetValue(Node<T> expect, T update) {
        return expect.value.compareAndSet(expect.value.get(), update);
    }



    @Override
    public T poll() {
        if (!allowPoll()) {
            throw new RuntimeException("not allowed poll node fron queue");
        }
        // queue is full
        Node<T> result = head.next;
        this.head.next = result.next;
        result.next = null;

        curLength --;
        return result.value;
    }
}
