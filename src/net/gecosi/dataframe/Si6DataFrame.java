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
		// TODO Auto-generated constructor stub
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
