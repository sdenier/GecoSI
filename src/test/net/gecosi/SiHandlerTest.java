/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi;

import static org.mockito.Mockito.verify;
import net.gecosi.SiHandler;
import net.gecosi.SiListener;
import net.gecosi.dataframe.Si5DataFrame;

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
		SiHandler siHandler = new SiHandler(listener);
		siHandler.setZeroHour(10000L);
		siHandler.notify(sicard5);
		verify(sicard5).startingAt(10000L);
	}
	
}
