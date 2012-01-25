//package org.waag.ah.jms;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.annotation.Resource;
//import javax.jms.Connection;
//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.MessageListener;
//import javax.jms.Queue;
//import javax.jms.QueueConnectionFactory;
//
//public abstract class AbstractQueueBean implements MessageListener {
//
//	@Resource(mappedName = "queue/importers/store") // can be topic too       
//	private Queue targetDestination;        
//	
//	@Resource(mappedName = "QueueConnectionFactory") // or ConnectionFactory        
//	private QueueConnectionFactory factory; 
//	
//	private Connection conn;        
//	
//	public void onMessage(Message m) {              
//		// parse message and do what you need to do             
//		//...             
//		// do something with the message and the JBoss JMS destination          
////		useJmsDestination("messageString");               
//	}       
//	
////	private void useJmsDestination(String text) {           
////		Session session = null;         
////		MessageProducer producer = null;  
////		
////		try {                   
////			session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);                   
////			producer = session.createProducer(targetDestination);                   
////			TextMessage msg = session.createTextMessage(text);                      
////			producer.send(msg);             
////		} catch (JMSException e) {                      
////			throw new RuntimeException(e);          
////		} finally {                     
////			try {                           
////				if (producer != null) {                                 
////					producer.close();                               
////				}                               
////				if (session != null) {                                  
////					session.close();                                
////				}                       
////			} catch (JMSException e) {                              
////				// handle error, should be non-fatal, as the message is already sent.                   
////			}               
////		}       
////	}       
//	
//	@PostConstruct  
//	public void init() {           
//		initConnection();               
//		// other initialization logic          
//	}       
//	
//	@PreDestroy     
//	public void cleanUp() {                
//		closeConnection();             
//	 	// other cleanup logic          
//	}       
//	
//	private void initConnection() {         
//		try {                   
//			conn = factory.createConnection();              
//		} catch (JMSException e) {                     
//			throw new RuntimeException("Could not initialize connection", e);              
//		}       
//	}       
//	
//	private void closeConnection() {                
//		try {                   
//			conn.close();          
//		} catch (JMSException e) {                      
//			// handle error, should be non-fatal, as the connection is being closed         
//		}       
//	}
//}