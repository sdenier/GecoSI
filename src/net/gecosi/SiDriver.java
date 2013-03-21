/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeoutException;

/**
 * @author Simon Denier
 * @since Feb 13, 2013
 *
 */
public class SiDriver implements Runnable {

	private SiPort siPort;
	private CommWriter writer;
	private SiMessageQueue messageQueue;
	private Thread thread;
	private SiHandler siHandler;

	public SiDriver(SiPort siPort, SiHandler siHandler) throws TooManyListenersException, IOException {
		messageQueue = new SiMessageQueue(10);
		this.siHandler = siHandler;
		this.siPort = siPort.initReader(messageQueue);
		this.writer = siPort.getWriter();
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		try {
			SiDriverState currentState = SiDriverState.STARTUP.send(writer);
			while (!thread.isInterrupted()) {
				currentState = currentState.receive(messageQueue, writer, siHandler);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
