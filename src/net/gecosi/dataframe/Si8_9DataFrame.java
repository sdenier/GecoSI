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
public class Si8_9DataFrame extends SiAbstractDataFrame {

	private static final int SINUMBER_PAGE = 6 * PAGE_SIZE;

	private static final int SICARD8_PUNCHES_START = 34;
	
	private static final int SICARD9_PUNCHES_START = 14;

	public Si8_9DataFrame(SiMessage[] data_messages) {
		this.dataFrame    = extractDataFrame(data_messages);
		this.siNumber     = extractSiNumber();
		this.startTime    = extractStartTime();
		this.finishTime   = extractFinishTime();
		this.checkTime    = extractCheckTime();
		this.punches      = extractPunches();
	}

	private byte[] extractDataFrame(SiMessage[] dataMessages) {
		byte[] dataFrame = Arrays.copyOfRange(dataMessages[0].sequence(), 6, 256 + 6);
		System.arraycopy(dataMessages[1].sequence(), 6, dataFrame, 128, 128);
		return dataFrame;
	}

	protected String extractSiNumber() {
		return Integer.toString(block3At(SINUMBER_PAGE + 1));
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

	private int getPunchesStartIndex() {
		return (byteAt(SINUMBER_PAGE) & 3) == 2 ? SICARD8_PUNCHES_START : SICARD9_PUNCHES_START;
	}
	
	protected int rawNbPunches() {
		return byteAt(5 * PAGE_SIZE + 2);
	}

	protected SiPunch[] extractPunches() {
		SiPunch[] punches = new SiPunch[rawNbPunches()];
		int punchesStart = getPunchesStartIndex();
		for (int i = 0; i < punches.length; i++) {
			int punchIndex = (punchesStart + i) * PAGE_SIZE;
			punches[i] = new SiPunch(extractCode(punchIndex), extract24HourTime(punchIndex));
		}
		return punches;
	}

	protected int extractCode(int punchIndex) {
		int codeHigh = (byteAt(punchIndex) & 192) << 2;
		int code = codeHigh + byteAt(punchIndex + 1); // TODO check/test
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

	@Override
	public String sicardSeries() {
		return "SiCard 8-9";
	}

}
