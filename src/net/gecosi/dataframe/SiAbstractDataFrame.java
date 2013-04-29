/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;


/**
 * @author Simon Denier
 * @since Apr 10, 2013
 *
 */
public abstract class SiAbstractDataFrame extends AbstractDataFrame {

	protected static long NO_SI_TIME = 1000L * 0xEEEE;
	
	protected static long TWELVE_HOURS = 1000L * 12 * 3600;

	protected byte[] dataFrame;
	
	protected int byteAt(int i) {
		return dataFrame[i] & 0xFF;
	}

	protected int wordAt(int i) {
		return byteAt(i) << 8 | byteAt(i + 1);
	}

	protected int block3At(int i) {
		return byteAt(i) << 16 | wordAt(i + 1);
	}
	
	protected long timestampAt(int i) {
		return 1000L * wordAt(i);
	}

	protected long shiftTime(long time, long zeroHourShift) {
		return ( time == NO_SI_TIME ) ? NO_TIME : time + zeroHourShift;
	}
	
}
