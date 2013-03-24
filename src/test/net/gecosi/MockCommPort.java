/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.TooManyListenersException;

import net.gecosi.CommWriter;
import net.gecosi.SiMessage;
import net.gecosi.SiMessageQueue;
import net.gecosi.SiPort;

/**
 * @author Simon Denier
 * @since Mar 14, 2013
 *
 */
public class MockCommPort implements SiPort {

	private MockComm comm;
	
	private SiMessageQueue messageQueue;
	
	/**
	 * Constructor for timeout (empty queue)
	 */
	public MockCommPort() {
		this(new SiMessage[0]);
	}

	public MockCommPort(SiMessage[] siMessages) {
		comm = new MockComm();
		messageQueue = new SiMessageQueue(siMessages.length + 1, 1);
		messageQueue.addAll(Arrays.asList(siMessages));
		
	}

	public SiMessageQueue createMessageQueue() throws TooManyListenersException, IOException {
		return messageQueue;
	}

	public CommWriter createWriter() throws IOException {
		return this.comm;
	}

	public class MockComm implements CommWriter {
		public void write_debug(SiMessage message) throws IOException {}
	}

	public void setupHighSpeed() throws UnsupportedCommOperationException {}

	public void setupLowSpeed() throws UnsupportedCommOperationException {}

	public void close() {
		// TODO test always closed/called
	}
	
}
