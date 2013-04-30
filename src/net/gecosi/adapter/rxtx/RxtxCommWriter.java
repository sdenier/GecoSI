/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.adapter.rxtx;

import java.io.IOException;
import java.io.OutputStream;

import net.gecosi.internal.CommWriter;
import net.gecosi.internal.GecoSILogger;
import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public class RxtxCommWriter implements CommWriter {

	private OutputStream output;

	public RxtxCommWriter(OutputStream out) {
		this.output = out;
	}

	public void write(SiMessage message) throws IOException {
		GecoSILogger.log("SEND", message.toString());
		this.output.write(message.sequence());
	}

}
