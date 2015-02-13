package cs601.blkqueue;

import java.util.concurrent.atomic.AtomicLong;
import static java.lang.Thread.sleep;

public class RingBuffer<T> implements MessageQueue<T> {
	private final AtomicLong w = new AtomicLong(-1);	// just wrote location
	private final AtomicLong r = new AtomicLong(0);		// about to read location
    private final int sizeMinusOne;

    T[] items;

	public RingBuffer(int n) {
        items = (T[]) new Object[n];
        sizeMinusOne = n-1;

        if(!isPowerOfTwo(n)) throw new IllegalArgumentException();
	}

	// http://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
	static boolean isPowerOfTwo(int v) {
		if (v<0) return false;
		v = v - ((v >> 1) & 0x55555555);                    // reuse input as temporary
		v = (v & 0x33333333) + ((v >> 2) & 0x33333333);     // temp
		int onbits = ((v + (v >> 4) & 0xF0F0F0F) * 0x1010101) >> 24; // count
		// if number of on bits is 1, it's power of two, except for sign bit
		return onbits==1;
	}

    @Override
    public void put(T v) throws InterruptedException {
            long wIndex = w.get();
            long wNewIndex = wIndex+1;

            if( wIndex-r.get() == sizeMinusOne ) {
                waitForFreeSlotAt(wIndex);
            }

            items[(int)wNewIndex & sizeMinusOne] = v;
            w.incrementAndGet();
    }

    @Override
    public T take() throws InterruptedException {
            long rIndex = r.get();

            if(w.get() < rIndex) {
                waitForDataAt(rIndex);
            }

            int index = (int) (rIndex & sizeMinusOne);

            T o = items[index];
            r.incrementAndGet();
            return o;
    }

	void waitForFreeSlotAt(final long writeIndex) throws InterruptedException {
        while (writeIndex-r.get()==sizeMinusOne)
            sleep(0);
    }

	void waitForDataAt(final long readIndex) throws InterruptedException {
        while (readIndex>w.get())
            sleep(0);
    }
}