/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public class CommWriter implements ICommWriter {

	private OutputStream output;

	public CommWriter(OutputStream out) {
		this.output = out;
	}

	public void write_debug(SiMessage message) throws IOException {
		System.out.format("SEND: %s CRC %s %n", message.toString(), message.toStringCRC());
		System.out.flush();
		this.output.write(message.sequence());
	}

}
