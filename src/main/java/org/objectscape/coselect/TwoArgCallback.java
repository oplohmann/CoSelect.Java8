package org.objectscape.coselect;

import java.util.function.BiConsumer;

/**
 * Created with IntelliJ IDEA.
 * User: Nutzer
 * Date: 26.11.13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class TwoArgCallback<ArgType> implements Callback<ArgType> {

    private static long RunningConsumerId = -1;

    private final BiConsumer<ArgType, Long> consumer;
    private final long consumerId;

    public TwoArgCallback(BiConsumer<ArgType, Long> consumer) {
        this.consumer = consumer;
        synchronized (this) {
            this.consumerId = ++RunningConsumerId;
        }
    }

    @Override
    public boolean contains(Object callback) {
        return consumer.equals(callback);
    }

    @Override
    public boolean isCallbackId(Long callback) {
        return consumerId == callback;
    }

    public void accept(ArgType arg) {
        consumer.accept(arg, consumerId);
    }
}
