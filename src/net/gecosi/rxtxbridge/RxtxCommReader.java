/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.rxtxbridge;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
				System.err.println("Message fragment 2");
				byte[] messsageFrame = Arrays.copyOf(messageFragment, messageFragment.length + nbBytes);
				System.arraycopy(answer, 0, messsageFrame, messageFragment.length, nbBytes);
				queueMessage(new SiMessage(messsageFrame));
				messageFragment = null;
			} else {
				if( messageInOnePiece(answer, nbBytes) ) {
					queueMessage(extractMessage(answer, nbBytes));
				} else {
					System.err.println("Message fragment 1");
					messageFragment = Arrays.copyOfRange(answer, 0, nbBytes);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		System.out.format("RECEIVE: %s CRC %s %n", message.toString(), message.toStringCRC());
	}

	private SiMessage extractMessage(byte[] answer, int nbBytes) {
		return new SiMessage( Arrays.copyOfRange(answer, 0, nbBytes) );
	}

}
