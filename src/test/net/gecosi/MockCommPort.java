/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
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
	
	/**
	 * 
	 */
	public MockCommPort() {
		this(new SiMessage[0]);
	}

	public MockCommPort(SiMessage[] siMessages) {
		this.comm = new MockComm(siMessages);
	}

	public SiMessageQueue createMessageQueue() throws TooManyListenersException, IOException {
		SiMessageQueue messageQueue = new SiMessageQueue(10, 1);
		this.comm.setQueue(messageQueue);
		return messageQueue;
	}

	public CommWriter createWriter() throws IOException {
		return this.comm;
	}

	public class MockComm implements CommWriter {

		private SiMessageQueue queue;
		private int index;
		private SiMessage[] answers = new SiMessage[0];

		public MockComm(SiMessage[] siMessages) {
			this.index = 0;
			this.answers = siMessages;
		}
		
		public void setQueue(SiMessageQueue messageQueue) {
			this.queue = messageQueue;
		}

		public void write_debug(SiMessage message) throws IOException {
			if( index < answers.length ){
				this.queue.add(answers[index++]);
			}
		}

	}

	public void setupHighSpeed() throws UnsupportedCommOperationException {}

	public void setupLowSpeed() throws UnsupportedCommOperationException {}

	public void close() {
		// TODO test always closed/called
	}
	
}
