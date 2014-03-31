package org.objectscape.coselect;

import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver Plohmann, www.objectscape.org
 * Date: 02.09.13
 * Time: 08:32
 */
public class CoSelectTest extends AbstractDispatchTest {

    @Test
    public void selectTwoChannels() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(2);
        CoSelect select = new CoSelect();
        AtomicInteger count = new AtomicInteger(0);

        Channel<String> channelOne = select.newChannel();
        channelOne.onElementAdded((String str) -> {
            System.out.println(str);
            count.incrementAndGet();
            latch.countDown();
        });

        Channel<Integer> channelTwo = select.newChannel();
        channelTwo.onElementAdded((Integer i) -> {
            System.out.println(i);
            count.incrementAndGet();
            latch.countDown();
        });

        channelOne.add("hello world!");
        channelTwo.add(123);

        latch.await();
        Assert.assertEquals(count.get(), 2);
    }

    @Test
    public void selectSingleChannel() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);
        CoSelect select = new CoSelect();
        AtomicInteger count = new AtomicInteger(0);

        Channel<String> channel = select.newChannel();
        channel.onElementAdded((String str) -> {
            System.out.println(str);
            count.incrementAndGet();
            latch.countDown();
        });

        channel.add("hello world!");

        latch.await();
        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void removeCallbackFromWithinCallback() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);
        CoSelect select = new CoSelect();
        AtomicInteger count = new AtomicInteger(0);

        Channel<String> channel = select.newChannel();
        channel.onElementAdded((String str, Long callback) -> {
            count.incrementAndGet();
            boolean found = channel.removeCallback(callback);
            Assert.assertTrue(found);
            latch.countDown();
        });

        channel.add("hello world!");

        latch.await();
        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void removeCallbackFromChannel() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);
        CoSelect select = new CoSelect();
        AtomicInteger count = new AtomicInteger(0);

        Channel<String> channel = select.newChannel();
        Consumer<String> callback = channel.onElementAdded((String str) -> {
            System.out.println(str);
            count.incrementAndGet();
            latch.countDown();
        });

        channel.add("hello world!");

        latch.await();
        Assert.assertEquals(count.get(), 1);

        boolean callbackFound = channel.removeCallback(callback);
        Assert.assertTrue(callbackFound);

        channel.add("another hello world!");

        Thread.sleep(1000);
        // still 1 as callback was removed from the channel
        Assert.assertEquals(count.get(), 1);
    }

    @Test
    public void selectOneChannelTwoCallbacks() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(4);
        CoSelect select = new CoSelect();
        AtomicInteger count = new AtomicInteger(0);

        Channel<String> channel = select.newChannel();

        channel.onElementAdded((String str) -> {
            System.out.println(str + "!");
            count.incrementAndGet();
            latch.countDown();
        });

        channel.onElementAdded((String str) -> {
            System.out.println(str + " world!");
            count.incrementAndGet();
            latch.countDown();
        });

        channel.add("hello");
        channel.add("hello");

        latch.await();
        Assert.assertEquals(count.get(), 4);
    }

}
