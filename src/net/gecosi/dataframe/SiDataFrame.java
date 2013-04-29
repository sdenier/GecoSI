/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;


/**
 * @author Simon Denier
 * @since Apr 4, 2013
 *
 */
public interface SiDataFrame {

	public final static long NO_TIME = -1;

	public SiDataFrame startingAt(long zerohour);

	public int getNbPunches();

	public String getSiNumber();

	public String getSiSeries();

	public long getStartTime();

	public long getFinishTime();

	public long getCheckTime();

	public SiPunch[] getPunches();

	public void printString();

}