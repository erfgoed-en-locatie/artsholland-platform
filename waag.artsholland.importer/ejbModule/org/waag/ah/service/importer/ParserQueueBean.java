package org.waag.ah.service.importer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import org.waag.ah.jms.StreamingMessageBuffer;

import com.Ostermiller.util.CircularByteBuffer;


@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/parse"),
		@ActivationConfigProperty(propertyName="maxSession", propertyValue = "1")})
//@Pool(value="StrictMaxPool", maxSize=5, timeout=2000) 
//@TransactionManagement(value=TransactionManagementType.BEAN) 
//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) 
//@TransactionTimeout(value=10000) 
public class ParserQueueBean implements MessageListener, ExceptionListener {
	private Logger logger = Logger.getLogger(ParserQueueBean.class);
	private QueueConnection connection;
	private CircularByteBuffer msgOutBuf;
	
	private final int BUFFER_SIZE = 65536;
	
	@Resource(mappedName = "ConnectionFactory")        
	protected QueueConnectionFactory factory; 

	@Resource(mappedName = "queue/importer/store")       
	private Queue queue;
	
	private @EJB StreamingMessageBuffer streamBuffer;
	private @EJB StreamingMessageParser streamParser;
	
	@PostConstruct
	public void create() throws JMSException {
		connection = factory.createQueueConnection();
		msgOutBuf = new CircularByteBuffer(BUFFER_SIZE);	
	}

	@PreDestroy
	public void destroy() {
		try {			
			connection.close();
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void onMessage(Message msgIn) {
		CircularByteBuffer msgInBuf = new CircularByteBuffer(BUFFER_SIZE);	
		try {
			QueueSession session = connection.createQueueSession(true, 
					Session.SESSION_TRANSACTED); 
			BytesMessage msgOut = session.createBytesMessage();
			msgOut.setObjectProperty("JMS_HQ_InputStream", 
					msgOutBuf.getInputStream());
			
			streamBuffer.pipedReader((BytesMessage) msgIn, msgInBuf.getOutputStream());
			
			InputStream inputStream = msgInBuf.getInputStream();
			OutputStream outputStream = msgOutBuf.getOutputStream();
			
			Future<Boolean> parseResult = streamParser.parseStreamMessage(
					(BytesMessage) msgIn, 
					inputStream, 
					outputStream);
			
			msgOut.setStringProperty(Properties.SOURCE_URL, 
					msgIn.getStringProperty(Properties.SOURCE_URL));
			msgOut.setStringProperty(Properties.CHARSET, 
					msgIn.getStringProperty(Properties.CHARSET));
			msgOut.setStringProperty(Properties.CONTENT_TYPE, 
					"application/rdf+xml");
			
			logger.info("PARSING MESSAGE");

			QueueSender queueSender = session.createSender(queue);
			queueSender.send(msgOut);
			
			try {
				parseResult.get();
				session.commit();
				logger.info("SENT MESSAGE");
			} catch(ExecutionException e) {
				logger.error(e.getCause().getMessage(), e);
				session.rollback();
			} finally {
//				inputStream.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			msgInBuf.clear();
			msgOutBuf.clear();
		}
		logger.info("RETURNING");
	}

	@Override
	public void onException(JMSException e) {
		logger.error("JMS EXCEPTION: "+e.getMessage());
	}	
}