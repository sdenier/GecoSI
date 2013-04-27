/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import java.util.Arrays;

import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 25, 2013
 *
 */
public abstract class Si6PlusAbstractDataFrame extends SiAbstractDataFrame {

	public Si6PlusAbstractDataFrame(SiMessage[] dataMessages) {
		this.dataFrame = extractDataFrame(dataMessages);
	}

	protected byte[] extractDataFrame(SiMessage[] dataMessages) {
		byte[] dataFrame = Arrays.copyOfRange(dataMessages[0].sequence(), 6, dataMessages.length * 128 + 6);
		for (int i = 1; i < dataMessages.length; i++) {
			System.arraycopy(dataMessages[i].sequence(), 6, dataFrame, i * 128, 128);
		}
		return dataFrame;
	}

	protected void initializeDataFields() {
		this.siNumber	= extractSiNumber();
		this.startTime	= extractStartTime();
		this.finishTime	= extractFinishTime();
		this.checkTime	= extractCheckTime();
		this.punches	= extractPunches();
	}

	protected String extractSiNumber() {
		return Integer.toString(block3At(siNumberIndex()));
	}

	protected long extractStartTime() {
		return extract24HourTime(startTimeIndex());
	}

	protected long extractFinishTime() {
		return extract24HourTime(finishTimeIndex());
	}

	protected long extractCheckTime() {
		return extract24HourTime(checkTimeIndex());
	}

	protected int rawNbPunches() {
		return byteAt(nbPunchesIndex());
	}

	protected long extract24HourTime(int pageStart) {
		int pmFlag = byteAt(pageStart) & 1;
		return shiftTime(timestampAt(pageStart + 2), TWELVE_HOURS * pmFlag);
	}
	
	protected int extractCode(int punchIndex) {
		int codeHigh = (byteAt(punchIndex) & 192) << 2;
		int code = codeHigh + byteAt(punchIndex + 1);
		return code;
	}

	@Override
	public SiDataFrame startingAt(long zerohour) {
		return this;
	}

	protected abstract int siNumberIndex();

	protected abstract int startTimeIndex();

	protected abstract int finishTimeIndex();

	protected abstract int checkTimeIndex();

	protected abstract int nbPunchesIndex();

	protected abstract SiPunch[] extractPunches();

}