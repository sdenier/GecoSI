/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;


import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 10, 2013
 *
 */
public class Si8PlusDataFrame extends Si6PlusAbstractDataFrame {

	public static final int PAGE_SIZE = 4;
	
	private static final int SINUMBER_PAGE = 6 * PAGE_SIZE;

	public static final int NB_PUNCHES_INDEX = 5 * PAGE_SIZE + 2;

	public static enum SiPlusSeries {
		SI8_SERIES ("SiCard 8", 34),
		SI9_SERIES ("SiCard 9", 14),
		SI10PLUS_SERIES ("SiCard 10/11/SIAC", 32),
		PCARD_SERIES ("pCard", 44),
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

	public Si8PlusDataFrame(SiMessage[] dataMessages) {
		super(dataMessages);
		this.siSeries = extractSiSeries();
	}

	protected SiPlusSeries extractSiSeries() {
		switch (byteAt(SINUMBER_PAGE) & 15) {
			case 2 :
				return SiPlusSeries.SI8_SERIES;
			case 1 :
				return SiPlusSeries.SI9_SERIES;
			case 4 :
				return SiPlusSeries.PCARD_SERIES;
			case 15 :
				return SiPlusSeries.SI10PLUS_SERIES;
			default :
				return SiPlusSeries.UNKNOWN_SERIES;
		}
	}
	
	@Override
	protected int siNumberIndex() {
		return SINUMBER_PAGE + 1;
	}

	@Override
	protected int startTimeIndex() {
		return 3 * PAGE_SIZE;
	}

	@Override
	protected int finishTimeIndex() {
		return 4 * PAGE_SIZE;
	}

	@Override
	protected int checkTimeIndex() {
		return 2 * PAGE_SIZE;
	}

	@Override
	protected int nbPunchesIndex() {
		return NB_PUNCHES_INDEX;
	}

	@Override
	protected SiPunch[] extractPunches(long startTime) {
		SiPunch[] punches = new SiPunch[rawNbPunches()];
		int punchesStart = siSeries.punchesPageStartIndex();
		long refTime = startTime;
		for (int i = 0; i < punches.length; i++) {
			int punchIndex = (punchesStart + i) * PAGE_SIZE;
			long punchTime = advanceTimePast(extractFullTime(punchIndex), refTime);
			punches[i] = new SiPunch(extractCode(punchIndex), punchTime);
			refTime = newRefTime(refTime, punchTime);
		}
		return punches;
	}

	@Override
	public String getSiSeries() {
		return siSeries.ident();
	}

}
