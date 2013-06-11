/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.adapter.rxtx;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import gnu.io.SerialPortEvent;
import net.gecosi.adapter.rxtx.RxtxCommReader;
import net.gecosi.internal.GecoSILogger;
import net.gecosi.internal.SiMessage;
import net.gecosi.internal.SiMessageQueue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Simon Denier
 * @since Jun 10, 2013
 *
 */
public class RxtxCommReaderTest {
	
	private MockInputStream inputStream;
	
	@Mock
	private SiMessageQueue messageQueue;

	@Mock
	private SerialPortEvent triggerEvent;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		GecoSILogger.open();
		inputStream = new MockInputStream();
	}
	
	public RxtxCommReader subject() {
		return new RxtxCommReader(inputStream, messageQueue);
	}
	
	@Test
	public void nomicalCase() {
		byte[] testInput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		testReaderOutput(new byte[][]{ testInput }, testInput);
	}

	@Test
	public void messageInTwoFragments() {
		byte[] testInput1 = new byte[]{0x02, (byte) 0xF0, 0x03};
		byte[] testInput2 = new byte[]{0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		byte[] testOutput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		testReaderOutput(new byte[][]{ testInput1, testInput2 }, testOutput);
	}

	@Test
	public void messageInMultipleFragments() {
		byte[] testInput1 = new byte[]{0x02, (byte) 0xF0, 0x03};
		byte[] testInput2 = new byte[]{0x00, 0x01, 0x4D, 0x0D};
		byte[] testInput3 = new byte[]{0x11, 0x03};
		byte[] testOutput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		testReaderOutput(new byte[][]{ testInput1, testInput2, testInput3 }, testOutput);
	}
	
	@Test
	public void firstFragmentWithoutLengthPrefix() {
		fail();
	}

	@Test
	public void shortMessage() {
		fail();
	}
	
	@Test
	public void timeoutError() {
		fail();
	}

	@Test
	public void tooLongFragmentError() {
		fail();
	}
	
	private void testReaderOutput(byte[][] testInputs, byte[] expectedOutput) {
		try {
			RxtxCommReader subject = subject();
			for (int i = 0; i < testInputs.length; i++) {
				inputStream.setInput(testInputs[i]);
				subject.serialEvent(triggerEvent);
			}
			ArgumentCaptor<SiMessage> message = ArgumentCaptor.forClass(SiMessage.class);
			verify(messageQueue).put(message.capture());
			assertThat(message.getValue().sequence(), equalTo(expectedOutput));
		} catch (InterruptedException e) {
			fail();
		}
	}

}
