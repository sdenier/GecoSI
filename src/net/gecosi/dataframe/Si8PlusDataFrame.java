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

	public Si8PlusDataFrame(SiMessage[] dataMessages) {
		super(dataMessages);
		this.siSeries = extractSiSeries();
		initializeDataFields();
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
		return 5 * PAGE_SIZE + 2;
	}

	@Override
	protected SiPunch[] extractPunches() {
		SiPunch[] punches = new SiPunch[rawNbPunches()];
		int punchesStart = siSeries.punchesPageStartIndex();
		for (int i = 0; i < punches.length; i++) {
			int punchIndex = (punchesStart + i) * PAGE_SIZE;
			punches[i] = new SiPunch(extractCode(punchIndex), extractFullTime(punchIndex));
		}
		return punches;
	}

	@Override
	public String getSiSeries() {
		return siSeries.ident();
	}

}
