/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

/**
 * @author Simon Denier
 * @since Apr 4, 2013
 *
 */
public interface SiDataFrame {

	public static long NO_TIME = 1000L * 0xEEEE;

	public SiDataFrame startingAt(long zerohour);

	public int getNbPunches();

	public String getSiNumber();

	public long getStartTime();

	public long getFinishTime();

	public long getCheckTime();

	public SiPunch[] getPunches();

	public void printString();

}