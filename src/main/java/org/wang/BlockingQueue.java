package org.wang;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author wangjiabao
 */
@Data
@Accessors(chain = true)
public class BlockingQueue <T> extends AbstractSyncQueue<T> {

    public BlockingQueue(int maxLength) {
        super(maxLength);
    }

    @Override
    protected boolean allowAdd() {
        return true;
    }

    @Override
    protected boolean allowPoll() {
        return true;
    }
}
