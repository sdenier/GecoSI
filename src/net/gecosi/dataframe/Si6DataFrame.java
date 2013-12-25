/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 22, 2013
 *
 */
public class Si6DataFrame extends Si6PlusAbstractDataFrame {

	public static final int PAGE_SIZE = 16;
	
	public static final int DOUBLE_WORD = 4;

	public static final int NB_PUNCHES_INDEX = 1 * PAGE_SIZE + 2;
	
	public Si6DataFrame(SiMessage[] dataMessages) {
		super(dataMessages);
	}

	@Override
	protected int siNumberIndex() {
		return 2 * DOUBLE_WORD + 3;
	}

	@Override
	protected int startTimeIndex() {
		return 1 * PAGE_SIZE + 2 * DOUBLE_WORD;
	}

	@Override
	protected int finishTimeIndex() {
		return 1 * PAGE_SIZE + 1 * DOUBLE_WORD;
	}

	@Override
	protected int checkTimeIndex() {
		return 1 * PAGE_SIZE + 3 * DOUBLE_WORD;
	}

	@Override
	protected int nbPunchesIndex() {
		return NB_PUNCHES_INDEX;
	}

	protected int punchesStartIndex() {
		return 8 * PAGE_SIZE;
	}

	@Override
	protected SiPunch[] extractPunches(long startTime) {
		SiPunch[] punches = new SiPunch[rawNbPunches()];
		int punchesStart = punchesStartIndex();
		long refTime = startTime;
		for (int i = 0; i < punches.length; i++) {
			int punchIndex = punchesStart + (DOUBLE_WORD * i);
			long punchTime = advanceTimePast(extractFullTime(punchIndex), refTime);
			punches[i] = new SiPunch(extractCode(punchIndex), punchTime);
			refTime = newRefTime(refTime, punchTime);
		}
		return punches;
	}
	
	@Override
	public String getSiSeries() {
		return "SiCard 6";
	}

}
