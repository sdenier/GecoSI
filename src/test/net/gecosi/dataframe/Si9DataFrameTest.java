/**
 * Copyright (c) 2013 Simon Denier
 */
package test.net.gecosi.dataframe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import net.gecosi.dataframe.Si8PlusDataFrame;
import net.gecosi.dataframe.SiPunch;
import net.gecosi.internal.SiMessage;

import org.junit.Test;

import test.net.gecosi.SiMessageFixtures;

/**
 * @author Simon Denier
 * @since Apr 11, 2013
 *
 */
public class Si9DataFrameTest {
	
	@Test
	public void getSiCardNumber() {
		assertThat(subject1061511().getSiNumber(), equalTo("1061511"));
	}

	@Test
	public void getSiCardSeries() {
		assertThat(subject1061511().getSiSeries(), equalTo("SiCard 9"));
	}

	@Test
	public void getStartTime() {
		assertThat(subject1061511().getStartTime(), equalTo(645361000L));
	}

	@Test
	public void getFinishTime() {
		assertThat(subject1061511().getFinishTime(), equalTo(648943000L));
	}

	@Test
	public void getCheckTime() {
		assertThat(subject1061511().getCheckTime(), equalTo(1249959000L));
	}

	@Test
	public void getNbPunches() {
		assertThat(subject1061511().getNbPunches(), equalTo(19));
	}
	
	@Test
	public void getPunches() {
		SiPunch[] punches = subject1061511().getPunches();
		assertThat(punches[0].code(), equalTo(134));
		assertThat(punches[0].timestamp(), equalTo(1855079000L));
		assertThat(punches[8].code(), equalTo(158));
		assertThat(punches[8].timestamp(), equalTo(1857067000L));
		assertThat(punches[18].code(), equalTo(200));
		assertThat(punches[18].timestamp(), equalTo(648931000L));
	}
	
	private Si8PlusDataFrame subject1061511() {
		return new Si8PlusDataFrame(new SiMessage[]{ SiMessageFixtures.sicard9_b0_data, SiMessageFixtures.sicard9_b1_data });
	}

}
