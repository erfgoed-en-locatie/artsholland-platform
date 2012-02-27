package org.waag.ah.service.scheduler;
//package org.waag.ah.service.scheduler;
//
//import java.util.logging.Logger;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.ejb.Remote;
//import javax.ejb.SessionContext;
//import javax.ejb.Stateless;
//import javax.jms.Connection;
//import javax.jms.ConnectionFactory;
//import javax.jms.JMSException;
//import javax.jms.MessageProducer;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//import javax.jms.Topic;
//
///**
// * Session Bean implementation class PerformTask
// */
//@Stateless
//@Remote({
//	Importer.class
//})
//public class PerformTask implements Importer {
//    static final Logger logger = Logger.getLogger("PublisherBean");
//    private Connection connection = null;
//    
//    @Resource(mappedName = "jms/ConnectionFactory")
//    private ConnectionFactory connectionFactory;
//    
//    @Resource
//    private SessionContext sc;
//    
//    @Resource(mappedName = "jms/Topic")
//    private Topic topic;
//
//    /**
//     * Default constructor. 
//     */
//    public PerformTask() {
//    }
//
//    /**
//     * Creates the connection.
//     */
//    @PostConstruct
//    public void makeConnection() {
//        try {
//            connection = connectionFactory.createConnection();
//        } catch (Throwable t) {
//            // JMSException could be thrown
//            logger.severe(
//                    "PublisherBean.makeConnection:" + "Exception: "
//                    + t.toString());
//        }
//        importUrl();
//    }
//    
//    /**
//     * Creates session, publisher, and message.  Publishes
//     * messages after setting their NewsType property and using
//     * the property value as the message text. Messages are
//     * received by MessageBean, a message-driven bean that uses a
//     * message selector to retrieve messages whose NewsType
//     * property has certain values.
//     */
//    public void importUrl() {
//        Session session = null;
//        MessageProducer publisher = null;
//        TextMessage message = null;
//
//        try {
//            session = connection.createSession(true, 0);
//            publisher = session.createProducer(topic);
//            message = session.createTextMessage();
//
//            message.setStringProperty("url", "http://127.0.0.1/ah/nub/events.xml");
//            logger.info("PUBLISHER: Requesting " + message.getStringProperty("url"));
//            publisher.send(message);
//        } catch (Throwable t) {
//            // JMSException could be thrown
//            logger.severe(
//                    "PublisherBean.publishNews: " + "Exception: "
//                    + t.toString());
//            sc.setRollbackOnly();
//        } finally {
//            if (session != null) {
//                try {
//                    session.close();
//                } catch (JMSException e) {
//                }
//            }
//        }
//    }
//}
