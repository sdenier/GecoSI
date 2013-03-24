/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import net.gecosi.CommStatus;
import net.gecosi.SiDriver;
import net.gecosi.SiHandler;
import net.gecosi.SiMessage;
import net.gecosi.SiPort;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Simon Denier
 * @since Mar 15, 2013
 *
 */
public class SiDriverTest {

	private SiPort siPort;

	@Mock
	private SiHandler siHandler;

	private SiDriver driver;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void startupProtocol_succeeds() throws Exception {
		siPort = new MockCommPort(new SiMessage[]{ SiMessageFixtures.startup_answer, SiMessageFixtures.config_answer });
		driver = new SiDriver(siPort, siHandler);
		driver.start();
		Thread.sleep(100);
		driver.interrupt();
		
		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notify(CommStatus.READY);
	}

	@Test
	public void startupProtocol_failsOnTimeout() throws Exception {
		siPort = new MockCommPort();
		driver = new SiDriver(siPort, siHandler);
		driver.start();
		Thread.sleep(100);
		driver.interrupt();

		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notifyError(eq(CommStatus.ERROR), anyString());
		inOrder.verify(siHandler).notify(CommStatus.OFF);
	}

	@Test
	public void startupProtocol_failsOnExtendedProtocolCheck() throws Exception {
		siPort = new MockCommPort(new SiMessage[]{ SiMessageFixtures.startup_answer, SiMessageFixtures.no_ext_protocol_answer });
		driver = new SiDriver(siPort, siHandler);
		driver.start();
		Thread.sleep(100);
		driver.interrupt();

		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notifyError(eq(CommStatus.ERROR), anyString());
		inOrder.verify(siHandler).notify(CommStatus.OFF);
	}

}
