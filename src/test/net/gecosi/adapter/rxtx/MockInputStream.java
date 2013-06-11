/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.adapter.rxtx;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simon Denier
 * @since Jun 10, 2013
 *
 */
public class MockInputStream extends InputStream {

	private byte[] input;

	public void setInput(byte[] input) {
		this.input = input;
	}
	
	@Override
	public int read() throws IOException {
		return 0;
	}

	@Override
	public int read(byte[] answer) throws IOException {
		int length = Math.min(input.length, answer.length);
		System.arraycopy(input, 0, answer, 0, length);
		return length;
	}
	
}
