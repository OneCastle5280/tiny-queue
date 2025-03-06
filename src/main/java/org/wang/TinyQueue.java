package org.wang;

/**
 * @author wangjiabao
 */
public interface TinyQueue<T> {

    /**
     * add node to queue
     */
    void add(T value);

    /**
     * get and remove the first node
     */
    T poll();
}
