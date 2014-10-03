package cs601.blkqueue;

import java.util.concurrent.atomic.AtomicLong;
import static java.lang.Thread.sleep;

public class RingBuffer<T> implements MessageQueue<T> {
	private final AtomicLong w = new AtomicLong(-1);	// just wrote location
	private final AtomicLong r = new AtomicLong(0);		// about to read location

    boolean debug_flag = false;

    private final int size;
    private final int sizeMinusOne;

    T[] items;

	public RingBuffer(int n) {
        items = (T[]) new Object[n];
        size = n;
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

//    @Override
//    public void put(T v) throws InterruptedException {
//        while ( w.get()-r.get() == size-1 ) /*waitForFreeSlotAt(w.get())*/;
//
//        items[(int)w.incrementAndGet()%size] = v;
//
//        if(debug_flag) System.out.println("--- " + w.get() + " （" + r.get() + " ) "  + (int)w.get()%size + " " + v);
//    }

    @Override
    public void put(T v) throws InterruptedException {
        while (true) {
            long wIndex = w.get();
            long rIndex = r.get();
            long wNewIndex = wIndex+1;

            T preValue = items[(int)wNewIndex & sizeMinusOne];

            if( wIndex-rIndex == sizeMinusOne ) {
                waitForFreeSlotAt(wIndex);
                continue;
            }

            items[(int)wNewIndex & sizeMinusOne] = v;

            if(w.compareAndSet(wIndex,wIndex+1)) {
                if(debug_flag) System.out.println("--- wNewIndex[" + wNewIndex + "] w.get()" + w.get() + " r.get()（" + r.get() + " ) 在 "  + (int)wNewIndex%size + " 写入 " + v);

                return;
            }
            else {
                items[(int)wNewIndex & sizeMinusOne] = preValue;
                continue;
            }
        }
    }

//	@Override
//	public T take() throws InterruptedException {
//        while (w.get()<r.get()) /*waitForDataAt(r.get())*/;
//
//        //T o = items.get((int) (r.getAndIncrement()%size));
//        int index = (int) (r.getAndIncrement()%size);
//        T o = items[index];
//
//        if(debug_flag) System.out.println(" ++++++++++++++++++++++++ " + r.get() + " " + (int)(r.get()-1)%size + " " + o);
//
//        return o;
//	}

    @Override
    public T take() throws InterruptedException {
        //while (w.get()<r.get()) /*waitForDataAt(r.get())*/;

        while(true) {
            long rIndex = r.get();
            long wIndex = w.get();

            if(wIndex < rIndex) {
                waitForDataAt(rIndex);
                continue;
            }

            int index = (int) (rIndex & sizeMinusOne);

            T o = items[index];

            if(r.compareAndSet(rIndex,rIndex+1)) {

                if(debug_flag) System.out.println(" ++++++++++++++++++++++++ rIndex[" + rIndex + "] r.get()" + r.get() + " 从 " + (int)(rIndex)%size + " 取出 " + o);

                return o;
            }
            else continue;
        }
    }

	void waitForFreeSlotAt(final long writeIndex) {
        if(debug_flag) System.out.println(writeIndex + " " + r.get());
        while (writeIndex-r.get()==sizeMinusOne) try {
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	void waitForDataAt(final long readIndex) {
        if(debug_flag) System.out.println(readIndex + " " + w.get());
        while (readIndex>w.get()) try {
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
