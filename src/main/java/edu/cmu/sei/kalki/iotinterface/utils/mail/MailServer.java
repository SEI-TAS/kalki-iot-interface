package edu.cmu.sei.kalki.iotinterface.utils.mail;

import edu.cmu.sei.kalki.iotinterface.utils.Config;
import org.subethamail.smtp.server.SMTPServer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class MailServer {

    private static Logger logger = Logger.getLogger("myLogger");

    private List<EventObserver> mailObservers = new ArrayList<EventObserver>();

    public static MailServer mailServer;

    public MailServer(int port)
    {
        MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory(this) ;
        SMTPServer smtpServer = new SMTPServer(myFactory);
        smtpServer.setPort(port);
        smtpServer.start();
    }

    public static void registerObserver(EventObserver o){
        mailServer.mailObservers.add(o);
    }

    public static void notify(String fromEmail){
        logger.info("notify! "+fromEmail);
        for(EventObserver o : mailServer.mailObservers){
            o.notify(fromEmail);
        }
    }

    public static void initialize() {
        if (mailServer == null){
            logger.info("Initializing mail server");
            mailServer = new MailServer(Integer.valueOf(Config.data.get("MAIL_PORT")));
            logger.info("[MailServer] Successfully initialized mail Server.");
        } else {
            logger.info("[MailServer] mail Server already initialized");
        }
    }
}
