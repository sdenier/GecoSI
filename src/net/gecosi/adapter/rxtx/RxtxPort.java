/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.adapter.rxtx;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

import net.gecosi.internal.CommWriter;
import net.gecosi.internal.SiMessageQueue;
import net.gecosi.internal.SiPort;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public class RxtxPort implements SiPort {

	private SerialPort port;

	public RxtxPort(SerialPort port) {
		this.port = port;
	}
	
	public SerialPort getPort() {
		return port;
	}
	
	public SiMessageQueue createMessageQueue() throws TooManyListenersException, IOException {
		SiMessageQueue messageQueue = new SiMessageQueue(10);
		port.addEventListener(new RxtxCommReader(port.getInputStream(), messageQueue));
		port.notifyOnDataAvailable(true);
		return messageQueue;
	}
	
	public CommWriter createWriter() throws IOException {
		return new RxtxCommWriter(port.getOutputStream());
	}

	public void setupHighSpeed() throws UnsupportedCommOperationException {
		setSpeed(38400);		
	}

	public void setupLowSpeed() throws UnsupportedCommOperationException {
		setSpeed(4800);		
	}

	public void setSpeed(int baudRate) throws UnsupportedCommOperationException {
		port.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	}

	public void close() {
		// TODO: close streams?
		port.close();
	}
	
}
