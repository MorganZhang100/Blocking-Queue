package cs601.blkqueue;

public class SynchronizedBlockingQueue<T> implements MessageQueue<T> {

    private T[] items;
    int count = 0,MAX_SIZE;

	public SynchronizedBlockingQueue(int size) {
        //this.items = new T[size];
        this.MAX_SIZE = size;
	}

	@Override
	public synchronized void put(T o) throws InterruptedException {
        try {
            while ( count==MAX_SIZE ) wait();
        }
        catch (InterruptedException ie) {
            throw new RuntimeException("woke up", ie);
        }
        this.items[count++] = o;

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
        T o = this.items[count];
        this.items[count--] = null;

        notifyAll();
        return o;
	}
}
