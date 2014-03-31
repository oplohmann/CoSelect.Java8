package org.objectscape.coselect;

import org.fusesource.hawtdispatch.internal.DispatcherConfig;
import org.junit.After;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver
 * Date: 09.12.13
 * Time: 08:36
 * To change this template use File | Settings | File Templates.
 */
public class AbstractDispatchTest {

    @After
    public void shutDown() {
        // shut down HawtDispatch
        DispatcherConfig.getDefaultDispatcher().shutdown();
    }

}
