/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * @author Simon Denier
 * @since Feb 13, 2013
 *
 */
public class SiComm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new SiComm().connect("/dev/tty.SLAB_USBtoUART", new SiListener() {
				public void handleEcard(Si5DataFrame dataFrame) {
					dataFrame.printString();
				}
			});
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connect(String portname, SiListener siListener) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portname);
		if( portId.isCurrentlyOwned() ) {
			System.err.println("owner error");
		} else {
			SerialPort port = (SerialPort) portId.open("GecoSI", 2000);
			port.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			// TODO: init with lower speed (failed startup?)
			// CommStatus: STOPPED/OFF, STARTING?, START_FAILED, READY, ERROR
			
			SiHandler siHandler = new SiHandler(siListener);
			siHandler.start();
			new SiDriver(new SiPort(port), siHandler).start();
			
//			siPort.close();
		}
	}

}
