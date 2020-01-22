package edu.cmu.sei.kalki.utils.mail;

import org.subethamail.smtp.server.SMTPServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.json.JSONTokener;

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

            try{

                InputStream fs = new FileInputStream("config.json");
                JSONTokener parser = new JSONTokener(fs);
                JSONObject config = new JSONObject(parser);
                int port = config.getInt("MAIL_PORT");
                fs.close();

                mailServer = new MailServer(port);
                logger.info("[MailServer] Succesfully initialized mail Server.");
            }
            catch(IOException e){
                logger.severe("[MailServer] Error intializing mail server.");
                System.exit(-1);
            }

        } else {
            logger.info("[MailServer] mail Server already initialized");
        }
    }
}
