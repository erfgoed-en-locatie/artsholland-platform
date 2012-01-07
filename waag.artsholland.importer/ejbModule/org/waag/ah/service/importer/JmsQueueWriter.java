package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.Writer;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class JmsQueueWriter extends Writer {
	private Logger logger = Logger.getLogger(JmsQueueWriter.class);
	private QueueSender queueSender;
	private QueueConnection connection;
	private QueueSession session;

	public JmsQueueWriter(String queueName) throws NamingException, 
			JMSException {
		InitialContext ctx = new InitialContext();
        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
        Queue queue = (Queue) ctx.lookup(queueName);
        this.connection = factory.createQueueConnection();
        this.session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        this.queueSender = session.createSender(queue);
    }

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		logger.debug("Received parsed document");
		try {
			queueSender.send(session.createTextMessage(new String(cbuf)));
		} catch (JMSException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public void flush() throws IOException {}

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