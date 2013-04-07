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

	public RxtxCommReader(InputStream input, SiMessageQueue messageQueue) {
		this.input = input;
		this.messageQueue = messageQueue;
	}

	public void serialEvent(SerialPortEvent event) {
		try {
			byte[] answer = new byte[MAX_MESSAGE_SIZE];
			int nbBytes = this.input.read(answer);
			SiMessage message = extractMessage(answer, nbBytes);
			messageQueue.put(message);
			System.out.format("RECEIVE: %s CRC %s %n", message.toString(), message.toStringCRC());
			System.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SiMessage extractMessage(byte[] answer, int nbBytes) {
		return new SiMessage( Arrays.copyOfRange(answer, 0, nbBytes) );
	}

}
