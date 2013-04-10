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

	protected byte[] dataFrame;
	
	protected int byteAt(int i) {
		return dataFrame[i] & 0xFF;
	}

	protected int wordAt(int i) {
		return byteAt(i) << 8 | byteAt(i + 1);
	}

	protected long timestampAt(int i) {
		return 1000L * wordAt(i);
	}
	
}
