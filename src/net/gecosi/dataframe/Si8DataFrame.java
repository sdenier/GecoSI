/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import java.util.Arrays;

import net.gecosi.SiPunch;
import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 10, 2013
 *
 */
public class Si8DataFrame extends SiAbstractDataFrame {

	private static final int PUNCHES_START = 34;

	public Si8DataFrame(SiMessage[] data_messages) {
		this.dataFrame  = extractDataFrame(data_messages);
		this.siNumber   = extractSiNumber();
		this.startTime  = extractStartTime();
		this.finishTime = extractFinishTime();
		this.checkTime  = extractCheckTime();
		this.punches    = extractPunches();
	}

	private byte[] extractDataFrame(SiMessage[] dataMessages) {
		byte[] dataFrame = Arrays.copyOfRange(dataMessages[0].sequence(), 6, 256 + 6);
		System.arraycopy(dataMessages[1].sequence(), 6, dataFrame, 128, 128);
		return dataFrame;
	}

	protected String extractSiNumber() {
		return Integer.toString(block3At(6 * PAGE_SIZE + 1));
	}

	protected long extractStartTime() {
		return extract24HourTime(3 * PAGE_SIZE);
	}

	protected long extractFinishTime() {
		return extract24HourTime(4 * PAGE_SIZE);
	}

	protected long extractCheckTime() {
		return extract24HourTime(2 * PAGE_SIZE);
	}
	
	protected int rawNbPunches() {
		return byteAt(5 * PAGE_SIZE + 2);
	}

	protected SiPunch[] extractPunches() {
		SiPunch[] punches = new SiPunch[rawNbPunches()];
		for (int i = 0; i < punches.length; i++) {
			int punchIndex = (PUNCHES_START + i) * PAGE_SIZE;
			punches[i] = new SiPunch(extractCode(punchIndex), extract24HourTime(punchIndex));
		}
		return punches;
	}

	protected int extractCode(int punchIndex) {
		int codeHigh = (byteAt(punchIndex) & 192) << 2;
		System.out.println(codeHigh);
		int code = codeHigh + byteAt(punchIndex + 1);
		return code;
	}
	
	protected long extract24HourTime(int pageStart) {
		int pmFlag = byteAt(pageStart) & 1;
		return shiftTime(timestampAt(pageStart + 2), TWELVE_HOURS * pmFlag);
	}

	@Override
	public SiDataFrame startingAt(long zerohour) {
		return this;
	}

}
