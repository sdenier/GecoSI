/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * @author Simon Denier
 * @since Apr 7, 2013
 *
 */
public abstract class AbstractDataFrame implements SiDataFrame {

	protected String siNumber;

	protected long checkTime;

	protected long startTime;

	protected long finishTime;

	protected SiPunch[] punches;

	private static final SimpleDateFormat TIME_FORMATTER;

	static {
		TIME_FORMATTER = new SimpleDateFormat("H:mm:ss");
		TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	@Override
	public String getSiNumber() {
		return siNumber;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getFinishTime() {
		return finishTime;
	}

	@Override
	public long getCheckTime() {
		return checkTime;
	}

	@Override
	public int getNbPunches() {
		return punches.length;
	}

	@Override
	public SiPunch[] getPunches() {
		return punches;
	}

	public String formatTime(long timestamp) {
		if( timestamp == NO_TIME ) {
			return "no time";
		} else {
			return TIME_FORMATTER.format(new Date(timestamp));
		}
	}

	public void printString() {
		System.out.format("%s: %s ", getSiSeries(), getSiNumber());
		System.out.format("(Start: %s ", formatTime(getStartTime()));
		System.out.format(" - Finish: %s", formatTime(getFinishTime()));
		System.out.format(" - Check: %s)%n", formatTime(getCheckTime()));
		System.out.format("Punches: %s %n", getNbPunches());
		for (int i = 0; i < getNbPunches(); i++) {
			System.out.format("%s: %s %s - ", i, getPunches()[i].code(), formatTime(getPunches()[i].timestamp()));
		}
		System.out.println();
	}
	
}