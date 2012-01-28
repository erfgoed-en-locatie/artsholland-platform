package org.waag.ah.jms;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateful;
import javax.jms.BytesMessage;
import javax.jms.JMSException;

import com.Ostermiller.util.CircularByteBuffer;

@Stateful
public class StreamingMessageBuffer extends CircularByteBuffer {
//	private Logger logger = Logger.getLogger(StreamingMessageBuffer.class);
	static int BUFFER_SIZE = 65536;
	
	public StreamingMessageBuffer() {
		super(BUFFER_SIZE);
	}

	@Asynchronous
	public Future<Boolean> readOutputStream(BytesMessage message) throws JMSException {
		message.setObjectProperty("JMS_HQ_SaveStream", getOutputStream());
		return new AsyncResult<Boolean>(true);
	}
}