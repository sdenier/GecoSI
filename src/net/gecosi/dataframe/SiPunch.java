/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.dataframe;

/**
 * @author Simon Denier
 * @since Apr 2, 2013
 *
 */
public class SiPunch {

	private int code;
	private long timestamp;

	public SiPunch(int code, long timestamp) {
		this.code = code;
		this.timestamp = timestamp;
	}
	
	public int code() {
		return this.code;
	}
	
	public long timestamp() {
		return this.timestamp;
	}
	
}
