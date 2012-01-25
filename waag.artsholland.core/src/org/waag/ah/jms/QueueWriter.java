package org.waag.ah.jms;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.waag.ah.DocumentWriter;

public class QueueWriter implements DocumentWriter {
	private Logger logger = Logger.getLogger(QueueWriter.class);
	private QueueConnection connection;
	private QueueSession session;
	private QueueSender queueSender;

	public QueueWriter(String queueName) throws Exception {
		try {
			InitialContext ctx = new InitialContext();
			QueueConnectionFactory factory = 
					(QueueConnectionFactory) ctx.lookup("ConnectionFactory");
			Queue queue = (Queue) ctx.lookup(queueName);
			this.connection = factory.createQueueConnection();
			this.session = connection.createQueueSession(false, 
//					QueueSession.CLIENT_ACKNOWLEDGE);
					QueueSession.AUTO_ACKNOWLEDGE);
			this.queueSender = session.createSender(queue);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void write(String message, Map<String, String> metadata) throws IOException {
		try {
			send(session.createTextMessage(message), metadata);
		} catch (JMSException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public void write(InputStream inputStream, Map<String, String> metadata)
			throws IOException {
		try {
			BytesMessage msg = session.createBytesMessage();
			BufferedInputStream bufferedInput = 
					new BufferedInputStream(inputStream);
			msg.setObjectProperty("JMS_HQ_InputStream", bufferedInput);
			send(msg, metadata);		
		} catch (JMSException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	
	private void send(Message message, Map<String, String> metadata) throws JMSException {
		for (Entry<String, String> prop : metadata.entrySet()) {
			message.setStringProperty(prop.getKey(), prop.getValue());
		}		
		queueSender.send(message);
	}
	
	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage(), e);
		}		
	}
}