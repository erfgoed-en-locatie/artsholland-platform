package org.waag.artsholland.service.scheduler;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/schedule")})
public class SchedulerServiceBean implements SchedulerService {
	private Logger logger = Logger.getLogger(SchedulerServiceBean.class);

	private Date lastScheduledImport;
	private QueueConnectionFactory factory;
	private InitialContext ctx;
	private QueueConnection connection;
	private QueueSession session;
	private Queue queue;
	private QueueSender sender;

	@PostConstruct
	public void create() {
		try {
			ctx = new InitialContext();
	        factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
	        connection = factory.createQueueConnection();
	        session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
	        queue = (Queue) ctx.lookup("queue/import");
			sender = session.createSender(queue);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}

	@PreDestroy
	public void destroy() {
		try {
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}

	public void onMessage(Message msg) {}
	
    @Schedule(persistent=false, minute="*/1", hour="*")
    public void automaticTimeout() {
    	try {
			TextMessage msg = session.createTextMessage();
			msg.setStringProperty("url", "http://127.0.0.1/ah/nub/events.xml");
			sender.send(msg);
			this.setLastScheduledImport(new Date());
			logger.info("Message sent successfully to import queue");
		} catch (JMSException e) {
			logger.error("Error while sending message to import queue", e);
			e.printStackTrace();
	    }
    }
    
	private void setLastScheduledImport(Date timeout) {
		this.lastScheduledImport = timeout;
	}
    
	public String getLastScheduledImport() {
        if (lastScheduledImport != null) {
            return lastScheduledImport.toString();
        } else {
            return "never";
        }
    }
}
