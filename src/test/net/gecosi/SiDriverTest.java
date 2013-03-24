/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import static org.mockito.Mockito.inOrder;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeoutException;

import net.gecosi.CommStatus;
import net.gecosi.InvalidMessage;
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
	public void startupProtocol_succeeds()
			throws TooManyListenersException, IOException, UnsupportedCommOperationException,
					InterruptedException, InvalidMessage, TimeoutException {
		siPort = new MockCommPort(new SiMessage[]{ SiMessageFixtures.startup_answer });
		driver = new SiDriver(siPort, siHandler);
		driver.start();
		Thread.sleep(100);
		driver.interrupt();
		
		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notify(CommStatus.READY);
	}

	@Test
	public void startupProtocol_failsOnTimeout()
			throws TooManyListenersException, IOException, UnsupportedCommOperationException,
					InterruptedException, InvalidMessage, TimeoutException {
		siPort = new MockCommPort();
		driver = new SiDriver(siPort, siHandler);
		driver.start();
		Thread.sleep(100);
		driver.interrupt();

		InOrder inOrder = inOrder(siHandler);
		inOrder.verify(siHandler).notify(CommStatus.STARTING);
		inOrder.verify(siHandler).notify(CommStatus.OFF);
	}

}
