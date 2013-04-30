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

	private InputStream input;
	private SiMessageQueue messageQueue;

	private byte[] messageFragment;

	public RxtxCommReader(InputStream input, SiMessageQueue messageQueue) {
		this.input = input;
		this.messageQueue = messageQueue;
	}

	public void serialEvent(SerialPortEvent event) {
		try {
			byte[] answer = new byte[MAX_MESSAGE_SIZE];
			int nbBytes = this.input.read(answer);
			if( awaitingSecondFragment() ) {
				GecoSILogger.debug("Message fragment 2");
				byte[] messsageFrame = Arrays.copyOf(messageFragment, messageFragment.length + nbBytes);
				System.arraycopy(answer, 0, messsageFrame, messageFragment.length, nbBytes);
				queueMessage(new SiMessage(messsageFrame));
				messageFragment = null;
			} else {
				if( messageInOnePiece(answer, nbBytes) ) {
					queueMessage(extractMessage(answer, nbBytes));
				} else {
					GecoSILogger.debug("Message fragment 1");
					messageFragment = Arrays.copyOfRange(answer, 0, nbBytes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GecoSILogger.error(" #serialEvent# " + e.toString());
		}
	}
	
	private boolean messageInOnePiece(byte[] answer, int nbBytes) {
		return (answer[2] & 0xFF) == nbBytes - 6;
	}

	private boolean awaitingSecondFragment() {
		return messageFragment != null;
	}

	private void queueMessage(SiMessage message) throws InterruptedException {
		messageQueue.put(message);
		GecoSILogger.log("READ", message.toString());
	}

	private SiMessage extractMessage(byte[] answer, int nbBytes) {
		return new SiMessage( Arrays.copyOfRange(answer, 0, nbBytes) );
	}

}
