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
		assertThat(subject2005331().getStartTime(), equalTo(71950000L));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject2005331().getFinishTime(), equalTo(71992000L));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject2005331().getCheckTime(), equalTo(71949000L));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject2005331().getNbPunches(), equalTo(1));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject2005331().getPunches();
		assertThat(punches[0].code(), equalTo(31));
		assertThat(punches[0].timestamp(), equalTo(71970000L));
	}

	private Si8DataFrame subject2005331() {
		return new Si8DataFrame(new SiMessage[]{ SiMessageFixtures.sicard8_b0_data, SiMessageFixtures.sicard8_b1_data });
	}

}
