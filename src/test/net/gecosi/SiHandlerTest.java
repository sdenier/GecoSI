/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import static org.mockito.Mockito.verify;
import net.gecosi.Si5DataFrame;
import net.gecosi.SiHandler;
import net.gecosi.SiListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Simon Denier
 * @since Apr 3, 2013
 *
 */
public class SiHandlerTest {

	@Mock
	private SiListener listener;
	
	@Mock
	private Si5DataFrame sicard5;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void notifySiCard5() {
		new SiHandler(listener, 10000L).notify(sicard5);
		verify(sicard5).compute24HourTimes(10000L);
	}
	
}
