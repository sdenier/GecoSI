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

	private boolean extendedPunches;
	
	public Si6DataFrame(SiMessage[] dataMessages) {
		super(dataMessages);
		this.extendedPunches = dataMessages.length == 8;
		initializeDataFields();
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
		return 1 * PAGE_SIZE + 2;
	}

	protected int punchesStartIndex() {
		return extendedPunches ? 16 * PAGE_SIZE : 8 * PAGE_SIZE;
	}

	@Override
	protected SiPunch[] extractPunches() {
		SiPunch[] punches = new SiPunch[rawNbPunches()];
		int punchesStart = punchesStartIndex();
		for (int i = 0; i < punches.length; i++) {
			int punchIndex = punchesStart + (DOUBLE_WORD * i);
			punches[i] = new SiPunch(extractCode(punchIndex), extract24HourTime(punchIndex));
		}
		return punches;
	}
	
	@Override
	public String getSiSeries() {
		return "SiCard 6";
	}

}
