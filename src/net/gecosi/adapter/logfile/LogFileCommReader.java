/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.adapter.logfile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.gecosi.internal.SiMessage;
import net.gecosi.internal.SiMessageQueue;

/**
 * @author Simon Denier
 * @since Apr 28, 2013
 *
 */
public class LogFileCommReader {

	private List<SiMessage> messages;

	public LogFileCommReader(String filename) throws IOException {
		messages = read(filename, new ArrayList<SiMessage>(100));
	}
	
	public List<SiMessage> read(String filename, ArrayList<SiMessage> messages) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(filename));
		String line = fileReader.readLine();
		while( line != null ){
			if( line.startsWith("READ") ) {
				String[] bytes = line.split(" ");
				byte[] seq = new byte[bytes.length - 1];
				for (int i = 1; i < bytes.length; i++) {
					seq[i-1] = (byte) Integer.parseInt(bytes[i], 16);
				}
				messages.add(new SiMessage(seq));
			}
			line = fileReader.readLine();
		}
		fileReader.close();
		return messages;
	}
	
	public SiMessageQueue createMessageQueue() {
		SiMessageQueue queue = new SiMessageQueue(messages.size());
		queue.addAll(messages);
		return queue;
	}
	
}
