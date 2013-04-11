/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import java.util.Arrays;

import net.gecosi.SiPunch;
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
		long zeroHourShift = computeZeroHourShift(rawStartTime(), zerohour);
		startTime = shiftTime(rawStartTime(), zeroHourShift);
		finishTime = shiftTime(rawFinishTime(), zeroHourShift);
		checkTime = shiftTime(rawCheckTime(), zeroHourShift);
		punches = computeShiftedPunches(zeroHourShift);
		return this;
	}
	
	private long computeZeroHourShift(long reftime, long zerohour) {
		long shift = 0;
		while( reftime + shift < zerohour ){
			shift += TWELVE_HOURS;
		}
		return shift;
	}

	private SiPunch[] computeShiftedPunches(long zeroHourShift) {
		int nbPunches = rawNbPunches();
		SiPunch[] punches = new SiPunch[nbPunches];
		int nbTimedPunches = Math.min(nbPunches, SI5_TIMED_PUNCHES);
		for (int i = 0; i < nbTimedPunches; i++) {
			punches[i] = new SiPunch(getPunchCode(i), shiftTime(getPunchTime(i), zeroHourShift));
		}
		for (int i = 0; i < nbPunches - SI5_TIMED_PUNCHES; i++) {
			punches[i + SI5_TIMED_PUNCHES] = new SiPunch(getNoTimePunchCode(i), NO_TIME);
		}
		return punches;
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

}
