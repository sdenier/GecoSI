/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.adapter.rxtx;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
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
	
	private InputStream input;

	private SiMessageQueue messageQueue;

	private byte[] accumulator;

	private int accSize;
	
	private long lastTime;

	private int timeoutDelay;
	

	public RxtxCommReader(InputStream input, SiMessageQueue messageQueue) {
		this(input, messageQueue, 500);
	}

	public RxtxCommReader(InputStream input, SiMessageQueue messageQueue, int timeout) {
		this.input = input;
		this.messageQueue = messageQueue;
		this.timeoutDelay = timeout;
		this.lastTime = 0;
	}

	public void serialEvent(SerialPortEvent event) {
		try {
			checkTimeout();
			accumulate();
			if( accSize == 1 && accumulator[0] != 0x02 ){
				sendMessage();
			} else {
				checkExpectedLength(accumulator, accSize);
			}
		} catch (Exception e) {
			GecoSILogger.error(" #serialEvent# " + e.toString());
			e.printStackTrace();
		}
	}

	private void resetAccumulator() {
		accumulator = new byte[MAX_MESSAGE_SIZE];
		accSize = 0;
	}

	private void accumulate() throws IOException {
		accSize += this.input.read(accumulator, accSize, MAX_MESSAGE_SIZE - accSize);
	}
	
	private void checkTimeout() {
		long currentTime = System.currentTimeMillis();
		if( currentTime > lastTime + timeoutDelay ){
			resetAccumulator();
		}
		lastTime = currentTime;
	}

	protected void checkExpectedLength(byte[] accumulator, int accSize) throws InterruptedException {
		if( completeMessage(accumulator, accSize) ){
			sendMessage();
		} else {
			GecoSILogger.debug("Fragment");
		}
	}
	
	protected boolean completeMessage(byte[] answer, int nbReadBytes) {
		return (answer[2] & 0xFF) == nbReadBytes - METADATA_SIZE;
	}

	private void sendMessage() throws InterruptedException {
		queueMessage(extractMessage(accumulator, accSize));
		resetAccumulator();
	}

	private void queueMessage(SiMessage message) throws InterruptedException {
		GecoSILogger.log("READ", message.toString());
		messageQueue.put(message);
	}

	private SiMessage extractMessage(byte[] answer, int nbBytes) {
		return new SiMessage( Arrays.copyOfRange(answer, 0, nbBytes) );
	}

}
