package org;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Log {
    private static Log instance;
    private Logger logger;
    public static FileHandler handler;
    public void setInfoLog(String message) {

        this.logger.info(message);

    }
    public void setWarning(String message) {

        this.logger.warning(message);


    }
    public Log() throws IOException {
        logger = Logger.getLogger(Log.class.getName());
        logger.setUseParentHandlers(false);

        MyFormatter formatter = new MyFormatter();

        handler = new FileHandler("log.txt", true);

        handler.setFormatter(formatter);

        logger.addHandler(handler);
    }


    /**
     * This class method returns the "only" instance available for this class
     * If the instance is still null, it gets instantiated.
     * Being a class method you can call it from anywhere and it will
     * always return the same instance.
     */
    public static Log getInstance() throws IOException {
        if( instance == null ) {
            instance = new Log();
        }
        return instance;
    }
}

class MyFormatter extends Formatter {
    // Create a DateFormat to format the logger timestamp.
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(df.format(new Date(record.getMillis()))).append(" - ");

        builder.append("[").append(record.getLevel()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }

    public String getHead(Handler h) {
        return super.getHead(h);
    }

    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
