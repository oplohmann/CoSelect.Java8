package org.objectscape.coselect;

/**
 * Created with IntelliJ IDEA.
 * User: Nutzer
 * Date: 26.11.13
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public interface Callback<ArgType> {

    public void accept(ArgType arg);

    public boolean contains(Object callback);

    public boolean isCallbackId(Long callback);
}
