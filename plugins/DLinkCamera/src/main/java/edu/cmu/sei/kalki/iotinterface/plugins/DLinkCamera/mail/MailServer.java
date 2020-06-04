package edu.cmu.sei.kalki.iotinterface.plugins.DLinkCamera.mail;

import org.subethamail.smtp.server.SMTPServer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MailServer {

    private static Logger logger = Logger.getLogger("myLogger");

    private List<EventObserver> mailObservers = new ArrayList<EventObserver>();
    public SMTPServer smtpServer;

    public static MailServer mailServer;

    public MailServer(int port)
    {
        MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory() ;
        smtpServer = new SMTPServer(myFactory);
        smtpServer.setPort(port);
    }

    public static void start() {
        if(mailServer != null) {
            logger.info("Starting mail server");
            mailServer.smtpServer.start();
        }
        else {
            logger.info("Can't start mail server, not properly initialized.");
        }
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

    public static void initialize(int port) {
        if (mailServer == null){
            logger.info("Initializing mail server");
            mailServer = new MailServer(port);
            logger.info("[MailServer] Successfully initialized mail server.");
        } else {
            logger.info("[MailServer] mail server already initialized");
        }
    }

    public static void stop() {
        if(mailServer != null){
            mailServer.smtpServer.stop();
            mailServer.smtpServer = null;
        }
        mailServer = null;
    }
}
