package org.waag.ah.service.importer;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Logger;

import com.Ostermiller.util.CircularByteBuffer;


@Stateless
public class AsyncBean {
	private Logger logger = Logger.getLogger(AsyncBean.class);
	
	@Asynchronous
	public Future<String> copyOutputStream(Message message, 
			CircularByteBuffer buffer) throws JMSException {
		logger.info("copyOutputStream START");
		message.setObjectProperty("JMS_HQ_SaveStream", buffer.getOutputStream());		
		logger.info("copyOutputStream FINISH");
		return new AsyncResult<String>("");
	}
}
