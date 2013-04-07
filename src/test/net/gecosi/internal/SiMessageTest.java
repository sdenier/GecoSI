/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.gecosi.internal.SiMessage;

import org.junit.Test;

/**
 * @author Simon Denier
 * @since Mar 15, 2013
 *
 */
public class SiMessageTest {

	private SiMessage message = new SiMessage(new byte[] {0x02, (byte) 0xF0, 0x01, 0x4D, 0x6D, 0x0A, 0x03} );
	private SiMessage crc_fail = new SiMessage(new byte[] {0x02, (byte) 0xF0, 0x01, 0x4D, 0x00, 0x00, 0x03} );
	private SiMessage bad_start = new SiMessage(new byte[] {0x00, (byte) 0xF0, 0x01, 0x4D, 0x00, 0x00, 0x03} );
	private SiMessage bad_end = new SiMessage(new byte[] {0x02, (byte) 0xF0, 0x01, 0x4D, 0x00, 0x00, 0x00} );
	private SiMessage bad_cmd = new SiMessage(new byte[] {0x02, (byte) 0x00, 0x01, 0x4D, 0x00, 0x00, 0x03} );

	@Test
	public void commandByte() {
		assertThat(new Byte(message.commandByte()), equalTo((byte) 0xF0));
	}

	@Test
	public void extractCRC() {
		assertThat(message.extractCRC(), equalTo(0x6D0A));
	}

	@Test
	public void computeCRC() {
		assertThat(message.computeCRC(), equalTo(0x6D0A));
	}

	@Test
	public void check() {
		assertThat(message.check((byte) 0xF0), is(true));
	}
	
	@Test
	public void check_failsOnCrcError() {
		assertThat(crc_fail.check((byte) 0xF0), is(false));
	}

	@Test
	public void check_failsOnBadStartOrBadEnd() {
		assertThat(bad_start.check((byte) 0xF0), is(false));
		assertThat(bad_end.check((byte) 0xF0), is(false));
	}

	@Test
	public void check_failsOnBadCommand() {
		assertThat(bad_cmd.check((byte) 0xF0), is(false));
	}

}
