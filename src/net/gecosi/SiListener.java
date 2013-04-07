/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import net.gecosi.dataframe.SiDataFrame;

/**
 * @author Simon Denier
 * @since Mar 12, 2013
 *
 */
public interface SiListener {

	public void handleEcard(SiDataFrame dataFrame);

	public void notify(CommStatus status);

	public void notify(CommStatus errorStatus, String errorMessage);

}
