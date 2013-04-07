/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.dataframe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import net.gecosi.SiPunch;
import net.gecosi.dataframe.Si5DataFrame;
import net.gecosi.dataframe.SiDataFrame;
import net.gecosi.internal.SiMessage;

import org.junit.Test;

import test.net.gecosi.SiMessageFixtures;

/**
 * @author Simon Denier
 * @since Mar 15, 2013
 *
 */
public class Si5DataFrameTest {
	
	@Test
	public void getSiCardNumber() {
		assertThat(subject304243().getSiNumber(), equalTo("304243"));
		assertThat(subject36353().getSiNumber(), equalTo("36353"));
	}

	@Test
	public void getStartTime() {
		assertThat(subject36353().getStartTime(), equalTo(1234000L));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject36353().getFinishTime(), equalTo(4321000L));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject36353().getCheckTime(), equalTo(4444000L));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject36353().getNbPunches(), equalTo(36));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject36353().getPunches();
		assertThat(punches[0].code(), equalTo(36));
		assertThat(punches[0].timestamp(), equalTo(40059000L));
		assertThat(punches[9].code(), equalTo(59));
		assertThat(punches[9].timestamp(), equalTo(40104000L));
	}

	@Test
	public void getPunches_with36Punches() {
		SiPunch[] punches = subject36353().getPunches();
		assertThat(punches[30].code(), equalTo(31));
		assertThat(punches[30].timestamp(), equalTo(Si5DataFrame.NO_TIME));
		assertThat(punches[35].code(), equalTo(36));
		assertThat(punches[35].timestamp(), equalTo(Si5DataFrame.NO_TIME));
	}

	@Test
	public void getPunches_withZeroHourShift() {
		SiDataFrame subject = subject36353().startingAt(41400000L);
		assertThat(subject.getStartTime(), equalTo(44434000L));
		assertThat(subject.getFinishTime(), equalTo(47521000L));
		SiPunch[] punches = subject.getPunches();
		assertThat(punches[9].timestamp(), equalTo(83304000L));
		assertThat(punches[10].timestamp(), equalTo(Si5DataFrame.NO_TIME));
		assertThat(punches[35].timestamp(), equalTo(Si5DataFrame.NO_TIME));
	}

	private Si5DataFrame subject304243() {
		return new Si5DataFrame(SiMessageFixtures.sicard5_data);
	}

	private SiDataFrame subject36353() {
		return new Si5DataFrame(new SiMessage(new byte[] {
			0x02, (byte) 0xB1, (byte) 0x82, 0x00, 0x01, (byte) 0xAA, 0x2E, 0x00, 0x01, (byte) 0x8E, (byte) 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x65, 0x10, (byte) 0x93, (byte) 0x04, (byte) 0xD2, (byte) 0x10, (byte) 0xE1, 0x25, 0x56, (byte) 0x11, (byte) 0x5C, 0x28, 0x03,
			(byte) 0xA6, 0x00, 0x07, 0x1F, 0x24, (byte) 0x9C, 0x7B, 0x26, (byte) 0x9C, (byte) 0x8C, 0x22, (byte) 0x9C, (byte) 0x8D, 0x28, (byte) 0x9C,
			(byte) 0x8F, 0x34, (byte) 0x9C, (byte) 0x9B, 0x20, 0x36, (byte) 0x9C, (byte) 0x9F, 0x33, (byte) 0x9C, (byte) 0xA1, 0x35, (byte) 0x9C,
			(byte) 0xA2, 0x3C, (byte) 0x9C, (byte) 0xA7, 0x3B, (byte) 0x9C, (byte) 0xA8, 0x21, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE,
			(byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x22, 0x00, (byte) 0xEE,
			(byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE,
			0x23, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE,
			0x00,(byte) 0xEE, (byte) 0xEE, 0x24, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE,
			0x00, (byte) 0xEE, (byte) 0xEE, 0x00, (byte) 0xEE, (byte) 0xEE})).startingAt(0);
	}
	
}
