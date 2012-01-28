package org.waag.ah.service.importer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.BytesMessage;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.waag.ah.jms.Properties;

import com.Ostermiller.util.CircularByteBuffer;


@MessageDriven(
	messageListenerInterface=MessageListener.class,
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/parse")})
public class ParserQueueBean implements MessageListener, ExceptionListener {
	private Logger logger = Logger.getLogger(ParserQueueBean.class);
	private QueueConnection connection;
	private CircularByteBuffer msgInBuf;
	private CircularByteBuffer msgOutBuf;
	
	private final int BUFFER_SIZE = 65536;
	
	@Resource(mappedName = "ConnectionFactory")        
	protected QueueConnectionFactory factory; 

	@Resource(mappedName = "queue/importer/store")       
	private Queue queue;
	
	@EJB
	private StreamingMessageHelper streamHelper;
	
	@PostConstruct
	public void create() throws JMSException {
		connection = factory.createQueueConnection();
		msgInBuf = new CircularByteBuffer(BUFFER_SIZE);
		msgOutBuf = new CircularByteBuffer(BUFFER_SIZE);	
	}

	@PreDestroy
	public void destroy() {
		logger.info("Destroying ParserQueueBean");
		try {			
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uses a rather cumbersome method for converting convert the JMS 
	 * outputstream to an inputstream.
	 * 
	 * @param msg
	 * @throws IOException
	 * @throws JMSException
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Jan 24, 2012
	 */
	public void onMessage(Message msgIn) {
		try {
			QueueSession session = connection.createQueueSession(true, 
					Session.SESSION_TRANSACTED); 
			BytesMessage msgOut = session.createBytesMessage();
			msgOut.setObjectProperty("JMS_HQ_InputStream", 
					msgOutBuf.getInputStream());
			
			streamHelper.readOutputStream((BytesMessage) msgIn, msgInBuf);
			
			Future<Boolean> parseResult = streamHelper.parseStreamMessage(
					(BytesMessage) msgIn, 
					msgInBuf.getInputStream(), 
					msgOutBuf.getOutputStream());
			
			msgOut.setStringProperty(Properties.SOURCE_URL, 
					msgIn.getStringProperty(Properties.SOURCE_URL));
			msgOut.setStringProperty(Properties.CHARSET, 
					msgIn.getStringProperty(Properties.CHARSET));
			msgOut.setStringProperty(Properties.CONTENT_TYPE, 
					"application/rdf+xml");

			QueueSender queueSender = session.createSender(queue);
			queueSender.send(msgOut);
			
			try {
				parseResult.get();
				session.commit();
			} catch(ExecutionException e) {
				logger.error(e.getCause().getMessage());
				session.rollback();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			msgInBuf.clear();
			msgOutBuf.clear();
		}
	}

	@Override
	public void onException(JMSException e) {
		logger.error("Got JMS exception: "+e.getMessage());
	}
}