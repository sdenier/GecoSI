/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi.internal;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.gecosi.CommStatus;
import net.gecosi.SiHandler;
import net.gecosi.dataframe.Si5DataFrame;
import net.gecosi.dataframe.Si8_9DataFrame;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public enum SiDriverState {
	
	STARTUP {
		public SiDriverState send(CommWriter writer) throws IOException {
			writer.write(SiMessage.startup_sequence);
			return STARTUP_CHECK;
		}
	},

	STARTUP_CHECK {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException, InvalidMessage {
			pollAnswer(queue, SiMessage.SET_MASTER_MODE);
			return GET_CONFIG.send(writer);
		}
	},

	STARTUP_TIMEOUT {
		public boolean isError() { return true; }
		public String status() {
			return "Master station did not answer to startup sequence (high/low baud)";
		}
	},

	GET_CONFIG {
		public SiDriverState send(CommWriter writer) throws IOException {
			writer.write(SiMessage.get_protocol_configuration);
			return EXTENDED_PROTOCOL_CHECK;
		}
	},

	EXTENDED_PROTOCOL_CHECK {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException, InvalidMessage {
			SiMessage message = pollAnswer(queue, SiMessage.GET_SYSTEM_VALUE);
			if( (message.sequence(6) & EXTENDED_PROTOCOL_MASK) != 0 ) {
				writer.write(SiMessage.beep_twice);
				siHandler.notify(CommStatus.ON);
				return DISPATCH_READY;
			} else {
				return EXTENDED_PROTOCOL_ERROR;
			}
		}
	},

	EXTENDED_PROTOCOL_ERROR {
		public boolean isError() { return true; }
		public String status() {
			return "Master station should be configured with extended protocol";
		}
	},
	
	DISPATCH_READY {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException, InvalidMessage {
			siHandler.notify(CommStatus.READY);
			SiMessage message = queue.take();
			siHandler.notify(CommStatus.PROCESSING);
			switch (message.commandByte()) {
			case SiMessage.SI_CARD_5_DETECTED:
				return RETRIEVE_SICARD_5_DATA.retrieve(queue, writer, siHandler);
			case SiMessage.SI_CARD_8_PLUS_DETECTED:
				return RETRIEVE_SICARD_8_9_DATA.retrieve(queue, writer, siHandler);
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
	},
	
	RETRIEVE_SICARD_5_DATA {
		public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			GecoSILogger.stateChanged(RETRIEVE_SICARD_5_DATA.name());
			try {
				SiMessage[] dataMessages = retrieveDataMessages(queue, writer, new SiMessage[] {
						SiMessage.read_sicard_5 });
				siHandler.notify(new Si5DataFrame(dataMessages[0]));
				return ACK_READ.send(writer);
			} catch (TimeoutException e) {
				return errorFallback(siHandler, "Timeout on retrieving SiCard 5 data");
			} catch (InvalidMessage e) {
				return errorFallback(siHandler, "Invalid message: " + e.receivedMessage().toString());
			}
		}
	},
	
	RETRIEVE_SICARD_8_9_DATA {
		public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			GecoSILogger.stateChanged(RETRIEVE_SICARD_8_9_DATA.name());
			try {
				SiMessage[] dataMessages = retrieveDataMessages(queue, writer, new SiMessage[] {
						SiMessage.read_sicard_8_plus_b0, SiMessage.read_sicard_8_plus_b1 });
				siHandler.notify(new Si8_9DataFrame(dataMessages));
				return ACK_READ.send(writer);
			} catch (TimeoutException e) {
				return errorFallback(siHandler, "Timeout on retrieving SiCard 8/9 data");
			} catch (InvalidMessage e) {
				return errorFallback(siHandler, "Invalid message: " + e.receivedMessage().toString());
			}
		}		
	},
	
	ACK_READ {
		public SiDriverState send(CommWriter writer) throws IOException {
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

	public SiDriverState send(CommWriter writer) throws IOException {
		wrongCall();
		return this;
	}

	public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
			throws IOException, InterruptedException, TimeoutException, InvalidMessage {
		wrongCall();
		return this;
	}

	public SiDriverState retrieve(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
			throws IOException, InterruptedException, TimeoutException, InvalidMessage {
		wrongCall();
		return this;
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

	protected SiMessage[] retrieveDataMessages(SiMessageQueue queue, CommWriter writer, SiMessage[] retrievalMessages)
			throws IOException, InterruptedException, TimeoutException, InvalidMessage {
		SiMessage[] dataMessages = new SiMessage[retrievalMessages.length];
		for (int i = 0; i < retrievalMessages.length; i++) {
			SiMessage messageToSend = retrievalMessages[i];
			writer.write(messageToSend);
			dataMessages[i] = pollAnswer(queue, messageToSend.commandByte());
		}
		return dataMessages;
	}

	protected SiDriverState errorFallback(SiHandler siHandler, String errorMessage) {
		GecoSILogger.error(errorMessage);
		siHandler.notify(CommStatus.PROCESSING_ERROR);
		return DISPATCH_READY;
	}

}
