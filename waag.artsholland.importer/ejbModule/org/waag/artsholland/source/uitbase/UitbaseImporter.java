package org.waag.artsholland.source.uitbase;
//package org.waag.artsholland.source.uitbase;
//
//import javax.ejb.ActivationConfigProperty;
//import javax.ejb.MessageDriven;
//import javax.ejb.Singleton;
//import javax.jms.Message;
//import javax.jms.MessageListener;
//
//@MessageDriven(activationConfig = {
////	@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
//	@ActivationConfigProperty(propertyName="destination", propertyValue="queue/import"),
//	@ActivationConfigProperty(propertyName="messageSelector", propertyValue="url='http://127.0.0.1/ah/nub/events.xml'")})
////@Depends ("jboss.mq.destination:service=Queue,name=queue/import")
//public @Singleton class UitbaseImporter implements MessageListener
//{
//   public void onMessage(Message recvMsg)
//   {
//      System.out.println("----------------");
//      System.out.println("Received message");
//      System.out.println("----------------");
//   }
//}
//
////package org.waag.artsholland.source.uitbase;
////
////import javax.annotation.Resource;
////import javax.ejb.ActivationConfigProperty;
////import javax.ejb.MessageDriven;
////import javax.ejb.Singleton;
////import javax.ejb.Stateless;
////import javax.jms.JMSException;
////import javax.jms.Message;
////import javax.jms.MessageListener;
////import javax.jms.TextMessage;
////
////import org.apache.log4j.Logger;
////import org.jboss.ejb3.context.spi.MessageDrivenContext;
////
/////**
//// * Message-Driven Bean implementation class for: UitbaseImporter
//// *
//// */
////@MessageDriven(
////	mappedName = "jms/ImportQueue", activationConfig =  {
////		@ActivationConfigProperty(
////				propertyName = "messageSelector", 
////				propertyValue = "url = 'http://127.0.0.1/ah/nub/events.xml'"),
////		@ActivationConfigProperty(
////				propertyName = "acknowledgeMode", 
////				propertyValue = "Client-acknowledge"), //Session.CLIENT_ACKNOWLEDGE
//////		@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
//////		@ActivationConfigProperty(propertyName = "clientId", propertyValue = "MyID"),
//////		@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "MySub")
////})
////@Singleton
////@Stateless
////public class UitbaseImporter implements MessageListener {
////    static final Logger logger = Logger.getLogger(UitbaseImporter.class);
////    
//////    @Resource
//////    public MessageDrivenContext mdc;
////    
////    public UitbaseImporter() {
////    }
////    
////	/**
////     * @see MessageListener#onMessage(Message)
////     */
////    public void onMessage(Message message) {
//////        TextMessage msg = null;
//////        try {
//////            if (message instanceof TextMessage) {
//////                msg = (TextMessage) message;
//////                logger.info("MESSAGE BEAN: Message received: " + msg.getText());
//////            } else {
//////                logger.warn("Message of wrong type: " + message.getClass().getName());
//////            }
//////            msg.acknowledge();
//////        } catch (JMSException e) {
//////            logger.fatal("MessageBean.onMessage: JMSException: " + e.toString());
//////            e.printStackTrace();
//////            mdc.setRollbackOnly();
//////        } catch (Throwable te) {
//////            logger.fatal("MessageBean.onMessage: Exception: " + te.toString());
//////            te.printStackTrace();
//////        }
////    }
////}
