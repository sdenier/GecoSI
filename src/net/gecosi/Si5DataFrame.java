/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.util.Arrays;

/**
 * @author Simon Denier
 * @since Mar 11, 2013
 *
 */
public class Si5DataFrame {

	public static long NO_TIME = 1000L * 0xEEEE;

	private static final int SI5_TIMED_PUNCHES = 30;
	
	private int siNumber;

	private byte[] dataFrame;

	public Si5DataFrame(SiMessage message) {
		this.dataFrame = extractDataFrame(message);
		this.siNumber = extractSiNumber();
	}

	protected byte[] extractDataFrame(SiMessage message) {
		return Arrays.copyOfRange(message.sequence(), 5, 133);
	}

	protected int byteAt(int i) {
		return dataFrame[i] & 0xFF;
	}
	
	protected int wordAt(int i) {
		return byteAt(i) << 8 | byteAt(i + 1);
	}

	protected long timestampAt(int i) {
		return 1000L * wordAt(i);
	}

	protected int extractSiNumber() {
		int siNumber = wordAt(0x04);
		int cns = byteAt(0x06);
		if( cns > 0x01 ) {
			siNumber = siNumber + cns * 100000;
		}
		return siNumber;
	}

	public int getSiNumber() {
		return siNumber;
	}

	public void compute24HourTimes(long zerohour) {
		// TODO Auto-generated method stub
		
	}

	public long getStartTime() {
		return timestampAt(0x13);
	}

	public long getFinishTime() {
		return timestampAt(0x15);
	}

	public long getCheckTime() {
		return timestampAt(0x19);
	}

	public int getNbPunches() {
		return byteAt(0x17) - 1;
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

	public SiPunch[] getPunches(long zeroHour) {
		SiPunch[] punches = new SiPunch[getNbPunches()];
		int allPunches = getNbPunches();
		int timedPunches = Math.min(allPunches, SI5_TIMED_PUNCHES);
		for (int i = 0; i < timedPunches; i++) {
			punches[i] = new SiPunch(getPunchCode(i), getPunchTime(i));
		}
		for (int i = 0; i < allPunches - SI5_TIMED_PUNCHES; i++) {
			punches[i + SI5_TIMED_PUNCHES] = new SiPunch(getNoTimePunchCode(i), NO_TIME);
		}
		return punches;
	}
	
	public String formatTime(long timestamp) {
		if( timestamp == NO_TIME ) {
			return "no time";
		} else {
			long seconds = timestamp / 1000;
			return String.format("%d:%02d:%02d", seconds/3600, (seconds%3600)/60, (seconds%60));
		}
	}
	
	public void printString() {
		System.out.println("It's aliiive!");
		System.out.format("SiCard5: %s ", getSiNumber());
		System.out.format("(Start: %s ", formatTime(getStartTime()));
		System.out.format(" - Finish: %s", formatTime(getFinishTime()));
		System.out.format(" - Check: %s)%n", formatTime(getCheckTime()));
		System.out.format("Punches: %s %n", getNbPunches());
		for (int i = 0; i < getNbPunches(); i++) {
			System.out.format("%s: %s %s - ", i, getPunchCode(i), formatTime(getPunchTime(i)));
		}
		System.out.println();
	}

}
