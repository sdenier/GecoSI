/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;

import net.gecosi.adapter.logfile.LogFilePort;
import net.gecosi.adapter.rxtx.RxtxPort;
import net.gecosi.dataframe.SiDataFrame;
import net.gecosi.internal.GecoSILogger;
import net.gecosi.internal.SiDriver;

/**
 * @author Simon Denier
 * @since Mar 12, 2013
 *
 */
public class SiHandler implements Runnable {

	private ArrayBlockingQueue<SiDataFrame> dataQueue;

	private SiListener siListener;

	private long zerohour;

	private SiDriver driver;

	private Thread thread;


	public SiHandler(SiListener siListener) {
		this.dataQueue = new ArrayBlockingQueue<SiDataFrame>(5);
		this.siListener = siListener;
	}

	public void setZeroHour(long zerohour) {
		this.zerohour = zerohour;
	}

	public void connect(String portname) throws IOException, TooManyListenersException {
		try {
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portname);
			if( portId.isCurrentlyOwned() ) {
				siListener.notify(CommStatus.FATAL_ERROR, "Port owned by other app");
			} else {
				GecoSILogger.open("######");
				GecoSILogger.logTime("Start " + portname);
				start();
				SerialPort port = (SerialPort) portId.open("GecoSI", 2000);
				driver = new SiDriver(new RxtxPort(port), this).start();
			}
		} catch (NoSuchPortException e) {
			siListener.notify(CommStatus.FATAL_ERROR, "Port unknowned");
		} catch (PortInUseException e) {
			siListener.notify(CommStatus.FATAL_ERROR, "Port in use");
		}
	}

	public void readLog(String logFilename) throws IOException {
		try {
			GecoSILogger.openOutStreamLogger();
			start();
			driver = new SiDriver(new LogFilePort(logFilename), this).start();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public Thread stop() {
		if( driver != null ){
			driver.interrupt();
		}
		if( thread != null ){
			thread.interrupt();
		}
		return thread;
	}

	public boolean isAlive() {
		return thread != null && thread.isAlive();
	}

	public void notify(SiDataFrame data) {
		data.startingAt(zerohour);
		dataQueue.offer(data); // TODO check true
	}

	public void notify(CommStatus status) {
		GecoSILogger.log("!", status.name());
		siListener.notify(status);
	}

	public void notifyError(CommStatus errorStatus, String errorMessage) {
		GecoSILogger.error(errorMessage);
		siListener.notify(errorStatus, errorMessage);
	}

	public void run() {
		try {
			SiDataFrame dataFrame;
			while( (dataFrame = dataQueue.take()) != null ) {
				siListener.handleEcard(dataFrame);
			}
		} catch (InterruptedException e) {
			dataQueue.clear();
		}
	}

	public static void main(String[] args) {
		if( args.length == 0 ){
			printUsage();
			System.exit(0);
		}

		SiHandler handler = new SiHandler(new SiListener() {
			public void handleEcard(SiDataFrame dataFrame) {
				dataFrame.printString();
			}
			public void notify(CommStatus status) {
				System.out.println("Status -> " + status);
			}
			public void notify(CommStatus errorStatus, String errorMessage) {
				System.out.println("Error -> " + errorStatus + " " + errorMessage);
			}
		});

		if( args.length == 1 ){
			try {
				handler.connect(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if( args.length == 2 && args[0].equals("--file") ){
			try {
				handler.readLog(args[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Unknown command line option");
			printUsage();
			System.exit(1);
		}
	}

	private static void printUsage() {
		System.out.println("Usage: java net.gecosi.SiHandler <serial portname> | --file <log filename>");
	}

}
