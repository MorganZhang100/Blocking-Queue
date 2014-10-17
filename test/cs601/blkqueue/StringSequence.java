package cs601.blkqueue;

public class StringSequence implements MessageSequence<String> {
	int n;
	int i = 0; // next one to read
	public StringSequence(int n) {
		this.n = n;
	}

	@Override
	public boolean validSequenceMove(String previous, String current) {
		if ( current==eof() ) return true;
		return Integer.parseInt(current) == Integer.parseInt(previous) + 1;
	}

	@Override
	public boolean hasNext() {
		return i<n;
	}

	@Override
	public String next() {
		int j = i; i++; return String.valueOf(j);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String eof() {
		return "<EOF>";
	}
}
