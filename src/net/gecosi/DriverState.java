/**
 * Copyright (c) 2013 Simon Denier
 */
package net.gecosi;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Simon Denier
 * @since Mar 10, 2013
 *
 */
public enum DriverState {

	STARTUP {
		public DriverState send(CommWriter writer) throws IOException {
			writer.write_debug(SiMessage.startup_sequence);
			return STARTUP_CHECK;
		}
	},

	STARTUP_CHECK {
		public DriverState receive(ArrayBlockingQueue<SiMessage> queue, CommWriter writer, SiHandler siHandler) throws IOException, InterruptedException {
			SiMessage message = timeoutWait(queue);
			message.check(SiMessage.SET_MASTER_MODE); // TODO 
			return GET_CONFIG.send(writer);
		}
	},

	GET_CONFIG {
		public DriverState send(CommWriter writer) throws IOException {
			writer.write_debug(SiMessage.get_protocol_configuration);
			return CONFIG_CHECK;
		}
	},

	CONFIG_CHECK {
		public DriverState receive(ArrayBlockingQueue<SiMessage> queue, CommWriter writer, SiHandler siHandler) throws IOException, InterruptedException {
			SiMessage message = timeoutWait(queue);
			message.check(SiMessage.GET_SYSTEM_VALUE); // TODO 
			return DISPATCH_READY;
		}
	},

	DISPATCH_READY {
		public DriverState receive(ArrayBlockingQueue<SiMessage> queue, CommWriter writer, SiHandler siHandler) throws IOException, InterruptedException {
			System.out.println("** DISPATCH READY **");
			System.out.flush();
			SiMessage message = queue.take();
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
		public DriverState send(CommWriter writer) throws IOException {
			writer.write_debug(SiMessage.read_sicard_5);
			return WAIT_SICARD_5_DATA;
		}
	},
	
	WAIT_SICARD_5_DATA {
		public DriverState receive(ArrayBlockingQueue<SiMessage> queue, CommWriter writer, SiHandler siHandler) throws IOException, InterruptedException {
			SiMessage message = timeoutWait(queue);
			// TODO error | sicard_removed --> reset
			 if( message.check(SiMessage.GET_SI_CARD_5) ){
				 siHandler.notify(new Si5DataFrame(message));
				 return ACK_READ.send(writer);
			 } else {
				 return DISPATCH_READY;
			 }
		}
	},
	
	ACK_READ {
		public DriverState send(CommWriter writer) throws IOException {
			writer.write_debug(SiMessage.ack_sequence);
			return WAIT_SICARD_REMOVAL;
		}		
	},
	
	WAIT_SICARD_REMOVAL {
		public DriverState receive(ArrayBlockingQueue<SiMessage> queue, CommWriter writer, SiHandler siHandler) throws IOException, InterruptedException {
			SiMessage message = timeoutWait(queue);
			// TODO NAK then SI_CARD_REMOVED?

			return DISPATCH_READY;
		}		
	};

	private static SiMessage timeoutWait(ArrayBlockingQueue<SiMessage> queue) throws InterruptedException {
		return queue.poll(2000, TimeUnit.MILLISECONDS);
	}
	
	public DriverState send(CommWriter writer) throws IOException {
		wrongCall();
		return this;
	}

	public DriverState receive(ArrayBlockingQueue<SiMessage> queue, CommWriter writer, SiHandler siHandler) throws IOException, InterruptedException {
		wrongCall();
		return this;
	}
	
	private void wrongCall() {
		throw new RuntimeException(String.format("This method should not be called on %s", this.name()));
	}

}
