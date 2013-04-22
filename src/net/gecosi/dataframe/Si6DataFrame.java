/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 22, 2013
 *
 */
public class Si6DataFrame extends SiAbstractDataFrame {

	public Si6DataFrame(SiMessage[] dataMessages) {
		this.dataFrame	= extractDataFrame(dataMessages);
		this.siNumber	= "N/A";
		this.startTime	= NO_TIME;
		this.finishTime	= NO_TIME;
		this.checkTime	= NO_TIME;
		this.punches	= new SiPunch[0];
	}

	@Override
	public SiDataFrame startingAt(long zerohour) {
		return this;
	}

	@Override
	public String getSiSeries() {
		return "SiCard 6";
	}

}
