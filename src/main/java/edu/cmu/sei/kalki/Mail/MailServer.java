package edu.cmu.sei.kalki.Mail;

import edu.cmu.sei.kalki.Monitors.EventObserver;
import org.subethamail.smtp.server.SMTPServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
        for(EventObserver o : mailServer.mailObservers){
            o.notify(fromEmail);
        }
    }

    public static void initialize() {
        if (mailServer == null){
            logger.info("Initializing mail server");

            try{
                Properties prop = new Properties();
                String fileName = "iot-interface.config";
                InputStream is = new FileInputStream(fileName);
                prop.load(is);

                int port = Integer.parseInt(prop.getProperty("MAIL_PORT"));
                mailServer = new MailServer(port);

                logger.info("Succesfully initialized mail Server.");
            }
            catch(IOException e){
                logger.severe("Error intializing mail server.");
                System.exit(-1);
            }

        } else {
            logger.info("Mail Server already initialized");
        }
    }
}
