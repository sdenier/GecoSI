/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.adapter.rxtx;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.util.Arrays;

import net.gecosi.internal.GecoSILogger;
import net.gecosi.internal.SiMessage;
import net.gecosi.internal.SiMessageQueue;

/**
 * @author Simon Denier
 * @since Feb 13, 2013
 *
 */
public class RxtxCommReader implements SerialPortEventListener {

	public static final int MAX_MESSAGE_SIZE = 139;

	private static final int METADATA_SIZE = 6;
	
	private CommReaderState state;

	private InputStream input;

	private SiMessageQueue messageQueue;

	private byte[] accumulator;

	private int accSize;

	public RxtxCommReader(InputStream input, SiMessageQueue messageQueue) {
		this.state = CommReaderState.READY;
		this.input = input;
		this.messageQueue = messageQueue;
		this.accumulator = new byte[MAX_MESSAGE_SIZE];
		this.accSize = 0;
	}

	public void serialEvent(SerialPortEvent event) {
		try {
			accSize += this.input.read(accumulator, accSize, MAX_MESSAGE_SIZE - accSize);
			this.state = this.state.handle(accumulator, accSize, this);
		} catch (Exception e) {
			GecoSILogger.error(" #serialEvent# " + e.toString());
			e.printStackTrace();
		}
	}
	
	private void queueMessage(SiMessage message) throws InterruptedException {
		GecoSILogger.log("READ", message.toString());
		messageQueue.put(message);
	}

	private SiMessage extractMessage(byte[] answer, int nbBytes) {
		return new SiMessage( Arrays.copyOfRange(answer, 0, nbBytes) );
	}

	private void sendMessage() throws InterruptedException {
		queueMessage(extractMessage(accumulator, accSize));
		accumulator = new byte[MAX_MESSAGE_SIZE];
		accSize = 0;
	}

	public enum CommReaderState {
		READY {
			@Override
			protected CommReaderState handle(byte[] accumulator, int accSize, RxtxCommReader reader)
					throws InterruptedException {
				return checkExpectedLength(accumulator, accSize, reader);
			}
		},
		
		EXPECTING_FRAGMENT {
			@Override
			protected CommReaderState handle(byte[] accumulator, int accSize, RxtxCommReader reader)
					throws InterruptedException {
				return checkExpectedLength(accumulator, accSize, reader);
			}
		};

		protected abstract CommReaderState handle(byte[] accumulator, int accSize, RxtxCommReader reader)
				throws InterruptedException;

		protected CommReaderState checkExpectedLength(byte[] accumulator, int accSize, RxtxCommReader reader)
				throws InterruptedException {
			if( completeMessage(accumulator, accSize) ){
				reader.sendMessage();
				return READY;
			} else {
				GecoSILogger.debug("Fragment");
				return EXPECTING_FRAGMENT;
			}
		}
		
		protected boolean completeMessage(byte[] answer, int nbBytes) {
			return (answer[2] & 0xFF) == nbBytes - METADATA_SIZE;
		}

	}
	
}
