/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.internal;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.gecosi.CommStatus;
import net.gecosi.SiHandler;
import net.gecosi.dataframe.Si5DataFrame;
import net.gecosi.dataframe.Si6DataFrame;
import net.gecosi.dataframe.Si8PlusDataFrame;
import net.gecosi.dataframe.SiDataFrame;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public enum SiDriverState {
	
	STARTUP {
		public SiDriverState send(CommWriter writer, SiHandler siHandler) throws IOException {
			writer.write(SiMessage.startup_sequence);
			return STARTUP_CHECK;
		}
	},

	STARTUP_CHECK {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException, InvalidMessage {
			pollAnswer(queue, SiMessage.SET_MASTER_MODE);
			return GET_CONFIG.send(writer, siHandler);
		}
	},

	STARTUP_TIMEOUT {
		public boolean isError() { return true; }
		public String status() {
			return "Master station did not answer to startup sequence (high/low baud)";
		}
	},

	GET_CONFIG {
		public SiDriverState send(CommWriter writer, SiHandler siHandler) throws IOException {
			writer.write(SiMessage.get_protocol_configuration);
			return CONFIG_CHECK;
		}
	},

	CONFIG_CHECK {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException, InvalidMessage {
			SiMessage message = pollAnswer(queue, SiMessage.GET_SYSTEM_VALUE);
			byte cpcByte = message.sequence(6);
			if( (cpcByte & CONFIG_CHECK_MASK) == CONFIG_CHECK_MASK ) {
				return GET_SI6_CARDBLOCKS.send(writer, siHandler);
			}
			if( (cpcByte & EXTENDED_PROTOCOL_MASK) == 0 ){
				return EXTENDED_PROTOCOL_ERROR;
			} else {
				return HANDSHAKE_MODE_ERROR;
			}
		}
	},

	EXTENDED_PROTOCOL_ERROR {
		public boolean isError() { return true; }
		public String status() {
			return "Master station should be configured with extended protocol";
		}
	},

	HANDSHAKE_MODE_ERROR {
		public boolean isError() { return true; }
		public String status() {
			return "Master station should be configured in handshake mode (no autosend)";
		}
	},
	
	GET_SI6_CARDBLOCKS {
		public SiDriverState send(CommWriter writer, SiHandler siHandler) throws IOException {
			writer.write(SiMessage.get_cardblocks_configuration);
			return SI6_CARDBLOCKS_SETTING;
		}
	},

	SI6_CARDBLOCKS_SETTING {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException, InvalidMessage {
			SiMessage message = pollAnswer(queue, SiMessage.GET_SYSTEM_VALUE);
			si6_192PunchesMode = (message.sequence(6) & 0xFF) == 0xFF;
			GecoSILogger.info("SiCard6 192 Punches Mode " + (si6_192PunchesMode ? "Enabled" : "Disabled"));
			return STARTUP_COMPLETE.send(writer, siHandler);
		}
	},

	STARTUP_COMPLETE {
		public SiDriverState send(CommWriter writer, SiHandler siHandler) throws IOException {
			writer.write(SiMessage.beep_twice);
			siHandler.notify(CommStatus.ON);
			return DISPATCH_READY;
		}
	},
	
	DISPATCH_READY {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			siHandler.notify(CommStatus.READY);
			SiMessage message = queue.take();
			siHandler.notify(CommStatus.PROCESSING);
			switch (message.commandByte()) {
			case SiMessage.SI_CARD_5_DETECTED:
				return RETRIEVE_SICARD_5_DATA.retrieve(queue, writer, siHandler);
			case SiMessage.SI_CARD_6_PLUS_DETECTED:
				return RETRIEVE_SICARD_6_DATA.retrieve(queue, writer, siHandler);
			case SiMessage.SI_CARD_8_PLUS_DETECTED:
				return dispatchSicard8Plus(message, queue, writer, siHandler);
			case SiMessage.BEEP:
				break;
			case SiMessage.SI_CARD_REMOVED:
				GecoSILogger.debug("Late removal " + message.toString());
				break;
			default:
				GecoSILogger.debug("Unexpected message " + message.toString());
			}
			return DISPATCH_READY;
		}

		private SiDriverState dispatchSicard8Plus(SiMessage message, SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			if( message.sequence(SiMessage.SI3_NUMBER_INDEX) == SiMessage.SI_CARD_10_PLUS_SERIES ) {
				return RETRIEVE_SICARD_10_PLUS_DATA.retrieve(queue, writer, siHandler);
			} else {
				return RETRIEVE_SICARD_8_9_DATA.retrieve(queue, writer, siHandler);
			}
		}
	},
	
	RETRIEVE_SICARD_5_DATA {
		public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			return retrieveDataMessages(queue, writer, siHandler, new SiMessage[]{ SiMessage.read_sicard_5 }, -1,
										"Timeout on retrieving SiCard 5 data");
		}

		public Si5DataFrame createDataFrame(SiMessage[] dataMessages) {
			return new Si5DataFrame(dataMessages[0]);
		}
	},
	
	RETRIEVE_SICARD_6_DATA {
		private SiMessage[] readoutCommands = new SiMessage[] {
				SiMessage.read_sicard_6_b0,
				SiMessage.read_sicard_6_b6,
				SiMessage.read_sicard_6_b7,
				SiMessage.read_sicard_6_plus_b2,
				SiMessage.read_sicard_6_plus_b3,
				SiMessage.read_sicard_6_plus_b4,
				SiMessage.read_sicard_6_plus_b5
		};

		public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			final int nbPunchesIndex = Si6DataFrame.NB_PUNCHES_INDEX + 6;
			return retrieveDataMessages(queue, writer, siHandler, readoutCommands, nbPunchesIndex,
										"Timeout on retrieving SiCard 6 data");
		}

		@Override
		public SiDataFrame createDataFrame(SiMessage[] dataMessages) {
			return new Si6DataFrame(dataMessages);
		}
	},

	RETRIEVE_SICARD_8_9_DATA {
		public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			return retrieveDataMessages(queue, writer, siHandler,
					new SiMessage[] { SiMessage.read_sicard_8_plus_b0, SiMessage.read_sicard_8_plus_b1 },
					-1, "Timeout on retrieving SiCard 8/9 data");
		}

		@Override
		public SiDataFrame createDataFrame(SiMessage[] dataMessages) {
			return new Si8PlusDataFrame(dataMessages);
		}
	},

	RETRIEVE_SICARD_10_PLUS_DATA {
		private SiMessage[] readoutCommands = new SiMessage[] {
				SiMessage.read_sicard_10_plus_b0,
				SiMessage.read_sicard_10_plus_b4,
				SiMessage.read_sicard_10_plus_b5,
				SiMessage.read_sicard_10_plus_b6,
				SiMessage.read_sicard_10_plus_b7
		};

		public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			final int nbPunchesIndex = Si8PlusDataFrame.NB_PUNCHES_INDEX + 6;
			return retrieveDataMessages(queue, writer, siHandler, readoutCommands, nbPunchesIndex,
										"Timeout on retrieving SiCard 10/11/SIAC data");
		}

		@Override
		public SiDataFrame createDataFrame(SiMessage[] dataMessages) {
			return new Si8PlusDataFrame(dataMessages);
		}
	},

	ACK_READ {
		public SiDriverState send(CommWriter writer, SiHandler siHandler) throws IOException {
			writer.write(SiMessage.ack_sequence);
			return WAIT_SICARD_REMOVAL;
		}		
	},
	
	WAIT_SICARD_REMOVAL {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			try {
				pollAnswer(queue, SiMessage.SI_CARD_REMOVED);
				return DISPATCH_READY;
			} catch (TimeoutException e) {
				GecoSILogger.info("Timeout on SiCard removal");
				return DISPATCH_READY;
			} catch (InvalidMessage e) {
				return errorFallback(siHandler, "Invalid message: " + e.receivedMessage().toString());
			}
		}		
	};

	private static final int EXTENDED_PROTOCOL_MASK = 1;

	private static final int HANDSHAKE_MODE_MASK = 4;

	private static final int CONFIG_CHECK_MASK = EXTENDED_PROTOCOL_MASK | HANDSHAKE_MODE_MASK;
	
	private static boolean si6_192PunchesMode = false;

	public static boolean sicard6_192PunchesMode() {
		return si6_192PunchesMode;
	}

	public static void setSicard6_192PunchesMode(boolean flag) {
		si6_192PunchesMode = flag;
	}
	
	public SiDriverState send(CommWriter writer, SiHandler siHandler) throws IOException {
		wrongCall();
		return this;
	}

	public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
			throws IOException, InterruptedException, TimeoutException, InvalidMessage {
		wrongCall();
		return this;
	}

	public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
			throws IOException, InterruptedException {
		wrongCall();
		return this;
	}

	public SiDataFrame createDataFrame(SiMessage[] dataMessages) {
		wrongCall();
		return null;
	}

	private void wrongCall() {
		throw new RuntimeException(String.format("This method should not be called on %s", this.name()));
	}

	public boolean isError() {
		return false;
	}
	
	public String status() {
		return name();
	}

	protected void checkAnswer(SiMessage message, byte command) throws InvalidMessage {
		if( ! message.check(command) ){
			throw new InvalidMessage(message);
		}
	}

	protected SiMessage pollAnswer(SiMessageQueue queue, byte command)
			throws InterruptedException, TimeoutException, InvalidMessage {
		SiMessage message = queue.timeoutPoll();
		checkAnswer(message, command);
		return message;
	}

	protected int extractNumberOfDataBlocks(SiMessage firstBlock, int nbPunchesIndex) {
		int nbPunches = firstBlock.sequence(nbPunchesIndex) & 0xFF;
		int nbPunchesPerBlock = 32;
		int nbPunchDataBlocks = (nbPunches / nbPunchesPerBlock) + Math.min(1, nbPunches % nbPunchesPerBlock);
		GecoSILogger.info(String.format("Nb punches/Data blocks: %s/%s", nbPunches, nbPunchDataBlocks));
		return nbPunchDataBlocks + 1;
	}

	protected SiDriverState retrieveDataMessages(SiMessageQueue queue, CommWriter writer, SiHandler siHandler,
			SiMessage[] readoutCommands, int nbPunchesIndex, String timeoutMessage) throws IOException, InterruptedException {
		try {
			GecoSILogger.stateChanged(name());
			SiMessage readoutCommand = readoutCommands[0];
			writer.write(readoutCommand);
			SiMessage firstDataBlock = pollAnswer(queue, readoutCommand.commandByte());
			int nbDataBlocks = ( nbPunchesIndex == -1 ) ?
					readoutCommands.length :
					extractNumberOfDataBlocks(firstDataBlock, nbPunchesIndex);
			SiMessage[] dataMessages = new SiMessage[nbDataBlocks];
			dataMessages[0] = firstDataBlock;
			for (int i = 1; i < nbDataBlocks; i++) {
				readoutCommand = readoutCommands[i];
				writer.write(readoutCommand);
				dataMessages[i] = pollAnswer(queue, readoutCommand.commandByte());
			}
			siHandler.notify(createDataFrame(dataMessages));
			return ACK_READ.send(writer, siHandler);
		} catch (TimeoutException e) {
			return errorFallback(siHandler, timeoutMessage);
		} catch (InvalidMessage e) {
			return errorFallback(siHandler, "Invalid message: " + e.receivedMessage().toString());
		}
	}

	protected SiDriverState errorFallback(SiHandler siHandler, String errorMessage) {
		GecoSILogger.error(errorMessage);
		siHandler.notify(CommStatus.PROCESSING_ERROR);
		return DISPATCH_READY;
	}

}
