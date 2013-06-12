/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.adapter.rxtx;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
		testReaderOutput(new byte[][]{ testInput }, testInput, subject());
	}

	@Test
	public void messageInTwoFragments() {
		byte[] testInput1 = new byte[]{0x02, (byte) 0xF0, 0x03};
		byte[] testInput2 = new byte[]{0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		byte[] testOutput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		testReaderOutput(new byte[][]{ testInput1, testInput2 }, testOutput, subject());
	}

	@Test
	public void messageInMultipleFragments() {
		byte[] testInput1 = new byte[]{0x02, (byte) 0xF0, 0x03};
		byte[] testInput2 = new byte[]{0x00, 0x01, 0x4D, 0x0D};
		byte[] testInput3 = new byte[]{0x11, 0x03};
		byte[] testOutput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		testReaderOutput(new byte[][]{ testInput1, testInput2, testInput3 }, testOutput, subject());
	}
	
	@Test
	public void firstFragmentWithoutLengthPrefix() {
		byte[] testInput1 = new byte[]{0x02};
		byte[] testInput2 = new byte[]{(byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D};
		byte[] testInput3 = new byte[]{0x11, 0x03};
		byte[] testOutput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
		testReaderOutput(new byte[][]{ testInput1, testInput2, testInput3 }, testOutput, subject());
	}

	@Test
	public void zeroDataMessage() {
		byte[] testInput1 = new byte[]{0x02};
		byte[] testInput2 = new byte[]{(byte) 0xF0, 0x00, (byte) 0xF0, 0x00, 0x03};
		byte[] testOutput = new byte[]{0x02, (byte) 0xF0, 0x00, (byte) 0xF0, 0x00, 0x03};
		testReaderOutput(new byte[][]{ testInput1, testInput2 }, testOutput, subject());
	}
	
	@Test
	public void emptyMessage() {
		RxtxCommReader subject = subject();
		inputStream.setInput(new byte[0]);
		subject.serialEvent(triggerEvent);
		verifyZeroInteractions(messageQueue);
	}
	
	@Test
	public void shortMessage() {
		byte[] testInput = new byte[]{0x15};
		testReaderOutput(new byte[][]{ testInput }, testInput, subject());
	}
	
	@Test
	public synchronized void timeoutResetsAccumulator() {
		try {
			RxtxCommReader subject = new RxtxCommReader(inputStream, messageQueue, 1);
			byte[] testInput1 = new byte[]{0x02, (byte) 0xF0, 0x03};
			inputStream.setInput(testInput1);
			subject.serialEvent(triggerEvent);

			wait(2);
			byte[] testInput2 = new byte[]{0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
			inputStream.setInput(testInput2);
			subject.serialEvent(triggerEvent);
		
			verifyZeroInteractions(messageQueue);
			
			wait(2);
			byte[] testInput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
			testReaderOutput(new byte[][]{ testInput }, testInput, subject);
		} catch (InterruptedException e) {
			fail();
		}
	}

	@Test
	public synchronized void tooLongFragmentResetsAccumulator() {
		RxtxCommReader subject = new RxtxCommReader(inputStream, messageQueue, 1);
		byte[] testInput1 = new byte[]{0x02, (byte) 0xF0, 0x03};
		inputStream.setInput(testInput1);
		subject.serialEvent(triggerEvent);

		byte[] testInput2 = new byte[]{0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03, (byte) 0xFF};
		inputStream.setInput(testInput2);
		subject.serialEvent(triggerEvent);
		
		verifyZeroInteractions(messageQueue);
		
		try {
			wait(2);
			byte[] testInput = new byte[]{0x02, (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D, 0x0D, 0x11, 0x03};
			testReaderOutput(new byte[][]{ testInput }, testInput, subject);
		} catch (InterruptedException e) {
			fail();
		}
	}
	
	private void testReaderOutput(byte[][] testInputs, byte[] expectedOutput, RxtxCommReader subject) {
		try {
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
