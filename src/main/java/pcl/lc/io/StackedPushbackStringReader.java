package pcl.lc.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class StackedPushbackStringReader {

	protected Object lock;
	private ArrayList<Character> str;
	private Stack<Integer> pushback;
	private int next = 0;

	public StackedPushbackStringReader(String s) {
		this.lock = this;
		this.str = new ArrayList<Character>();
		this.pushback = new Stack<Integer>();
		char[] chars = s.toCharArray();
		System.out.println(chars.length);
		for (int i = 0; i < chars.length; i++)
			str.add(chars[i]);
	}

	/** Check to make sure that the stream has not been closed */
	private void ensureOpen() throws IOException {
		if (str == null)
			throw new IOException("Stream closed");
	}

	public char next() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if (next >= str.size())
				return (char) 0;
			return str.get(next++);
		}
	}

	public void pushPosition() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if (this.pushback.size() > 64)
				throw new IOException("Pusback overflow!");
			this.pushback.push(next);
		}
	}

	public void popPosition() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if (this.pushback.size() == 0)
				throw new IOException("Pushback underflow!");
			this.next = this.pushback.pop();
		}
	}

	public int getPosition() throws IOException {
		synchronized (lock) {
			ensureOpen();
			return this.next;
		}
	}

	public void setPosition(int ns) throws IOException {
		synchronized (lock) {
			ensureOpen();
			this.next = ns;
		}
	}

	public void commitPosition() throws IOException {
		synchronized (lock) {
			ensureOpen();
			this.pushback.pop();
		}
	}

	public void skip(long ns) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if (next >= str.size())
				return;
			next += ns;
			if (0 > next)
				next = 0;
			if (next > str.size())
				next = str.size();
		}
	}

	public void rewind(long ns) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if (next >= str.size())
				return;
			next -= ns;
			if (0 > next)
				next = 0;
			if (next > str.size())
				next = str.size();
		}
	}

	public boolean ready() throws IOException {
		synchronized (lock) {
			ensureOpen();
			return true;
		}
	}

	public int available() throws IOException {
		synchronized (lock) {
			ensureOpen();
			return Math.max(0, str.size() - next);
		}
	}

	public void close() {
		str = null;
	}

}
