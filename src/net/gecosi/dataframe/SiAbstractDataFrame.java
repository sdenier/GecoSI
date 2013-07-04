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

	protected final static long NO_SI_TIME = 1000L * 0xEEEE;
	
	protected final static long TWELVE_HOURS = 1000L * 12 * 3600;

	protected final static long ONE_DAY = 2 * TWELVE_HOURS;

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

	public long advanceTimePast(long timestamp, long refTime, long stepTime) {
		if( timestamp == NO_SI_TIME ) {
			return NO_TIME;
		}
		if( refTime == NO_TIME ) {
			return timestamp;
		}
		long newTimestamp = timestamp;
		// advance time until it is at least less than one hour before refTime
		// accomodates for drifting clocks or even controls with different daylight savings mode
		long baseTime = refTime - 3600000;
		while( newTimestamp < baseTime){
			newTimestamp += stepTime;
		}
		return newTimestamp;
	}

	public long newRefTime(long refTime, long punchTime) {
		return punchTime != NO_TIME ? punchTime : refTime;
	}

}
