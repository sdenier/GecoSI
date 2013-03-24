/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import net.gecosi.SiMessage;

/**
 * @author Simon Denier
 * @since Mar 24, 2013
 *
 */
public class SiMessageFixtures {

	public final static SiMessage startup_answer = new SiMessage(new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03});

}
