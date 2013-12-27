/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.dataframe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static test.net.gecosi.SiMessageFixtures.sicard6_192p_b0_data;
import static test.net.gecosi.SiMessageFixtures.sicard6_192p_b2_data;
import static test.net.gecosi.SiMessageFixtures.sicard6_192p_b3_data;
import static test.net.gecosi.SiMessageFixtures.sicard6_192p_b6_data;
import static test.net.gecosi.SiMessageFixtures.sicard6_192p_b7_data;
import net.gecosi.dataframe.Si6DataFrame;
import net.gecosi.dataframe.SiDataFrame;
import net.gecosi.dataframe.SiPunch;
import net.gecosi.internal.SiMessage;

import org.junit.Test;

/**
 * @author Simon Denier
 * @since Apr 26, 2013
 *
 */
public class Si6_192PunchesDataFrameTest {
	
	@Test
	public void getSiCardNumber() {
		assertThat(subject821003_192p().getSiNumber(), equalTo("821003"));
	}

	@Test
	public void getSiCardSeries() {
		assertThat(subject821003_192p().getSiSeries(), equalTo("SiCard 6"));
	}

	@Test
	public void getStartTime() {
		assertThat(subject821003_192p().getStartTime(), equalTo(36752000L));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject821003_192p().getFinishTime(), equalTo(48684000L));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject821003_192p().getCheckTime(), equalTo(36750000L));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject821003_192p().getNbPunches(), equalTo(101));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject821003_192p().getPunches();
		
		assertThat(punches[0].code(), equalTo(31));
		assertThat(punches[0].timestamp(), equalTo(36762000L));
		assertThat(punches[1].code(), equalTo(32));
		assertThat(punches[1].timestamp(), equalTo(36764000L));
		assertThat(punches[32].code(), equalTo(33));
		assertThat(punches[32].timestamp(), equalTo(36851000L));
		assertThat(punches[63].code(), equalTo(34));
		assertThat(punches[63].timestamp(), equalTo(36940000L));
		assertThat(punches[99].code(), equalTo(35));
		assertThat(punches[99].timestamp(), equalTo(37040000L));
		
		assertThat(punches[100].code(), equalTo(634));
		assertThat(punches[100].timestamp(), equalTo(48625000L));
	}
	
	private SiDataFrame subject821003_192p() {
		return new Si6DataFrame(new SiMessage[]{ 	sicard6_192p_b0_data, sicard6_192p_b6_data, sicard6_192p_b7_data,
													sicard6_192p_b2_data, sicard6_192p_b3_data}).startingAt(0);
	}

}
