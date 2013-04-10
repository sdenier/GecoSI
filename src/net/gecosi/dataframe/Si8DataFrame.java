/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

import java.util.Arrays;

import net.gecosi.internal.SiMessage;

/**
 * @author Simon Denier
 * @since Apr 10, 2013
 *
 */
public class Si8DataFrame extends SiAbstractDataFrame {

	public Si8DataFrame(SiMessage[] data_messages) {
		this.dataFrame = extractDataFrame(data_messages);
		this.siNumber = extractSiNumber();
	}

	private byte[] extractDataFrame(SiMessage[] dataMessages) {
		byte[] dataFrame = Arrays.copyOfRange(dataMessages[0].sequence(), 6, 256 + 6);
		System.arraycopy(dataMessages[1].sequence(), 6, dataFrame, 128, 128);
		return dataFrame;
	}

	protected String extractSiNumber() {
		return Integer.toString((byteAt(25) << 16) + wordAt(26));
	}

	@Override
	public SiDataFrame startingAt(long zerohour) {
		return this;
	}

}
