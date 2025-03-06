package org.wang;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangjiabao
 */
@Data
@Accessors(chain = true)
public class BlockingQueue <T> extends AbstractSyncQueue<T> {

    @Override
    protected boolean allowAdd() {
        return false;
    }

    @Override
    protected boolean allowPoll() {
        return false;
    }
}
