package app.test.log4j2;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;


import app.interfaces.IAppData;
import app.loadTime.LoadTime;
 
public class Log4j2Example implements IAppData {
 
    private static final Logger LOG = LogManager.getLogger(Log4j2Example.class);
 
    public static void main(String[] args) {
    	
    	
    	
    	new LogConfigXML().createXML();

    	LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
//    	File file = new File("log4j2.xml");
    	File file = new File(IAppData.getAppDataFile("logs") + File.separator + "log.xml");
    	 
    	// this will force a reconfiguration
    	context.setConfigLocation(file.toURI());

    	
        LOG.debug("This Will Be Printed On Debug");
        LOG.info("This Will Be Printed On Info");
        LOG.warn("This Will Be Printed On Warn");
        LOG.error("This Will Be Printed On Error");
        LOG.fatal("This Will Be Printed On Fatal");
 
        LOG.info("Appending string: {}.", "Hello, World");
    }

}
