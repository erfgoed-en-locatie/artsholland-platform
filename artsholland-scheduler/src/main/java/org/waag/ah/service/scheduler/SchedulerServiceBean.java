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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@MessageDriven(
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/scheduler")})
public class SchedulerServiceBean implements MessageListener {
	final static Logger logger = LoggerFactory.getLogger(SchedulerServiceBean.class);

	@Resource(name="java:/queue/importer/parse")  
	private Queue targetQueue;
	
	@Resource(name="java:/ConnectionFactory")        
	protected QueueConnectionFactory factory; 
	
	private QueueConnection conn;
		
	@PostConstruct  
	public void init() {         
		try {                   
			conn = factory.createQueueConnection();              
		} catch (JMSException e) {                     
			throw new RuntimeException("Could not initialize connection", e);              
		} 
	}       
	
	@PreDestroy     
	public void cleanUp() {                
		try {                   
			conn.close();          
		} catch (JMSException e) {                      
			logger.error(e.getMessage());
		} 
	}       
	
	public void onMessage(Message msg) {}
	
//    @Schedule(persistent=false, minute="*/1", hour="*")
    public void automaticTimeout() {
		QueueSession session = null;         
		QueueSender sender = null;  
		try {
			
			session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);                   
//			session = conn.createQueueSession(false, QueueSession.CLIENT_ACKNOWLEDGE);                   
			sender = session.createSender(targetQueue);                   

//			TextMessage msg = session.createTextMessage("http://127.0.0.1/ah/nub/amsterdam.xml");                      
//			TextMessage msg = session.createTextMessage("http://127.0.0.1/ah/nub/events.xml");                      
//			TextMessage msg = session.createTextMessage("http://waxworks.nl/amsterdam.xml");                      
//			TextMessage msg = session.createTextMessage("http://waxworks.nl/events.xml");                      
			
			String[] sourceURLs = {
//				"http://localhost/ah/nub/v4/event.xml",
//				"http://localhost/ah/nub/v4/production.xml",
//				"http://localhost/ah/nub/v4/location.xml",
//				"http://127.0.0.1/ah/nub/amsterdam.xml",
//				"http://127.0.0.1/ah/nub/events.xml",
				"http://accept.ps4.uitburo.nl/api/events?key=505642b12881b9a60688411a333bc78b&rows=500",
//				"http://accept.ps4.uitburo.nl/api/productions?key=505642b12881b9a60688411a333bc78b&rows=10000",
//				"http://accept.ps4.uitburo.nl/api/locations?key=505642b12881b9a60688411a333bc78b&rows=10000"
			};
			
			for (String sourceURL: sourceURLs) {
				logger.info("Scheduling URL for import: "+sourceURL);
				TextMessage msg = session.createTextMessage(sourceURL);
				sender.send(msg);
				logger.info("Message sent successfully to import queue");
			}
			
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
