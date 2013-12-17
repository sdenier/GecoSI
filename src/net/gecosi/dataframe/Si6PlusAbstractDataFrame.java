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
		this.siNumber  = extractSiNumber();
	}

	protected byte[] extractDataFrame(SiMessage[] dataMessages) {
		byte[] dataFrame = Arrays.copyOfRange(dataMessages[0].sequence(), 6, dataMessages.length * 128 + 6);
		for (int i = 1; i < dataMessages.length; i++) {
			System.arraycopy(dataMessages[i].sequence(), 6, dataFrame, i * 128, 128);
		}
		return dataFrame;
	}

	@Override
	public SiDataFrame startingAt(long zerohour) {
		startTime	= advanceTimePast(extractStartTime(), zerohour);
		checkTime	= advanceTimePast(extractCheckTime(), zerohour);
		long refTime = newRefTime(zerohour, startTime);
		punches	= extractPunches(refTime);
		if( punches.length > 0 ) {
			SiPunch lastPunch = punches[punches.length - 1];
			refTime = newRefTime(refTime, lastPunch.timestamp());
		}
		finishTime	= advanceTimePast(extractFinishTime(), refTime);
		return this;
	}

	public long advanceTimePast(long timestamp, long refTime) {
		return advanceTimePast(timestamp, refTime, ONE_DAY);
	}
	
	protected String extractSiNumber() {
		return Integer.toString(block3At(siNumberIndex()));
	}

	protected long extractStartTime() {
		return extractFullTime(startTimeIndex());
	}

	protected long extractFinishTime() {
		return extractFullTime(finishTimeIndex());
	}

	protected long extractCheckTime() {
		return extractFullTime(checkTimeIndex());
	}

	protected int rawNbPunches() {
		return byteAt(nbPunchesIndex());
	}

	protected long extractFullTime(int pageStart) {
//		int tdByte = byteAt(pageStart);
//		int weekCounter = (tdByte & 48) >> 4;
//		int numDay = (tdByte & 14) >> 1;
		int pmFlag = byteAt(pageStart) & 1;
		return computeFullTime(pmFlag, timestampAt(pageStart + 2));
	}
	
	public long computeFullTime(int pmFlag, long twelveHoursTime) {
		if( twelveHoursTime == NO_SI_TIME ) {
			return NO_SI_TIME;
		}
		return pmFlag * TWELVE_HOURS + twelveHoursTime;
	}
	
	protected int extractCode(int punchIndex) {
		int codeHigh = (byteAt(punchIndex) & 192) << 2;
		int code = codeHigh + byteAt(punchIndex + 1);
		return code;
	}

	protected abstract int siNumberIndex();

	protected abstract int startTimeIndex();

	protected abstract int finishTimeIndex();

	protected abstract int checkTimeIndex();

	protected abstract int nbPunchesIndex();

	protected abstract SiPunch[] extractPunches(long startTime);

}