/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.dataframe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static test.net.gecosi.SiMessageFixtures.sicard11_b0_data;
import static test.net.gecosi.SiMessageFixtures.sicard11_b4_data;
import static test.net.gecosi.SiMessageFixtures.sicard11_b5_data;
import static test.net.gecosi.SiMessageFixtures.sicard11_b6_data;
import static test.net.gecosi.SiMessageFixtures.sicard11_b7_data;
import net.gecosi.dataframe.Si8PlusDataFrame;
import net.gecosi.dataframe.SiDataFrame;
import net.gecosi.dataframe.SiPunch;
import net.gecosi.internal.SiMessage;

import org.junit.Test;

/**
 * @author Simon Denier
 * @since Apr 21, 2013
 *
 */
public class Si11DataFrameTest {
	
	@Test
	public void getSiCardNumber() {
		assertThat(subject9993810().getSiNumber(), equalTo("9993810"));
	}

	@Test
	public void getSiCardSeries() {
		assertThat(subject9993810().getSiSeries(), equalTo("SiCard 10/11/SIAC"));
	}
	
	@Test
	public void getStartTime() {
		assertThat(subject9993810().getStartTime(), equalTo(SiDataFrame.NO_TIME));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject9993810().getFinishTime(), equalTo(205497000L));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject9993810().getCheckTime(), equalTo(SiDataFrame.NO_TIME));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject9993810().getNbPunches(), equalTo(4));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject9993810().getPunches();
		assertThat(punches[0].code(), equalTo(38));
		assertThat(punches[0].timestamp(), equalTo(54903000L));
		assertThat(punches[1].code(), equalTo(42));
		assertThat(punches[1].timestamp(), equalTo(132543000L));
		assertThat(punches[2].code(), equalTo(38));
		assertThat(punches[2].timestamp(), equalTo(132554000L));
		assertThat(punches[3].code(), equalTo(32));
		assertThat(punches[3].timestamp(), equalTo(134912000L));
	}
	
	private SiDataFrame subject9993810() {
		return new Si8PlusDataFrame(new SiMessage[]{
				sicard11_b0_data, sicard11_b4_data, sicard11_b5_data, sicard11_b6_data, sicard11_b7_data}).startingAt(0);
	}

}
