package org.jboss.as.quickstarts.rshelloworld;



import org.json.simple.JSONObject;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.annotation.Resource;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

    @Resource(lookup = "/jms/riadCF")
    ConnectionFactory qconFactory;


    @GET
    @Path("/browse")
    @Produces({ "application/json" })
    public String displayQueue(@QueryParam("queue") String queueJndiName, @QueryParam("print") boolean printMessage) throws JMSException {
        JSONObject response = new JSONObject();
        try {
            qcon = qconFactory.createConnection();
            qsession = qcon.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) new InitialContext().lookup(queueJndiName);
            qbrowser = qsession.createBrowser(queue);
            qcon.start();

            Enumeration e = qbrowser.getEnumeration();
            if (! e.hasMoreElements()) {
                response.put("Result", "No message on ["+queue.getQueueName()+"]");
            } else {
                JSONObject listMessages = new JSONObject();
                while (e.hasMoreElements()) {
                    JSONObject message = new JSONObject();
                    Message m = (Message) e.nextElement();

                    Enumeration messagePropertiesName =  m.getPropertyNames();
                    while (messagePropertiesName.hasMoreElements()) {
                        String propName = (String) messagePropertiesName.nextElement();
                        String propValue = m.getStringProperty(propName);
                        message.put(propName,propValue);
                    }

                    message.put("Delivered",new Date(m.getJMSTimestamp()).toString());

                    message.put("Delivered Oracle", new Date(m.getLongProperty("JMS_BEA_DeliveryTime")).toString());

                    if (m.getJMSExpiration() > 0) {
                        message.put("Expires",new Date( m.getJMSExpiration()));
                    }else {
                        message.put("Expires","never");
                    }

                    message.put("Priority",m.getJMSPriority());
                    message.put("Mode",(m.getJMSDeliveryMode() == DeliveryMode.PERSISTENT ?"PERSISTENT" : "NON_PERSISTENT"));
                    message.put("Correlation ID",m.getJMSCorrelationID());
                    message.put("Reply to",m.getJMSReplyTo());
                    message.put("Message type",m.getJMSType());
                    if (printMessage && m instanceof TextMessage) {
                        message.put("TextMessage",((TextMessage)m).getText() );
                    }
                    listMessages.put("Message "+m.getJMSMessageID(),message);
                }
                response.put(queue.getQueueName(),listMessages);
            }
        } catch (Exception e1) {
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
        return response.toJSONString();
    }


}