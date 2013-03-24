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

	public final static SiMessage config_answer = new SiMessage(new byte[]{0x02, (byte) 0x83, 0x04, 0x00, 0x01, 0x74, 0x05, (byte) 0xB1, 0x64, 0x03});

	public final static SiMessage no_ext_protocol_answer = new SiMessage(new byte[]{0x02, (byte) 0x83, 0x04, 0x00, 0x01, 0x74, 0x04, (byte) 0x31, 0x61, 0x03});
}
