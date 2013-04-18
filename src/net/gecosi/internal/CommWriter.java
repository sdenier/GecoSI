/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.internal;

import java.io.IOException;

/**
 * @author Simon Denier
 * @since Mar 15, 2013
 *
 */
public interface CommWriter {

	public void write(SiMessage message) throws IOException;

}
