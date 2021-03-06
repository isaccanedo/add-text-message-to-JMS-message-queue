import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;


public class SimpleProducer {
    private static final Log LOG = LogFactory.getLog(SimpleProducer.class);

    private static final Boolean NON_TRANSACTED = false;
//    private static final long MESSAGE_TIME_TO_LIVE_MILLISECONDS = 3000;
    private static final long MESSAGE_TIME_TO_LIVE_MILLISECONDS = 0;
    private static final int MESSAGE_DELAY_MILLISECONDS = 100;
    private static final int NUM_MESSAGES_TO_BE_SENT = 100;
    private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    private static final String DESTINATION_NAME = "queue/simple";
    private static final String JMSX_GROUP_ID = "JMSXGroupID";


    public static void main(String[] args) {
        Connection connection = null;
        try {/* w  w w .  j  a va2  s.c  o m*/
            // start up an init context... properties file must be named "jndi.properties" on the root
            // of the classpath
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);

            // sets the JMSExpiration
            producer.setTimeToLive(MESSAGE_TIME_TO_LIVE_MILLISECONDS);


            for (int i = 1; i <= NUM_MESSAGES_TO_BE_SENT; i++) {
                TextMessage message = session.createTextMessage(i + ". message sent");
                String messageCode = "Msg_" + (i % 2);
                message.setStringProperty(JMSX_GROUP_ID, messageCode);
                LOG.info("Sending to destination: " + destination.toString() + " this text: '" + message.getText() + "'");
                producer.send(message);

                // delay the sending for a bit
                Thread.sleep(MESSAGE_DELAY_MILLISECONDS);
            }

            producer.close();
            session.close();

        } catch (Exception e) {
            LOG.error(e);
        }
        finally {
            // got to clean up the connections and other resources!
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOG.error(e);
                }
            }
        }
    }

}
