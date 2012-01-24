package org.waag.ah.service.scheduler;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.Schedule;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/schedule")})
public class SchedulerServiceBean {
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
	        queue = (Queue) ctx.lookup("queue/importer/fetch");
	        connect();
		} catch (NamingException e) {
//			e.printStackTrace();
			logger.warn(e.getMessage());
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}

	private void connect() throws JMSException {
        session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		sender = session.createSender(queue);
	}

	@PreDestroy
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}

	public void onMessage(Message msg) {}
	
    @Schedule(persistent=false, minute="*/5", hour="*")
    public void automaticTimeout() {
    	if (sender == null) {
    		return;
    	}
    	try {
//    		sender.send(session.createTextMessage("http://waxworks.nl/events.xml"));
    		sender.send(session.createTextMessage("http://waxworks.nl/amsterdam.xml"));
			this.setLastScheduledImport(new Date());
			logger.info("Message sent successfully to import queue");
    	} catch (IllegalStateException e) {
    		try {
				connect();
			} catch (JMSException e1) {
				logger.error("Reconnecting with JMS queue failed");
			}
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