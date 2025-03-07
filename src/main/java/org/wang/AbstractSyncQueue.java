package org.wang;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * @author wangjiabao
 */
public abstract class AbstractSyncQueue<T> implements TinyQueue<T> {

    private static final Unsafe unsafe;
    private static final long nextOffset;
    private static final long curLengthOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);

            curLengthOffset = unsafe.objectFieldOffset(AbstractSyncQueue.class.getDeclaredField("curLength"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    /**
     * node
     */
    static class Node <T> {
        /**
         * data
         */
        private T value;

        /**
         * point to the next node
         */
        private volatile Node<T> next;
    }

    /**
     * current length of the queue
     */
    private volatile int curLength;
    /**
     * the max length of the queue
     */
    private volatile int maxLength;
    /**
     * point to the head node
     */
    private volatile Node<T> head;
    /**
     * point to the tail node
     */
    private volatile Node<T> tail;

    protected abstract boolean allowPoll();

    protected abstract boolean allowAdd();

    protected AbstractSyncQueue (Integer maxLength) {
        this.head = new Node<>();
        this.tail = new Node<>();
        Node<T> dumbNode = new Node<>();
        this.head.next = dumbNode;
        this.tail.next = dumbNode;

        this.maxLength = maxLength;
    }

    @Override
    public void add(T value) {
        if (!allowAdd()) {
            throw new RuntimeException("not allowed add node to queue");
        }

        Node<T> n = new Node<>();
        n.value = value;

        for (;;) {
            if (this.curLength < this.maxLength) {
                Node<T> t = this.tail;
                Node<T> originalLastNode = t.next;
                if (compareAndSetNext(originalLastNode, originalLastNode.next, n)
                        && compareAndSetNext(t, originalLastNode, n)
                        && compareAndSetCurLength(this.curLength, this.curLength + 1)) {
                    // cas add success
                    return;
                }
            } else {
                // thread wait
                return;
            }
        }
    }

    private boolean compareAndSetNext(Node<T> node, Node<T> expect, Node<T> update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }

    private boolean compareAndSetCurLength(int expect, int update) {
        return unsafe.compareAndSwapInt(this, curLengthOffset, expect, update);
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
