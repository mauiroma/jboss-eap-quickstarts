package org.jboss.as.quickstarts.rshelloworld;



import org.json.simple.JSONObject;
import java.util.Date;
import java.util.Enumeration;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;


@Path("/queue")
public class QueueBrowse
{
    private Connection qcon;
    private Session qsession;
    private QueueBrowser qbrowser;



    @GET
    @Path("/browse")
    @Produces({ "application/json" })
    public String displayQueue(@QueryParam("cf") String connectionFactoryJndiName, @QueryParam("queue") String queueJndiName, @QueryParam("print") boolean printMessage) throws JMSException {
        JSONObject response = new JSONObject();
        try {
            InitialContext context = new InitialContext();
            ConnectionFactory qconFactory = (ConnectionFactory) context.lookup(connectionFactoryJndiName);
            qcon = qconFactory.createConnection();
            qsession = qcon.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) context.lookup(queueJndiName);
            qbrowser = qsession.createBrowser(queue);
            qcon.start();

            Enumeration e = qbrowser.getEnumeration();
            if (! e.hasMoreElements()) {
                response.put("Result", "No message on ["+queue.getQueueName()+"]");
            } else {
                JSONObject listMessages = new JSONObject();
                int messages = 0;
                while (e.hasMoreElements()) {
                    messages++;
                    JSONObject message = new JSONObject();
                    Message m = (Message) e.nextElement();

                    Enumeration messagePropertiesName =  m.getPropertyNames();
                    while (messagePropertiesName.hasMoreElements()) {
                        String propName = (String) messagePropertiesName.nextElement();
                        String propValue = m.getStringProperty(propName);
                        message.put(propName,propValue);
                    }
                    message.put("JMSTimestamp(Delivered)",new Date(m.getJMSTimestamp()).toString());
                    if (m.getJMSExpiration() > 0) {
                        message.put("JMSExpiration",new Date( m.getJMSExpiration()));
                    }else {
                        message.put("JMSExpiration","never");
                    }

                    message.put("JMSPriority",m.getJMSPriority());
                    message.put("JMSDeliveryMode",(m.getJMSDeliveryMode() == DeliveryMode.PERSISTENT ?"PERSISTENT" : "NON_PERSISTENT"));
                    message.put("JMSCorrelationID",m.getJMSCorrelationID());
                    message.put("JMSReplyTo",m.getJMSReplyTo());
                    message.put("JMSType",m.getJMSType());
                    if (printMessage && m instanceof TextMessage) {
                        message.put("TextMessage",((TextMessage)m).getText() );
                    }
                    listMessages.put("Message "+m.getJMSMessageID(),message);
                }
                response.put(queue.getQueueName() +" #["+messages+"]",listMessages);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            response.put("Exception", e1);
        } finally {
            if(qbrowser!=null){
                qbrowser.close();
            }
            if(qsession!=null) {
                qsession.close();
            }
            if(qcon!=null) {
                qcon.close();
            }
        }
        return response.toString();
    }


}