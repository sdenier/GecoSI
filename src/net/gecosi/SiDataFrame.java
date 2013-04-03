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

	public Si5DataFrame startingAt(long zerohour);

	public int getNbPunches();

	public int getSiNumber();

	public long getStartTime();

	public long getFinishTime();

	public long getCheckTime();

	public SiPunch[] getPunches();

}