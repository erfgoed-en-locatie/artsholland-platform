package org.waag.ah.service.importer;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;

public class JMSQueueWriter implements DocumentWriter {
	private Logger logger = Logger.getLogger(JMSQueueWriter.class);
	private QueueConnection connection;
	private QueueSession session;
	private QueueSender queueSender;

	public JMSQueueWriter(String queueName) throws Exception {
		try {
			InitialContext ctx = new InitialContext();
			QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("SAILConnectionFactory");
			Queue queue = (Queue) ctx.lookup(queueName);
			this.connection = factory.createQueueConnection();
			this.session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
			this.queueSender = session.createSender(queue);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void write(String message, Metadata metadata) throws IOException {
		try {
			TextMessage msg = session.createTextMessage(message);
			for (String name : metadata.names()) {
				msg.setStringProperty(name.replace("-", ""), metadata.get(name));
			}
			queueSender.send(msg);		
			logger.info("Message sent successfully to peristence queue");
		} catch (JMSException e) {
			throw new IOException(e.getMessage(), e);
		}
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
