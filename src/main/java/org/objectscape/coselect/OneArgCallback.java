package org.objectscape.coselect;

import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver
 * Date: 22.11.13
 * Time: 08:20
 * To change this template use File | Settings | File Templates.
 */
public class OneArgCallback<ArgType> implements Callback<ArgType>
{
    private final Consumer<ArgType> consumer;

    public OneArgCallback(Consumer<ArgType> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(ArgType arg) {
        consumer.accept(arg);
    }

    @Override
    public boolean contains(Object callback) {
        return consumer.equals(callback);
    }

    @Override
    public boolean isCallbackId(Long callback) {
        return false;
    }
}
