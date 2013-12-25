/**
 * Copyright (c) 2013 Simon Denier
 */
package test.de.sportident;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.sportident.CRCCalculator;

/**
 * @author Simon Denier
 * @since Feb 16, 2013
 *
 */
public class CRCCalculatorTest {

	@Test
	public void testSampleCrc() {
		byte[] sample = new byte[] {
				(byte) 0x53, (byte) 0x00, (byte) 0x05, (byte) 0x01,
				(byte) 0x0F, (byte) 0xB5, (byte) 0x00, (byte) 0x00,
				(byte) 0x1E, (byte) 0x08
		};
		int expected_crc = 0x2C12;
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}
	
	@Test
	public void testStartupCrc() {
		int expected_crc = 0x6D0A;
		byte[] sample = new byte[] { (byte) 0xF0, 0x01, 0x4D };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));		
	}

	@Test
	public void testAnswerStartupCrc() {
		int expected_crc = 0x8B12;
		byte[] sample = new byte[] { (byte) 0xF0, 0x03, 0x01, 0x01, 0x4D };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));		
	}
	
	@Test
	public void test2AnswerStartupCrc() {
		int expected_crc = 0x0D11;
		byte[] sample = new byte[] { (byte) 0xF0, 0x03, 0x00, 0x01, 0x4D };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

	@Test
	public void testGetProtocolModeCrc() {
		int expected_crc = 0x0414;
		byte[] sample = new byte[] { (byte) 0x83, 0x02, 0x74, 0x01 };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

	@Test
	public void testGetCardBlocksCrc() {
		int expected_crc = 0x1611;
		byte[] sample = new byte[] { (byte) 0x83, 0x02, 0x33, 0x01 };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

	@Test
	public void testReadSiCard5() {
		int expected_crc = 0xB100;
		byte[] sample = new byte[] { (byte) 0xB1, 0x00 };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

	@Test
	public void testReadSiCard6Bx() {
		int[] expected_crc = new int[] { 0x460A, 0x440A, 0x450A, 0x420A, 0x430A, 0x400A, 0x410A };
		byte[] sample_byte = new byte[] { 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
		for (int i = 0; i < expected_crc.length; i++) {
			byte[] sample = new byte[] { (byte) 0xE1, 0x01, sample_byte[i] };
			assertThat("CRC failed on Block " + sample_byte[i], CRCCalculator.crc(sample), equalTo(expected_crc[i]));
		}
	}

	@Test
	public void testReadSiCard6B8() {
		int expected_crc = 0x4E0A;
		byte[] sample = new byte[] { (byte) 0xE1, 0x01, 0x08 };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

	@Test
	public void testReadSiCard8B0() {
		int expected_crc = 0xE209;
		byte[] sample = new byte[] { (byte) 0xEF, 0x01, 0x00 };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

	@Test
	public void testReadSiCard8B1() {
		int expected_crc = 0xE309;
		byte[] sample = new byte[] { (byte) 0xEF, 0x01, 0x01 };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

	@Test
	public void testReadSiCard8Bx() {
		int[] expected_crc = new int[] { 0xE609, 0xE709, 0xE409, 0xE509 };
		byte[] sample_byte = new byte[] { 0x04, 0x05, 0x06, 0x07 };
		for (int i = 0; i < expected_crc.length; i++) {
			byte[] sample = new byte[] { (byte) 0xEF, 0x01, sample_byte[i] };
			assertThat("CRC failed on Block " + sample_byte[i], CRCCalculator.crc(sample), equalTo(expected_crc[i]));
		}
	}

	@Test
	public void testReadSiCard10B8() {
		int expected_crc = 0xEA09;
		byte[] sample = new byte[] { (byte) 0xEF, 0x01, 0x08 };
		assertThat(CRCCalculator.crc(sample), equalTo(expected_crc));
	}

}
