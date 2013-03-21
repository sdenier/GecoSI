/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeoutException;

import net.gecosi.ICommWriter;
import net.gecosi.DriverState;
import net.gecosi.InvalidMessage;
import net.gecosi.SiHandler;
import net.gecosi.SiMessage;
import net.gecosi.SiMessageQueue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Simon Denier
 * @since Mar 15, 2013
 *
 */
public class SiDriverStateTest {

	private SiMessageQueue queue;

	@Mock
	private ICommWriter writer;
	
	@Mock
	private SiHandler siHandler;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		queue = new SiMessageQueue(5, 1);
	}
	
	@Test
	public void STARTUP_CHECK() throws Exception {
		SiMessage startup_answer = new SiMessage(new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03});
		queue.add(startup_answer);
		DriverState nextState = DriverState.STARTUP_CHECK.receive(queue, writer, siHandler);

		assertThat(nextState, equalTo(DriverState.CONFIG_CHECK));
		verify(writer).write_debug(SiMessage.get_protocol_configuration);
	}

	@Test(expected=TimeoutException.class)
	public void STARTUP_CHECK_throws_TimeoutException() throws Exception {
		DriverState.STARTUP_CHECK.receive(queue, writer, siHandler);
	}

	@Test(expected=InvalidMessage.class)
	public void STARTUP_CHECK_throws_InvalidMessage() throws Exception {
		queue.add(SiMessage.ack_sequence);
		DriverState.STARTUP_CHECK.receive(queue, writer, siHandler);
	}

}
