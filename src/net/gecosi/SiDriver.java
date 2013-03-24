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
			SiDriverState currentState = startupBootstrap();
			while( isAlive(currentState) ) {
				currentState = currentState.receive(messageQueue, writer, siHandler);
			}
			if( currentState.isError() ) {
				siHandler.notifyError(CommStatus.FATAL_ERROR, currentState.status());
			}
		} catch (InterruptedException e) {
			// TODO normal way out?
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			stop();
		}
	}

	private boolean isAlive(SiDriverState currentState) {
		return ! (thread.isInterrupted() || currentState.isError());
	}

	private SiDriverState startupBootstrap()
			throws UnsupportedCommOperationException, IOException, InterruptedException, InvalidMessage {
		try {
			siHandler.notify(CommStatus.STARTING);
			siPort.setupHighSpeed();
			return startup();
		} catch (TimeoutException e) {
			try {
				siPort.setupLowSpeed();
				return startup();
			} catch (TimeoutException e1) {
				return SiDriverState.STARTUP_TIMEOUT;
			}
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
