/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import gnu.io.SerialPort;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public class SiPort implements ISiPort {

	private SerialPort port;

	public SiPort(SerialPort port) {
		this.port = port;
	}
	
	public SerialPort getPort() {
		return port;
	}
	
	public ISiPort initReader(SiMessageQueue messageQueue) throws TooManyListenersException, IOException {
		port.addEventListener(new CommReader(port.getInputStream(), messageQueue));
		port.notifyOnDataAvailable(true);
		return this;
	}
	
	public ICommWriter getWriter() throws IOException {
		return new CommWriter(port.getOutputStream());
	}
	
}
