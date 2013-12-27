/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeoutException;

import net.gecosi.CommStatus;
import net.gecosi.SiHandler;
import net.gecosi.dataframe.Si5DataFrame;
import net.gecosi.dataframe.Si6DataFrame;
import net.gecosi.dataframe.Si8PlusDataFrame;
import net.gecosi.internal.CommWriter;
import net.gecosi.internal.GecoSILogger;
import net.gecosi.internal.InvalidMessage;
import net.gecosi.internal.SiDriverState;
import net.gecosi.internal.SiMessage;
import net.gecosi.internal.SiMessageQueue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import test.net.gecosi.SiMessageFixtures;

/**
 * @author Simon Denier
 * @since Mar 15, 2013
 *
 */
public class SiDriverStateTest {

	private SiMessageQueue queue;

	@Mock
	private CommWriter writer;
	
	@Mock
	private SiHandler siHandler;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		queue = new SiMessageQueue(10, 1);
		SiDriverState.setSicard6_192PunchesMode(false);
		GecoSILogger.open();
	}
	
	@Test
	public void STARTUP_CHECK() throws Exception {
		queue.add(SiMessageFixtures.startup_answer);
		SiDriverState nextState = SiDriverState.STARTUP_CHECK.receive(queue, writer, siHandler);

		assertThat(nextState, equalTo(SiDriverState.CONFIG_CHECK));
		verify(writer).write(SiMessage.get_protocol_configuration);
	}

	@Test(expected=TimeoutException.class)
	public void STARTUP_CHECK_throwsTimeoutException() throws Exception {
		SiDriverState.STARTUP_CHECK.receive(queue, writer, siHandler);
	}

	@Test(expected=InvalidMessage.class)
	public void STARTUP_CHECK_throwsInvalidMessage() throws Exception {
		queue.add(SiMessage.ack_sequence);
		SiDriverState.STARTUP_CHECK.receive(queue, writer, siHandler);
	}

	@Test
	public void CONFIG_CHECK() throws Exception {
		queue.add(SiMessageFixtures.ok_ext_protocol_answer);
		SiDriverState nextState = SiDriverState.CONFIG_CHECK.receive(queue, writer, siHandler);

		assertThat(nextState, equalTo(SiDriverState.SI6_CARDBLOCKS_SETTING));
	}

	@Test
	public void CONFIG_CHECK_failsOnExtendedProtocol() throws Exception {
		queue.add(SiMessageFixtures.no_ext_protocol_answer);
		SiDriverState nextState = SiDriverState.CONFIG_CHECK.receive(queue, writer, siHandler);

		assertThat(nextState, equalTo(SiDriverState.EXTENDED_PROTOCOL_ERROR));
	}

	@Test
	public void CONFIG_CHECK_failsOnHandshakeMode() throws Exception {
		queue.add(SiMessageFixtures.no_handshake_answer);
		SiDriverState nextState = SiDriverState.CONFIG_CHECK.receive(queue, writer, siHandler);

		assertThat(nextState, equalTo(SiDriverState.HANDSHAKE_MODE_ERROR));
	}
	
	@Test
	public void SI6_CARDBLOCKS_SETTING_64PunchesMode() throws Exception {
		queue.add(SiMessageFixtures.si6_64_punches_answer);
		SiDriverState nextState = SiDriverState.SI6_CARDBLOCKS_SETTING.receive(queue, writer, siHandler);

		verify(siHandler).notify(CommStatus.ON);
		assertFalse(SiDriverState.sicard6_192PunchesMode());
		assertThat(nextState, equalTo(SiDriverState.DISPATCH_READY));
	}

	@Test
	public void SI6_CARDBLOCKS_SETTING_192PunchesMode() throws Exception {
		queue.add(SiMessageFixtures.si6_192_punches_answer);
		SiDriverState nextState = SiDriverState.SI6_CARDBLOCKS_SETTING.receive(queue, writer, siHandler);

		verify(siHandler).notify(CommStatus.ON);
		assertTrue(SiDriverState.sicard6_192PunchesMode());
		assertThat(nextState, equalTo(SiDriverState.DISPATCH_READY));
	}

	@Test
	public void DISPATCH_READY() throws Exception {
		queue.add(SiMessageFixtures.sicard5_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(siHandler).notify(CommStatus.READY);
	}

	@Test
	public void DISPATCH_READY_dispatchesSiCard5() throws Exception {
		queue.add(SiMessageFixtures.sicard5_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_5);
	}
	
	@Test
	public void RETRIEVE_SICARD_5_DATA() throws Exception {
		queue.add(SiMessageFixtures.sicard5_data);
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_5_DATA.retrieve(queue, writer, siHandler);

		verify(writer).write(SiMessage.ack_sequence);
		verify(siHandler).notify(any(Si5DataFrame.class));
		assertThat(nextState, equalTo(SiDriverState.WAIT_SICARD_REMOVAL));
	}

	@Test
	public void RETRIEVE_SICARD_5_DATA_earlySiCardRemovalFallbackToDispatchReady() throws Exception {
		queue.add(SiMessageFixtures.sicard5_removed);
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_5_DATA.retrieve(queue, writer, siHandler);

		verify(siHandler).notify(CommStatus.PROCESSING_ERROR);
		assertThat(nextState, equalTo(SiDriverState.DISPATCH_READY));
	}

	@Test
	public void RETRIEVE_SICARD_5_DATA_timeoutFallbackToDispatchReady() throws Exception {
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_5_DATA.retrieve(queue, writer, siHandler);

		verify(siHandler).notify(CommStatus.PROCESSING_ERROR);
		assertThat(nextState, equalTo(SiDriverState.DISPATCH_READY));
	}

	@Test
	public void DISPATCH_READY_dispatchesSiCard6() throws Exception {
		queue.add(SiMessageFixtures.sicard6_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_6_b0);
	}

	@Test
	public void DISPATCH_READY_dispatchesSiCard6Star() throws Exception {
		queue.add(SiMessageFixtures.sicard6Star_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_6_b0);
	}

	@Test
	public void DISPATCH_READY_dispatchesSiCard6In192PunchesMode() throws Exception {
		SiDriverState.setSicard6_192PunchesMode(true);
		
		queue.add(SiMessageFixtures.sicard6_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_6_b0);
	}

	@Test
	public void RETRIEVE_SICARD_6_DATA() throws Exception {
		queue.add(SiMessageFixtures.sicard6_b0_data);
		queue.add(SiMessageFixtures.sicard6_b6_data);
		queue.add(SiMessageFixtures.sicard6_b7_data);
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_6_DATA.retrieve(queue, writer, siHandler);

		verify(writer).write(SiMessage.read_sicard_6_b0);
		verify(writer).write(SiMessage.read_sicard_6_b6);
		verify(writer, never()).write(SiMessage.read_sicard_6_b7);
		verify(writer).write(SiMessage.ack_sequence);
		verify(siHandler).notify(any(Si6DataFrame.class));
		assertThat(nextState, equalTo(SiDriverState.WAIT_SICARD_REMOVAL));
	}

	@Test
	public void RETRIEVE_SICARD_6_8BLOCKS_DATA() throws Exception {
		queue.add(SiMessageFixtures.sicard6_192p_b0_data);
		queue.add(SiMessageFixtures.sicard6_192p_b1_data);
		queue.add(SiMessageFixtures.sicard6_192p_b2_data);
		queue.add(SiMessageFixtures.sicard6_192p_b3_data);
		queue.add(SiMessageFixtures.sicard6_192p_b4_data);
		queue.add(SiMessageFixtures.sicard6_192p_b5_data);
		queue.add(SiMessageFixtures.sicard6_192p_b6_data);
		queue.add(SiMessageFixtures.sicard6_192p_b7_data);
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_6_DATA.retrieve(queue, writer, siHandler);

		InOrder inOrder = inOrder(writer);
		inOrder.verify(writer).write(SiMessage.read_sicard_6_b0);
		inOrder.verify(writer).write(SiMessage.read_sicard_6_b6);
		inOrder.verify(writer).write(SiMessage.read_sicard_6_b7);
		inOrder.verify(writer).write(SiMessage.read_sicard_6_plus_b2);
		inOrder.verify(writer).write(SiMessage.read_sicard_6_plus_b3);
		verify(writer, never()).write(SiMessage.read_sicard_6_plus_b4);
		verify(writer, never()).write(SiMessage.read_sicard_6_plus_b5);
		inOrder.verify(writer).write(SiMessage.ack_sequence);
		verify(siHandler).notify(any(Si6DataFrame.class));
		assertThat(nextState, equalTo(SiDriverState.WAIT_SICARD_REMOVAL));
	}
	
	@Test
	public void DISPATCH_READY_dispatchesSiCard8() throws Exception {
		queue.add(SiMessageFixtures.sicard8_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_8_plus_b0);
	}

	@Test
	public void RETRIEVE_SICARD_8_9_DATA() throws Exception {
		queue.add(SiMessageFixtures.sicard9_b0_data);
		queue.add(SiMessageFixtures.sicard9_b1_data);
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_8_9_DATA.retrieve(queue, writer, siHandler);

		verify(writer).write(SiMessage.read_sicard_8_plus_b0);
		verify(writer).write(SiMessage.read_sicard_8_plus_b1);
		verify(writer).write(SiMessage.ack_sequence);
		verify(siHandler).notify(any(Si8PlusDataFrame.class));
		assertThat(nextState, equalTo(SiDriverState.WAIT_SICARD_REMOVAL));
	}

	@Test
	public void DISPATCH_READY_dispatchesSiCard10() throws Exception {
		queue.add(SiMessageFixtures.sicard10_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_10_plus_b0);
	}

	@Test
	public void DISPATCH_READY_dispatchesSiCard11() throws Exception {
		queue.add(SiMessageFixtures.sicard11_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_10_plus_b0);
	}

	@Test
	public void DISPATCH_READY_dispatchesSiCard10PlusIn192PunchesMode() throws Exception {
		SiDriverState.setSicard6_192PunchesMode(true);
		
		queue.add(SiMessageFixtures.sicard10_detected);
		SiDriverState.DISPATCH_READY.receive(queue, writer, siHandler);
		verify(writer).write(SiMessage.read_sicard_10_plus_b0);
	}

	@Test
	public void RETRIEVE_SICARD_10_PLUS_DATA() throws Exception {
		queue.add(SiMessageFixtures.sicard10_b0_data);
		queue.add(SiMessageFixtures.sicard10_b4_data);
		queue.add(SiMessageFixtures.sicard10_b5_data);
		queue.add(SiMessageFixtures.sicard10_b6_data);
		queue.add(SiMessageFixtures.sicard10_b7_data);
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_10_PLUS_DATA.retrieve(queue, writer, siHandler);

		InOrder inOrder = inOrder(writer);
		inOrder.verify(writer).write(SiMessage.read_sicard_10_plus_b0);
		inOrder.verify(writer).write(SiMessage.read_sicard_10_plus_b4);
		verify(writer, never()).write(SiMessage.read_sicard_10_plus_b5);
		verify(writer, never()).write(SiMessage.read_sicard_10_plus_b6);
		verify(writer, never()).write(SiMessage.read_sicard_10_plus_b7);
		inOrder.verify(writer).write(SiMessage.ack_sequence);
		verify(siHandler).notify(any(Si8PlusDataFrame.class));
		assertThat(nextState, equalTo(SiDriverState.WAIT_SICARD_REMOVAL));
	}

	@Test
	public void RETRIEVE_SICARD_10_PLUS_DATA_192_MODE() throws Exception {
		queue.add(SiMessageFixtures.sicard10_b0_data);
		queue.add(SiMessageFixtures.sicard10_b1_data);
		queue.add(SiMessageFixtures.sicard10_b2_data);
		queue.add(SiMessageFixtures.sicard10_b3_data);
		queue.add(SiMessageFixtures.sicard10_b4_data);
		queue.add(SiMessageFixtures.sicard10_b5_data);
		queue.add(SiMessageFixtures.sicard10_b6_data);
		queue.add(SiMessageFixtures.sicard10_b7_data);
		SiDriverState nextState = SiDriverState.RETRIEVE_SICARD_10_PLUS_DATA.retrieve(queue, writer, siHandler);

		verify(writer).write(SiMessage.read_sicard_10_plus_b0);
		verify(writer).write(SiMessage.read_sicard_10_plus_b4);
		verify(writer, never()).write(SiMessage.read_sicard_10_plus_b5);
		verify(writer, never()).write(SiMessage.read_sicard_10_plus_b6);
		verify(writer, never()).write(SiMessage.read_sicard_10_plus_b7);
		verify(writer).write(SiMessage.ack_sequence);
		verify(siHandler).notify(any(Si8PlusDataFrame.class));
		assertThat(nextState, equalTo(SiDriverState.WAIT_SICARD_REMOVAL));
	}

	@Test
	public void WAIT_SICARD_REMOVAL() throws Exception {
		queue.add(SiMessageFixtures.sicard5_removed);
		SiDriverState nextState = SiDriverState.WAIT_SICARD_REMOVAL.receive(queue, writer, siHandler);

		assertThat(nextState, equalTo(SiDriverState.DISPATCH_READY));
	}

	@Test
	public void WAIT_SICARD_REMOVAL_timeoutFallbackToDispatchReady() throws Exception {
		SiDriverState nextState = SiDriverState.WAIT_SICARD_REMOVAL.receive(queue, writer, siHandler);

		assertThat(nextState, equalTo(SiDriverState.DISPATCH_READY));
	}

}
