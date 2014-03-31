package org.objectscape.coselect;

import org.fusesource.hawtdispatch.DispatchQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver Plohmann, www.objectscape.org
 * Date: 02.09.13
 * Time: 08:39
 */
public class Channel<ItemType> {

    private CoSelect select = null;
    private DispatchQueue dispatchQueue = null;
    private transient BlockingQueue<ItemType> itemQueue = new LinkedBlockingQueue<>();

    protected Channel(CoSelect select, DispatchQueue dispatchQueue) {
        super();
        this.select = select;
        this.dispatchQueue = dispatchQueue;
    }

    public boolean add(ItemType item) {
        boolean result = itemQueue.add(item);
        enqueue();
        return result;
    }

    public boolean offer(ItemType item) {
        boolean result = itemQueue.offer(item);
        enqueue();
        return result;
    }

    public void put(ItemType item) throws InterruptedException {
        itemQueue.put(item);
        enqueue();
    }

    public boolean offer(ItemType item, long timeout, TimeUnit unit) throws InterruptedException {
        boolean result = itemQueue.offer(item);
        enqueue();
        return result;
    }

    private void enqueue() {
        dispatchQueue.execute(() -> {
            select.execute(itemQueue);
        });
    }

    protected void setDispatchQueue(DispatchQueue dispatchQueue) {
        this.dispatchQueue = dispatchQueue;
    }

    protected DispatchQueue getQueue() {
        return dispatchQueue;
    }

    protected void setSelect(CoSelect select) {
        this.select = select;
    }

    protected CoSelect getSelect() {
        return select;
    }

    protected BlockingQueue<ItemType> getItemQueue() {
        return itemQueue;
    }

    public Consumer<ItemType> onElementAdded(Consumer<ItemType> consumer) {
        select.addChannelCallback(this, new OneArgCallback<>(consumer));
        return consumer;
    }

    public boolean removeCallback(Consumer<ItemType> consumer) {
        return select.removeChannelCallback(this, consumer);
    }

    public boolean removeCallback(long callback) {
        return select.removeChannelCallback(this, callback);
    }

    public BiConsumer<ItemType, Long> onElementAdded(BiConsumer<ItemType, Long> consumer) {
        select.addChannelCallback(this, new TwoArgCallback<>(consumer));
        return consumer;
    }

}
