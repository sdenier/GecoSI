/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import gnu.io.UnsupportedCommOperationException;

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
		this.siPort = siPort;
		this.messageQueue = siPort.createMessageQueue();
		this.writer = siPort.createWriter();
		this.siHandler = siHandler;
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void interrupt() {
		thread.interrupt();
	}

	public void run() {
		try {
			SiDriverState currentState = startupProtocol();
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
			stop();
		} catch (InvalidMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private SiDriverState startupProtocol()
			throws UnsupportedCommOperationException, IOException, InterruptedException, InvalidMessage, TimeoutException {
		try {
			siHandler.notify(CommStatus.STARTING);
			siPort.setupHighSpeed();
			return startup();
		} catch (TimeoutException e) {
			siPort.setupLowSpeed();
			return startup();
		}
	}

	private SiDriverState startup()
			throws IOException, InterruptedException, TimeoutException, InvalidMessage {
		SiDriverState currentState = SiDriverState.STARTUP.send(writer).receive(messageQueue, writer, siHandler);
		return currentState;
	}

	private void stop() {
		siPort.close();
		siHandler.notify(CommStatus.OFF);
	}

}
