/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.dataframe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static test.net.gecosi.SiMessageFixtures.sicard6_b0_data;
import static test.net.gecosi.SiMessageFixtures.sicard6_b6_data;
import static test.net.gecosi.SiMessageFixtures.sicard6_b7_data;
import net.gecosi.dataframe.Si6DataFrame;
import net.gecosi.dataframe.SiDataFrame;
import net.gecosi.dataframe.SiPunch;
import net.gecosi.internal.SiMessage;

import org.junit.Test;

/**
 * @author Simon Denier
 * @since Apr 25, 2013
 *
 */
public class Si6DataFrameTest {
	
	@Test
	public void getSiCardNumber() {
		assertThat(subject821003().getSiNumber(), equalTo("821003"));
	}

	@Test
	public void getSiCardSeries() {
		assertThat(subject821003().getSiSeries(), equalTo("SiCard 6"));
	}

	@Test
	public void getStartTime() {
		assertThat(subject821003().getStartTime(), equalTo(35565000L));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject821003().getFinishTime(), equalTo(35652000L));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject821003().getCheckTime(), equalTo(35563000L));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject821003().getNbPunches(), equalTo(5));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject821003().getPunches();
		assertThat(punches[0].code(), equalTo(31));
		assertThat(punches[0].timestamp(), equalTo(35589000L));
		assertThat(punches[1].code(), equalTo(32));
		assertThat(punches[1].timestamp(), equalTo(35602000L));
		assertThat(punches[4].code(), equalTo(35));
		assertThat(punches[4].timestamp(), equalTo(35635000L));
	}
	
	private SiDataFrame subject821003() {
		return new Si6DataFrame(new SiMessage[]{ sicard6_b0_data, sicard6_b6_data, sicard6_b7_data }).startingAt(0);
	}

}
