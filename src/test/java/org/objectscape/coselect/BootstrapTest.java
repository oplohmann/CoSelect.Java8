package org.objectscape.coselect;

import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.hawtdispatch.DispatchQueue;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver
 * Date: 09.12.13
 * Time: 08:35
 * To change this template use File | Settings | File Templates.
 */
public class BootstrapTest extends AbstractDispatchTest {

    @Test
    public void suspendResumeQueue()
    {
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch waitTillDone = new CountDownLatch(1);
        DispatchQueue queue = Dispatch.createQueue();
        queue.execute(() -> {
            try {
                System.out.println("1");
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        queue.suspend();

        queue.execute(() -> {
            System.out.println("2");
            waitTillDone.countDown();
        });

        System.out.println("suspend: " + queue.isSuspended());
        latch.countDown();

        System.out.println("before resume");
        queue.resume();
        System.out.println("after resume");

        try {
            waitTillDone.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("done");
    }

    protected Callable<String> getCallable(CountDownLatch waitTillDone)
    {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("call start");
                waitTillDone.await();
                System.out.println("call end");
                return "done";
            }
        };

        return callable;
    }

}
