/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Simon Denier
 * @since Mar 12, 2013
 *
 */
public class SiHandler implements Runnable {

	private ArrayBlockingQueue<Si5DataFrame> dataQueue;
	private Thread thread;
	private SiListener siListener;
	private long zerohour;
	
	public SiHandler(SiListener siListener, long zerohour) {
		this.dataQueue = new ArrayBlockingQueue<Si5DataFrame>(5);
		this.siListener = siListener;
		this.zerohour = zerohour;
	}
	
	public Thread start() {
		thread = new Thread(this);
		thread.start();
		return thread;
	}
	
	public void notify(Si5DataFrame data) {
		data.compute24HourTimes(zerohour);
		dataQueue.offer(data); // TODO check true
	}

	public void notify(CommStatus status) {
		// TODO Auto-generated method stub
		System.out.println("Status -> " + status);
	}

	public void notifyError(CommStatus errorStatus, String errorMessage) {
		// TODO Auto-generated method stub
		
	}
	
	public void run() {
		try {
			Si5DataFrame dataFrame;
			while( (dataFrame = dataQueue.take()) != null ) {
				siListener.handleEcard(dataFrame);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
