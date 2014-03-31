package org.objectscape.coselect;

import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.hawtdispatch.DispatchQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver Plohmann, www.objectscape.org
 * Date: 02.09.13
 * Time: 08:30
 */
public class CoSelect
{

    private DispatchQueue queue = Dispatch.getGlobalQueue();
    private ConcurrentHashMap<BlockingQueue<?>, Queue<Callback>> itemQueueMap = new ConcurrentHashMap<>();

    protected <ChannelType> void addChannelCallback(Channel<ChannelType> channel, Callback<ChannelType> callback)
    {
        if(channel.getQueue() != queue || channel.getSelect() != this) {
            throw new IllegalArgumentException("select/channel mismatch");
        }

        Queue<Callback> consumers = itemQueueMap.get(channel.getItemQueue());
        if(consumers == null) {
            Queue<Callback> newConsumers = new ConcurrentLinkedQueue<>();
            Queue<Callback> previousConsumers = itemQueueMap.putIfAbsent(channel.getItemQueue(), newConsumers);
            if(previousConsumers == null) {
                consumers = newConsumers;
            }
            else {
                consumers = previousConsumers;
            }
        }

        consumers.add(callback);
    }

    public void execute(BlockingQueue<?> itemQueue)
    {
        synchronized (this)
        {
            // TODO - write comment why synchronized block here canot be avoided
            Object element = itemQueue.poll();
            if(element == null)
                return;

            Queue<Callback> callbacks = itemQueueMap.get(itemQueue);
            if(callbacks == null)
                return;

            List<Callback> currentCallbacks = new ArrayList<>();

            Callback next = null;
            while((next = callbacks.poll()) != null) {
                currentCallbacks.add(next);
            }

            if(currentCallbacks.isEmpty())
                return;

            // Poll and then add is performance-wise not that optimal, but for now it gets around
            // having to use synchronized blocks to make access to the callbacks queue synchronized.
            // Re-inserting the OneArgCallback at the end of the queue through add has to be done
            // before the lambda is invoked through accept, because the callback may be removed from
            // the channel by the user from within the lambda (see CoSelectTest.removeCallbackFromWithinCallback)

            callbacks.addAll(currentCallbacks);

            for(Callback callback : currentCallbacks) {
                callback.accept(element);
            }
        }
    }

    public <ChannelType> Channel<ChannelType> newChannel() {
        return new Channel<>(this, queue);
    }

    public <ItemType> boolean removeChannelCallback(Channel<ItemType> channel, Consumer<ItemType> callback)
    {
        Queue<Callback> callbacks = itemQueueMap.get(channel.getItemQueue());
        if(callbacks != null) {
            // not very efficient, stub for now
            for(Callback currentCallback : callbacks) {
                if(currentCallback.contains(callback)) {
                    return callbacks.remove(currentCallback);
                }
            }
        }
        return false;
    }

    public <ItemType> boolean removeChannelCallback(Channel<ItemType> channel, long callback) {
        Queue<Callback> callbacks = itemQueueMap.get(channel.getItemQueue());
        if(callbacks != null) {
            // not very efficient, stub for now
            for(Callback currentCallback : callbacks) {
                if(currentCallback.isCallbackId(callback)) {
                    return callbacks.remove(currentCallback);
                }
            }
        }
        return false;
    }

}
