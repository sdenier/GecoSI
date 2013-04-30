/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.internal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

/**
 * @author Simon Denier
 * @since Apr 17, 2013
 *
 */
public class GecoSILogger {
	
	public static class NullWriter extends Writer {
		@Override
		public void close() throws IOException {}
		@Override
		public void flush() throws IOException {}
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {}
	}

	public static class OutStreamWriter extends Writer {
		@Override
		public void close() throws IOException {}
		@Override
		public void flush() throws IOException {}
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			for (int i = off; i < off + len; i++) {
				System.out.print(cbuf[i]);
			}
		}
	}
	
	private static Writer logger;
	
	public static void open() {
		if( logger != null ) {
			close();
		}
		String logProp = System.getProperty("GECOSI_LOG", "FILE");
		if( logProp.equals("FILE") ){
			openFileLogger();
		}
		else if( logProp.equals("NONE") ){
			openNullLogger();
		}
		else if( logProp.equals("OUTSTREAM") ){
			openOutStreamLogger();
		}
	}

	public static void openFileLogger() {
		try {
			logger = new BufferedWriter(new FileWriter("gecosi.log", true));
		} catch (IOException e) {
			e.printStackTrace();
			openOutStreamLogger();
		}
	}

	public static void openNullLogger() {
		logger = new NullWriter();
	}

	public static void openOutStreamLogger() {
		logger = new OutStreamWriter();
	}
	
	public static void open(String header) {
		open();
		log(header, "");
	}
	
	public static void log(String header, String message) {
		try {
			logger.write(String.format("%s %s\n", header, message));
			logger.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	public static void logTime(String message) {
		log(new Date().toString(), message);
	}

	public static void stateChanged(String message) {
		log("-->", message);
	}
	
	public static void info(String message) {
		log("[Info]", message);
	}

	public static void debug(String message) {
		log("[Debug]", message);
	}

	public static void warning(String message) {
		log("[Warning]", message);
	}
	
	public static void error(String message) {
		log("[Error]", message);
	}
	
	public static void close() {
		try {
			logger.close();
			logger = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
