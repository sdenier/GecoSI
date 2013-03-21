/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

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
	
	public SiPort initReader(SiMessageQueue messageQueue) throws TooManyListenersException, IOException {
		port.addEventListener(new RxtxCommReader(port.getInputStream(), messageQueue));
		port.notifyOnDataAvailable(true);
		return this;
	}
	
	public CommWriter getWriter() throws IOException {
		return new RxtxCommWriter(port.getOutputStream());
	}

	public void setHighSpeed() throws UnsupportedCommOperationException {
		setSpeed(38400);		
	}

	public void setLowSpeed() throws UnsupportedCommOperationException {
		setSpeed(4800);		
	}

	public void setSpeed(int baudRate) throws UnsupportedCommOperationException {
		port.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	}
	
}
