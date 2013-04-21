/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import java.util.Arrays;

import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 10, 2013
 *
 */
public class Si8PlusDataFrame extends SiAbstractDataFrame {
	
	private static final int SINUMBER_PAGE = 6 * PAGE_SIZE;

	public static enum SiPlusSeries {
		SI8_SERIES ("SiCard 8", 34),
		SI9_SERIES ("SiCard 9", 14),
		SI10PLUS_SERIES ("SiCard 10/11/SIAC", 32),
		UNKNOWN_SERIES ("Unknown", 0);
		
		private String ident;
		private int punchesPageIndex;

		SiPlusSeries(String ident, int punchesPageIndex) {
			this.ident = ident;
			this.punchesPageIndex = punchesPageIndex;
		}
		
		public String ident() { return ident; }
		public int punchesPageStartIndex() { return punchesPageIndex; }
	}

	private SiPlusSeries siSeries;

	public Si8PlusDataFrame(SiMessage[] data_messages) {
		this.dataFrame	= extractDataFrame(data_messages);
		this.siSeries	= extractSiSeries();
		this.siNumber	= extractSiNumber();
		this.startTime	= extractStartTime();
		this.finishTime	= extractFinishTime();
		this.checkTime	= extractCheckTime();
		this.punches	= extractPunches();
	}

	protected byte[] extractDataFrame(SiMessage[] dataMessages) {
		byte[] dataFrame = Arrays.copyOfRange(dataMessages[0].sequence(), 6, dataMessages.length * 128 + 6);
		for (int i = 1; i < dataMessages.length; i++) {
			System.arraycopy(dataMessages[i].sequence(), 6, dataFrame, i * 128, 128);
		}
		return dataFrame;
	}

	protected SiPlusSeries extractSiSeries() {
		switch (byteAt(SINUMBER_PAGE) & 15) {
			case 2 :
				return SiPlusSeries.SI8_SERIES;
			case 1 :
				return SiPlusSeries.SI9_SERIES;
			case 15 :
				return SiPlusSeries.SI10PLUS_SERIES;
			default :
				return SiPlusSeries.UNKNOWN_SERIES;
		}
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

	protected int rawNbPunches() {
		return byteAt(5 * PAGE_SIZE + 2);
	}

	protected SiPunch[] extractPunches() {
		SiPunch[] punches = new SiPunch[rawNbPunches()];
		int punchesStart = siSeries.punchesPageStartIndex();
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
	public String getSiSeries() {
		return siSeries.ident();
	}

}
