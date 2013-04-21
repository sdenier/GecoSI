/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;


/**
 * @author Simon Denier
 * @since Apr 7, 2013
 *
 */
public class MockDataFrame extends AbstractDataFrame {

	public MockDataFrame(String siNumber, long checkTime, long startTime, long finishTime, SiPunch[] punches) {
		this.siNumber = siNumber;
		this.checkTime = checkTime;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.punches = punches;
	}

	@Override
	public SiDataFrame startingAt(long zerohour) {
		return this;
	}

	@Override
	public String getSiSeries() {
		return "Mock Sicard";
	}
	
}
