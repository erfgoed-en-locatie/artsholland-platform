//package org.waag.ah.jms;
//
//import java.io.BufferedInputStream;
//import java.io.Closeable;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.annotation.Resource;
//import javax.ejb.Stateful;
//import javax.jms.BytesMessage;
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.Queue;
//import javax.jms.QueueConnection;
//import javax.jms.QueueConnectionFactory;
//import javax.jms.QueueSender;
//import javax.jms.QueueSession;
//
//import org.apache.log4j.Logger;
//import org.jboss.ejb3.annotation.LocalBinding;
//
//@Stateful
//@LocalBinding(jndiBinding=QueueHelper.OBJECT_NAME)  
//public class QueueHelper implements Closeable {
//	private Logger logger = Logger.getLogger(QueueHelper.class);
//	private Queue targetQueue;
//	private QueueConnection connection;
//	private QueueSession session;
//
//	public final static String OBJECT_NAME = "java:global/QueueHelper";
//	
////	@Resource(mappedName = "ConnectionFactory")        
////	protected QueueConnectionFactory factory; 
//
////	public QueueHelper(String queueName) throws NamingException {
////		this((Queue) new InitialContext().lookup(queueName));
////	}
//	
//	public QueueHelper() {
////		this.targetQueue = queue;
//		initConnection();
//	}
//	
//	private void initConnection() {         
//		try {   
//			connection = factory.createQueueConnection();
//			session = connection.createQueueSession(false, 
//					QueueSession.AUTO_ACKNOWLEDGE);
//		} catch (JMSException e) {                     
//			throw new RuntimeException("Could not initialize connection", e);              
//		}       
//	}       
//	
//	public void close() {                
//		try {                   
//			connection.close();          
//		} catch (JMSException e) {                      
//			logger.error(e.getMessage());
//		}       
//	}
//	
//	public void write(Queue queue, InputStream inputStream, Map<String, String> metadata)
//			throws IOException {
//		try {
//			BytesMessage msg = session.createBytesMessage();
//			BufferedInputStream bufferedInput = 
//					new BufferedInputStream(inputStream);
//			msg.setObjectProperty("JMS_HQ_InputStream", bufferedInput);
//			send(msg, metadata);		
//		} catch (JMSException e) {
//			throw new IOException(e.getMessage(), e);
//		} finally {
//			inputStream.close();
//		}
//	}
//	
//	private void send(Message message, Map<String, String> metadata) throws JMSException {
//		for (Entry<String, String> prop : metadata.entrySet()) {
//			message.setStringProperty(prop.getKey(), prop.getValue());
//		}	
//		QueueSender queueSender = session.createSender(targetQueue);
//		queueSender.send(message);
//	}
//}