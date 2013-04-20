/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import net.gecosi.SiPunch;
import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 20, 2013
 *
 */
public class Si10SeriesDataFrame extends SiAbstractDataFrame {

	public Si10SeriesDataFrame(SiMessage[] data_messages) {
		this.punches = new SiPunch[0];
	}
	
	@Override
	public SiDataFrame startingAt(long zerohour) {
		return this;
	}

	@Override
	public String sicardSeries() {
		return "SiCard 10/11/SIAC";
	}

}
