package cs601.blkqueue;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

public class RingBuffer<T> implements MessageQueue<T> {
	private final AtomicLong w = new AtomicLong(-1);	// just wrote location
	private final AtomicLong r = new AtomicLong(0);		// about to read location

    boolean debug_flag = true;

    //private ArrayList<T> items;
    private final int size;

    T[] items;

	public RingBuffer(int n) {
        //this.items = new ArrayList<T>(n);
        items = (T[]) new Object[n];

        size = n;
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
        while ( w.get()-r.get() == size-1 ) /*waitForFreeSlotAt(w.get())*/;
//        if(w.get()>size-2) items.set((int) w.incrementAndGet()%size,v);
//        else {
//            items.add(v);
//            w.incrementAndGet();
//        }

        //items.set((int) w.incrementAndGet()%size,v);
        items[(int)w.incrementAndGet()%size] = v;

        if(debug_flag) System.out.println("--- " + w.get() + " （" + r.get() + " ) "  + (int)w.get()%size + " " + v);
	}

	@Override
	public T take() throws InterruptedException {
        while (w.get()<r.get()) /*waitForDataAt(r.get())*/;

        //T o = items.get((int) (r.getAndIncrement()%size));
        int index = (int) (r.getAndIncrement()%size);
        T o = items[index];

        if(debug_flag) System.out.println(" ++++++++++++++++++++++++ " + r.get() + " " + (int)(r.get()-1)%size + " " + o);

        return o;
	}

	// spin wait instead of lock for low latency store
	void waitForFreeSlotAt(final long writeIndex) {
        if(debug_flag) System.out.println(writeIndex + " " + r.get());
        while (writeIndex-r.get()==size-1) try {
            if(debug_flag) System.out.println("@@@@@@@@@@@@@@@@ " + writeIndex + " " + r.get());
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	// spin wait instead of lock for low latency pickup
	void waitForDataAt(final long readIndex) {
        if(debug_flag) System.out.println(readIndex + " " + w.get());
        while (readIndex>w.get()) try {
            if(debug_flag) System.out.print("~~~~~~~~~~~~~~~~~~~~~" + readIndex + " " + w.get());
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
