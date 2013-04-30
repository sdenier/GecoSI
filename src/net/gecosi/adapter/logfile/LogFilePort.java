/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.adapter.logfile;

import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

import net.gecosi.internal.CommWriter;
import net.gecosi.internal.SiMessageQueue;
import net.gecosi.internal.SiPort;

/**
 * @author Simon Denier
 * @since Apr 28, 2013
 *
 */
public class LogFilePort implements SiPort {

	private String logFilename;

	public LogFilePort(String logFilename) {
		this.logFilename = logFilename;
	}

	@Override
	public SiMessageQueue createMessageQueue() throws TooManyListenersException, IOException {
		return new LogFileCommReader(logFilename).createMessageQueue();
	}

	@Override
	public CommWriter createWriter() throws IOException {
		return new NullCommWriter();
	}

	@Override
	public void setupHighSpeed() throws UnsupportedCommOperationException {	}

	@Override
	public void setupLowSpeed() throws UnsupportedCommOperationException {	}

	@Override
	public void close() {}

}
