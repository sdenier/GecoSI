/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import java.util.Arrays;

import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Mar 11, 2013
 *
 */
public class Si5DataFrame extends SiAbstractDataFrame {
	
	private static final int SI5_TIMED_PUNCHES = 30;

	public Si5DataFrame(SiMessage message) {
		this.dataFrame = extractDataFrame(message);
		this.siNumber = extractSiNumber();
	}

	protected byte[] extractDataFrame(SiMessage message) {
		return Arrays.copyOfRange(message.sequence(), 5, 133);
	}

	@Override
	public SiDataFrame startingAt(long zerohour) {
		startTime = advanceTimePast(rawStartTime(), zerohour);
		checkTime = advanceTimePast(rawCheckTime(), zerohour);
		long refTime = newRefTime(zerohour, startTime);
		punches = computeShiftedPunches(refTime);
		if( punches.length > 0) {
			SiPunch lastTimedPunch = punches[nbTimedPunches(punches) - 1];
			refTime = newRefTime(refTime, lastTimedPunch.timestamp());
		}
		finishTime = advanceTimePast(rawFinishTime(), refTime);
		return this;
	}
	
	public long advanceTimePast(long timestamp, long refTime) {
		return advanceTimePast(timestamp, refTime, TWELVE_HOURS);
	}

	private SiPunch[] computeShiftedPunches(long startTime) {
		int nbPunches = rawNbPunches();
		SiPunch[] punches = new SiPunch[nbPunches];
		int nbTimedPunches = nbTimedPunches(punches);
		long refTime = startTime;
		for (int i = 0; i < nbTimedPunches; i++) {
			// shift each punch time after the previous
			long punchTime = advanceTimePast(getPunchTime(i), refTime);
			punches[i] = new SiPunch(getPunchCode(i), punchTime);
			refTime = newRefTime(refTime, punchTime);
		}
		for (int i = 0; i < nbPunches - SI5_TIMED_PUNCHES; i++) {
			punches[i + SI5_TIMED_PUNCHES] = new SiPunch(getNoTimePunchCode(i), NO_TIME);
		}
		return punches;
	}

	private int nbTimedPunches(SiPunch[] punches) {
		return Math.min(punches.length, SI5_TIMED_PUNCHES);
	}

	protected String extractSiNumber() {
		int siNumber = wordAt(0x04);
		int cns = byteAt(0x06);
		if( cns > 0x01 ) {
			siNumber = siNumber + cns * 100000;
		}
		return Integer.toString(siNumber);
	}
	
	protected int rawNbPunches() {
		return byteAt(0x17) - 1;
	}

	private long rawStartTime() {
		return timestampAt(0x13);
	}

	private long rawFinishTime() {
		return timestampAt(0x15);
	}

	private long rawCheckTime() {
		return timestampAt(0x19);
	}

	protected int punchOffset(int i) {
		return 0x21 + (i / 5) * 0x10 + (i % 5) * 0x03;
	}
	
	protected int getPunchCode(int i) {
		return byteAt(punchOffset(i));
	}
	
	protected int getNoTimePunchCode(int i) {
		return byteAt(0x20 + i * 0x10);
	}	
	
	protected long getPunchTime(int i) {
		return timestampAt(punchOffset(i) + 1);
	}

	@Override
	public String getSiSeries() {
		return "SiCard 5";
	}

}
