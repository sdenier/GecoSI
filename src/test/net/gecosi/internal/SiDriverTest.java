/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import net.gecosi.CommStatus;
import net.gecosi.SiHandler;
import net.gecosi.dataframe.Si5DataFrame;
import net.gecosi.internal.SiDriver;
import net.gecosi.internal.SiMessage;
import net.gecosi.internal.SiPort;

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
public class SiDriverTest {

	private SiPort siPort;

	@Mock
	private SiHandler siHandler;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private void testRunDriver(SiDriver driver) throws InterruptedException {
		driver.start();
		Thread.sleep(100);
		driver.interrupt();
	}
	
	@Test
	public void startupProtocol_succeeds() throws Exception {
		siPort = new MockCommPort(new SiMessage[]{ SiMessageFixtures.startup_answer, SiMessageFixtures.config_answer });
		testRunDriver(new SiDriver(siPort, siHandler));
		
		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notify(CommStatus.READY);
	}

	@Test
	public void startupProtocol_failsOnTimeout() throws Exception {
		siPort = new MockCommPort();
		testRunDriver(new SiDriver(siPort, siHandler));

		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notifyError(eq(CommStatus.FATAL_ERROR), anyString());
		inOrder.verify(siHandler).notify(CommStatus.OFF);
	}

	@Test
	public void startupProtocol_failsOnExtendedProtocolCheck() throws Exception {
		siPort = new MockCommPort(new SiMessage[]{ SiMessageFixtures.startup_answer, SiMessageFixtures.no_ext_protocol_answer });
		testRunDriver(new SiDriver(siPort, siHandler));

		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notifyError(eq(CommStatus.FATAL_ERROR), anyString());
		inOrder.verify(siHandler).notify(CommStatus.OFF);
	}

	@Test
	public void readSiCard5() throws Exception {
		siPort = new MockCommPort(new SiMessage[]{  SiMessageFixtures.startup_answer, SiMessageFixtures.config_answer,
													SiMessageFixtures.sicard5_detected, SiMessageFixtures.sicard5_data,
													SiMessageFixtures.sicard5_removed });
		testRunDriver(new SiDriver(siPort, siHandler));

		verify(siHandler).notify(any(Si5DataFrame.class));
	}
	
	@Test
	public void siCard5_removedBeforeRead() throws Exception {
		siPort = new MockCommPort(new SiMessage[]{  SiMessageFixtures.startup_answer, SiMessageFixtures.config_answer,
													SiMessageFixtures.sicard5_detected, SiMessageFixtures.sicard5_removed });
		testRunDriver(new SiDriver(siPort, siHandler));

		verify(siHandler).notify(CommStatus.PROCESSING_ERROR);
	}
		
}
