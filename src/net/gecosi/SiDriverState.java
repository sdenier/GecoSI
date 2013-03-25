/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public enum SiDriverState {
	
	STARTUP {
		public SiDriverState send(CommWriter writer) throws IOException {
			writer.write_debug(SiMessage.startup_sequence);
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
			writer.write_debug(SiMessage.get_protocol_configuration);
			return EXTENDED_PROTOCOL_CHECK;
		}
	},

	EXTENDED_PROTOCOL_CHECK {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException, InvalidMessage {
			SiMessage message = pollAnswer(queue, SiMessage.GET_SYSTEM_VALUE);
			if( (message.sequence(6) & EXTENDED_PROTOCOL_MASK) != 0 ) {
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
				throws IOException, InterruptedException {
			siHandler.notify(CommStatus.READY);
			SiMessage message = queue.take();
			siHandler.notify(CommStatus.PROCESSING);
			switch (message.commandByte()) {
			case SiMessage.SI_CARD_5_DETECTED:
				return READ_SICARD_5.send(writer);
			case SiMessage.SI_CARD_REMOVED:
			default:
				// TODO log?
				return DISPATCH_READY;
			}
		}
	},
	
	READ_SICARD_5 {
		public SiDriverState send(CommWriter writer) throws IOException {
			writer.write_debug(SiMessage.read_sicard_5);
			return WAIT_SICARD_5_DATA;
		}
	},
	
	WAIT_SICARD_5_DATA {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException {
			try {
				SiMessage message = queue.timeoutPoll();
				 if( message.check(SiMessage.GET_SI_CARD_5) ){
					 siHandler.notify(new Si5DataFrame(message));
					 return ACK_READ.send(writer);
				 } else {
					 return errorFallback(siHandler);
				 }
			} catch (TimeoutException e) {
				 return errorFallback(siHandler);
			}
		}
	},
	
	ACK_READ {
		public SiDriverState send(CommWriter writer) throws IOException {
			writer.write_debug(SiMessage.ack_sequence);
			return WAIT_SICARD_REMOVAL;
		}		
	},
	
	WAIT_SICARD_REMOVAL {
		public SiDriverState receive(SiMessageQueue queue, CommWriter writer, SiHandler siHandler)
				throws IOException, InterruptedException, TimeoutException {
			SiMessage message = queue.timeoutPoll();
			// TODO NAK then SI_CARD_REMOVED?

			return DISPATCH_READY;
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
	
	private void wrongCall() {
		throw new RuntimeException(String.format("This method should not be called on %s", this.name()));
	}

	public boolean isError() {
		return false;
	}
	
	public String status() {
		return name();
	}

	public void checkAnswer(SiMessage message, byte command) throws InvalidMessage {
		if( ! message.check(command) ){
			throw new InvalidMessage(message);
		}
	}

	public SiMessage pollAnswer(SiMessageQueue queue, byte command)
			throws InterruptedException, TimeoutException, InvalidMessage {
		SiMessage message = queue.timeoutPoll();
		checkAnswer(message, command);
		return message;
	}

	public SiDriverState errorFallback(SiHandler siHandler) {
		siHandler.notify(CommStatus.PROCESSING_ERROR);
		return DISPATCH_READY;
	}

}
