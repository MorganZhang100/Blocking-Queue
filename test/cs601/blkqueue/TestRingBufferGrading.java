package cs601.blkqueue;

public class TestRingBufferGrading {

	public static void main(String[] args) throws Exception {
		int N = 1;

		RingBuffer<Integer> queue = new RingBuffer<Integer>(1024);
		MessageSequence<Integer> sequence = new IntegerSequence(1,N);
		TestRig.test(queue, sequence, N);

		N = 8000;
		queue = new RingBuffer<Integer>(1);
		sequence = new IntegerSequence(1,N);
		TestRig.test(queue, sequence, N);

		N = 10000;
		RingBuffer<String> qs = new RingBuffer<String>(1024);
		MessageSequence<String> ss = new StringSequence(N);
		TestRig.test(qs, ss, N);
	}
}
