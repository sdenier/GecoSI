/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

/**
 * @author Simon Denier
 * @since Apr 7, 2013
 *
 */
public class MockDataFrame extends SiAbstractDataFrame {

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

}