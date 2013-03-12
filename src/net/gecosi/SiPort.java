/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import gnu.io.SerialPort;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public class SiPort {

	private SerialPort port;

	public SiPort(SerialPort port) {
		this.port = port;
	}
	
	public SerialPort getPort() {
		return port;
	}
	
	public SiPort initReader(ArrayBlockingQueue<SiMessage> messageQueue) throws TooManyListenersException, IOException {
		port.addEventListener(new CommReader(port.getInputStream(), messageQueue));
		port.notifyOnDataAvailable(true);
		return this;
	}
	
	public CommWriter getWriter() throws IOException {
		return new CommWriter(port.getOutputStream());
	}
	
}
