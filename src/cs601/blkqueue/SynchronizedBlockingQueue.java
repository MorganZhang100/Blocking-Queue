package cs601.blkqueue;

import java.util.ArrayList;

public class SynchronizedBlockingQueue<T> implements MessageQueue<T> {

    //private T[] items;
    private int count = 0;
    private int MAX_INDEX;
    private volatile ArrayList<T> items;

	public SynchronizedBlockingQueue(int size) {
        this.items = new ArrayList<T>(size);
        this.MAX_INDEX = size-1;

	}

	@Override
	public synchronized void put(T o) throws InterruptedException {
        try {
            while ( count==MAX_INDEX ) wait();
        }
        catch (InterruptedException ie) {
            throw new RuntimeException("woke up", ie);
        }
        this.items.add(o);
        count++;

        // have data.  tell any waiting threads to wake up
        notifyAll();
	}

	@Override
	public synchronized T take() throws InterruptedException {

        try {
            while (count==0) wait();
        }

        catch (InterruptedException ie) {
            System.err.println("heh, who woke me up too soon?");
        }

        // we have the lock and state we're seeking; remove, return element
        T o = this.items.remove(0);
        count--;

        notifyAll();
        return o;
	}
}
