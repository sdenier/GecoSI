/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.adapter.logfile;

import java.io.IOException;

import net.gecosi.internal.CommWriter;
import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 28, 2013
 *
 */
public class NullCommWriter implements CommWriter {

	@Override
	public void write(SiMessage message) throws IOException {}

}
