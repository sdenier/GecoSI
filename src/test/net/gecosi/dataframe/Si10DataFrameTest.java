/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.dataframe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static test.net.gecosi.SiMessageFixtures.sicard10_b0_data;
import static test.net.gecosi.SiMessageFixtures.sicard10_b4_data;
import static test.net.gecosi.SiMessageFixtures.sicard10_b5_data;
import static test.net.gecosi.SiMessageFixtures.sicard10_b6_data;
import static test.net.gecosi.SiMessageFixtures.sicard10_b7_data;
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
public class Si10DataFrameTest {
	
	@Test
	public void getSiCardNumber() {
		assertThat(subject7773810().getSiNumber(), equalTo("7773810"));
	}

	@Test
	public void getSiCardSeries() {
		assertThat(subject7773810().getSiSeries(), equalTo("SiCard 10/11/SIAC"));
	}
	
	@Test
	public void getStartTime() {
		assertThat(subject7773810().getStartTime(), equalTo(SiDataFrame.NO_TIME));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject7773810().getFinishTime(), equalTo(SiDataFrame.NO_TIME));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject7773810().getCheckTime(), equalTo(SiDataFrame.NO_TIME));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject7773810().getNbPunches(), equalTo(3));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject7773810().getPunches();
		assertThat(punches[0].code(), equalTo(42));
		assertThat(punches[0].timestamp(), equalTo(45706000L));
		assertThat(punches[1].code(), equalTo(38));
		assertThat(punches[1].timestamp(), equalTo(45713000L));
		assertThat(punches[2].code(), equalTo(32));
		assertThat(punches[2].timestamp(), equalTo(48397000L));
	}
	
	private SiDataFrame subject7773810() {
		return new Si8PlusDataFrame(new SiMessage[]{
				sicard10_b0_data, sicard10_b4_data, sicard10_b5_data, sicard10_b6_data, sicard10_b7_data}).startingAt(0);
	}

}
