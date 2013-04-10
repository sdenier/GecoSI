/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.dataframe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import net.gecosi.SiPunch;
import net.gecosi.dataframe.Si8DataFrame;
import net.gecosi.internal.SiMessage;

import org.junit.Test;

import test.net.gecosi.SiMessageFixtures;

/**
 * @author Simon Denier
 * @since Apr 10, 2013
 *
 */
public class Si8DataFrameTest {
	
	@Test
	public void getSiCardNumber() {
		assertThat(subject2005331().getSiNumber(), equalTo("2005331"));
	}

	@Test
	public void getStartTime() {
		assertThat(subject2005331().getStartTime(), equalTo(1234000L));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject2005331().getFinishTime(), equalTo(4321000L));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject2005331().getCheckTime(), equalTo(4444000L));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject2005331().getNbPunches(), equalTo(4));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject2005331().getPunches();
		assertThat(punches[0].code(), equalTo(36));
		assertThat(punches[0].timestamp(), equalTo(40059000L));
		assertThat(punches[9].code(), equalTo(59));
		assertThat(punches[9].timestamp(), equalTo(40104000L));
	}

	private Si8DataFrame subject2005331() {
		return new Si8DataFrame(new SiMessage[]{ SiMessageFixtures.sicard8_b0_data, SiMessageFixtures.sicard8_b1_data });
	}

}
