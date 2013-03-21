/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.io.StringWriter;

import de.sportident.CRCCalculator;

/**
 * @author Simon Denier
 * @since Feb 15, 2013
 *
 */
public class SiMessage {

	private final byte[] sequence;
	
	public SiMessage(byte[] sequence) {
		this.sequence = sequence;
	}

	public byte[] sequence() {
		return sequence;
	}

	public byte[] data() {
		int cmd_length = sequence.length - 4;
		byte[] command = new byte[cmd_length];
		System.arraycopy(sequence, 1, command, 0, cmd_length);
		return command;
	}

	public byte commandByte() {
		return sequence[1];
	}

	public byte startByte() {
		return sequence[0];
	}

	public byte endByte() {
		return sequence[sequence.length - 1];
	}

	public int extractCRC() {
		int i = sequence.length;
		return (sequence[i-3] << 8 & 0xFFFF) | (sequence[i-2] & 0xFF); 
	}
	
	public int computeCRC() {
		return CRCCalculator.crc(data());
	}
	
	public boolean check(byte command) {
		return valid() && commandByte() == command;
	}
	
	public boolean valid() {
		return startByte() == STX && endByte() == ETX && validCRC();
	}
	
	public boolean validCRC() {
		return computeCRC() == extractCRC();
	}

	@Override
	public String toString() {
		StringWriter buf = new StringWriter(sequence.length);
		for (int i = 0; i < sequence.length; i++) {
			buf.write(String.format("%02X ", sequence[i]));// & 0xFF);
		}
		return buf.toString();
	}

	public String toStringCRC() {
		if( sequence.length >= 6 ) {
			return String.format("%04X", computeCRC());
		} else {
			return "none";
		}
	}


	/*
	 * Basic protocol instructions
	 */
	private static final byte WAKEUP = (byte) 0xFF;
	private static final byte STX = 0x02;
	private static final byte ETX = 0x03;
	private static final byte ACK = 0x06;
	private static final byte NAK = 0x15;

	/*
	 * Command instructions
	 */
	public static final byte GET_SYSTEM_VALUE = (byte) 0x83;
	public static final byte SET_MASTER_MODE = (byte) 0xF0;
	public static final byte DIRECT_MODE = 0x4d;

	/*
	 * Card detected/removed
	 */
	public static final byte SI_CARD_5_DETECTED = (byte) 0xE5;
	public static final byte SI_CARD_8_PLUS_DETECTED = (byte) 0xE8;
	public static final byte SI_CARD_6_PLUS_DETECTED = (byte) 0xE6;
	public static final byte SI_CARD_REMOVED = (byte) 0xE7;
	
	/*
	 * Card Readout instructions
	 */
	public static final byte GET_SI_CARD_5 = (byte) 0xB1;
	public static final byte GET_SI_CARD_8_PLUS_BN = (byte) 0xEF;
	public static final byte GET_SI_CARD_6_PLUS_BN = (byte) 0xE1;

	public static final SiMessage NULL = new SiMessage(new byte[0]);

	public static final SiMessage startup_sequence = new SiMessage(new byte[] {
		WAKEUP, STX, STX, SET_MASTER_MODE, 0x01, DIRECT_MODE, 0x6D, 0x0A, ETX
	});

	public static final SiMessage get_protocol_configuration = new SiMessage(new byte[] {
		STX, GET_SYSTEM_VALUE, 0x02, 0x74, 0x01, 0x04, 0x14, ETX
	});
		
	public static final SiMessage ack_sequence = new SiMessage(new byte[] {
		STX, ACK, 0x00, ACK, 0x00, ETX
	});

	public static final SiMessage read_sicard_5 = new SiMessage(new byte[] {
		STX, GET_SI_CARD_5, 0x00, GET_SI_CARD_5, 0x00, ETX	
	});
	
}
