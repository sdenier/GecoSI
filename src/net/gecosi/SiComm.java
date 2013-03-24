/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * @author Simon Denier
 * @since Feb 13, 2013
 *
 */
public class SiComm {

	public static void main(String[] args) {
		try {
			new SiComm().connect("/dev/tty.SLAB_USBtoUART", new SiListener() {
				public void handleEcard(Si5DataFrame dataFrame) {
					dataFrame.printString();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(String portname, SiListener siListener)
			throws NoSuchPortException, PortInUseException, IOException, TooManyListenersException {
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portname);
		if( portId.isCurrentlyOwned() ) {
			System.err.println("owner error");
		} else {
			SiHandler siHandler = new SiHandler(siListener);
			siHandler.start();

			SerialPort port = (SerialPort) portId.open("GecoSI", 2000);
			new SiDriver(new RxtxPort(port), siHandler).start();
		}
	}

}
