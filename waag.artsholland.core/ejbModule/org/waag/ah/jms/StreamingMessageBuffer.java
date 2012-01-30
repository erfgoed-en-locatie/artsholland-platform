package org.waag.ah.jms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.jms.BytesMessage;
import javax.jms.JMSException;

import org.jboss.logging.Logger;

@Stateless
public class StreamingMessageBuffer {
	private Logger logger = Logger.getLogger(StreamingMessageBuffer.class);

	@Asynchronous
	public Future<Boolean> pipedReader(BytesMessage message, 
			OutputStream out) throws JMSException, IOException {
		try {
			message.setObjectProperty("JMS_HQ_SaveStream", out);
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			logger.info("CLOSING STREAM");
			out.close();
		}
		return new AsyncResult<Boolean>(true);
	}
}