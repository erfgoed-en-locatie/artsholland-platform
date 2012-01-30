package org.waag.ah.service.scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.Schedule;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

@MessageDriven(
	activationConfig = {
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/schedule")})
public class SchedulerServiceBean implements MessageListener {
	private Logger logger = Logger.getLogger(SchedulerServiceBean.class);

	@Resource(mappedName = "queue/importer/fetch")       
	private Queue targetQueue;
	
	@Resource(mappedName = "ConnectionFactory")        
	private QueueConnectionFactory factory; 
	
	private QueueConnection conn;
	
	@PostConstruct  
	public void init() {           
		initConnection();               
	}       
	
	@PreDestroy     
	public void cleanUp() {                
		closeConnection();             
	}       
	
	private void initConnection() {         
		try {                   
			conn = factory.createQueueConnection();              
		} catch (JMSException e) {                     
			throw new RuntimeException("Could not initialize connection", e);              
		}       
	}       
	
	private void closeConnection() {                
		try {                   
			conn.close();          
		} catch (JMSException e) {                      
			logger.error(e.getMessage());
		}       
	}
	
	public void onMessage(Message msg) {}
	
    @Schedule(persistent=false, minute="*/5", hour="*")
    public void automaticTimeout() {
		QueueSession session = null;         
		QueueSender sender = null;  
		try {       	
			session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);                   
//			session = conn.createQueueSession(false, QueueSession.CLIENT_ACKNOWLEDGE);                   
			sender = session.createSender(targetQueue);                   
			TextMessage msg = session.createTextMessage("http://127.0.0.1/ah/nub/amsterdam.xml");                      
//			TextMessage msg = session.createTextMessage("http://127.0.0.1/ah/nub/events.xml");                      
//			TextMessage msg = session.createTextMessage("http://waxworks.nl/amsterdam.xml");                      
//			TextMessage msg = session.createTextMessage("http://waxworks.nl/events.xml");                      
			sender.send(msg); 
			logger.info("Message sent successfully to import queue");
		} catch (JMSException e) {                      
			throw new RuntimeException(e);          
		} finally {                     
			try {                           
				if (sender != null) {                                 
					sender.close();                               
				}                               
				if (session != null) {                                  
					session.close();                                
				}                       
			} catch (JMSException e) {                              
				logger.error(e.getMessage());
			}               
		}     	
    }
}