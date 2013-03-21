/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.io.IOException;

/**
 * @author Simon Denier
 * @since Mar 15, 2013
 *
 */
public interface ICommWriter {

	public void write_debug(SiMessage message) throws IOException;

}
