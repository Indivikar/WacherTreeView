package app.test.log4j2;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;

import app.interfaces.IAppData;
import app.view.functions.notification.Notification;
import app.view.functions.notification.Notification.NotificationType;


public class LogConfigXML implements IAppData {

	// config
	private String dateiNameConfiXML = "log.xml";
	private String dateiNameLogFile = "log.ini";
	
	private boolean consolenAusgabeLog = false;
	private boolean consolenAusgabeXML = false;

    public LogConfigXML config() {
        return new LogConfigXML();
    }
	
    public LogConfigXML consolenAusgabeLog() {
    	// Soll alles was in die Log-Datei geschrieben wird, auch in der Console angezeigt werden?
        this.consolenAusgabeLog = true;
        return this;
    }
    
    public LogConfigXML consolenAusgabeXML() {
    	// Soll die generierte XML-Datei in der Console angezeigt werden?
        this.consolenAusgabeXML = true;
        return this;
    }
    
	public void createXML() {
		try {			
			File f = new File(IAppData.getAppDataFile("logs") + File.separator + dateiNameConfiXML);
			
			DocumentBuilderFactory dbFactory =
			         DocumentBuilderFactory.newInstance();
			         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			         Document doc = dBuilder.newDocument();
			         
			         // root element
			         Element configurationElement = doc.createElement("Configuration");
			         doc.appendChild(configurationElement);
			         Attr attrConfiguration = doc.createAttribute("xmlns");
			         attrConfiguration.setValue("http://logging.apache.org/log4j/2.0/config");
			         configurationElement.setAttributeNode(attrConfiguration);
			         
				         // Properties element			      
				         Element propertiesElement = doc.createElement("Properties");
				         configurationElement.appendChild(propertiesElement);
	
				         	 // Property element
					         Element propertyElement = doc.createElement("Property");
					         propertiesElement.appendChild(propertyElement);
					         Attr attrProperty = doc.createAttribute("name");
					         attrProperty.setValue("basePath");
					         propertyElement.setAttributeNode(attrProperty);
					         propertyElement.appendChild(doc.createTextNode(f.getParent()));
			         
			         // Appenders element
			         Element appendersElement = doc.createElement("Appenders");
			         configurationElement.appendChild(appendersElement);
			         
				         // File Appender
				         Element fileElement = doc.createElement("File");
				         appendersElement.appendChild(fileElement);
				         Attr attrFile1 = doc.createAttribute("name");
				         attrFile1.setValue("FILE");
				         fileElement.setAttributeNode(attrFile1);
				         Attr attrFile2 = doc.createAttribute("fileName");
				         attrFile2.setValue("${basePath}/" + dateiNameLogFile);
				         fileElement.setAttributeNode(attrFile2);
				         Attr attrFile3 = doc.createAttribute("append");
				         attrFile3.setValue("true");
				         fileElement.setAttributeNode(attrFile3);
			         
				         	// PatternLayout element
					         Element patternLayoutElement = doc.createElement("PatternLayout");
					         fileElement.appendChild(patternLayoutElement);
					         Attr attrPatternLayout = doc.createAttribute("pattern");
					         attrPatternLayout.setValue("%-5p | %d{yyyy-MM-dd HH:mm:ss} | [%t] %C{2} (%F:%L) - %m%n");
					         patternLayoutElement.setAttributeNode(attrPatternLayout);
					         
				         // Console Appender
					     if (consolenAusgabeLog) {
							 Element consoleElement = doc.createElement("Console");
					         appendersElement.appendChild(consoleElement);
					         Attr attrConsole1 = doc.createAttribute("name");
					         attrConsole1.setValue("STDOUT");
					         consoleElement.setAttributeNode(attrConsole1);
					         Attr attrConsole2 = doc.createAttribute("target");
					         attrConsole2.setValue("SYSTEM_OUT");
					         consoleElement.setAttributeNode(attrConsole2);
						         
					         	// PatternLayout element
						         Element patternLayoutElement2 = doc.createElement("PatternLayout");
						         consoleElement.appendChild(patternLayoutElement2);
						         Attr attrPatternLayout2 = doc.createAttribute("pattern");
						         attrPatternLayout2.setValue("%-5p | %d{yyyy-MM-dd HH:mm:ss} | [%t] %C{2} (%F:%L) - %m%n");
						         patternLayoutElement2.setAttributeNode(attrPatternLayout2);
					     }

			         
			         // Loggers element
			         Element loggersElement = doc.createElement("Loggers");
			         configurationElement.appendChild(loggersElement); 
			         
				         // Attribut Logger
				         Element loggerElement = doc.createElement("Logger");
				         loggersElement.appendChild(loggerElement);
				         Attr attrlogger = doc.createAttribute("name");
				         attrlogger.setValue("com.jcg");
				         loggerElement.setAttributeNode(attrlogger);
				         Attr attrlogger2 = doc.createAttribute("level");
				         attrlogger2.setValue("debug");
				         loggerElement.setAttributeNode(attrlogger2);
			         
			         	 // Root element
				         Element rootElement = doc.createElement("Root");
				         loggersElement.appendChild(rootElement);
				         Attr attrRoot = doc.createAttribute("level");
				         attrRoot.setValue("info");
				         rootElement.setAttributeNode(attrRoot);
				         
				         	 // Attribut AppenderRef 1 
					         Element appenderRefElement1 = doc.createElement("AppenderRef");
					         rootElement.appendChild(appenderRefElement1);
					         Attr attrAppenderRef1 = doc.createAttribute("ref");
					         attrAppenderRef1.setValue("STDOUT");
					         appenderRefElement1.setAttributeNode(attrAppenderRef1);
				         
				         	 // Attribut AppenderRef 2
					         Element appenderRefElement2 = doc.createElement("AppenderRef");
					         rootElement.appendChild(appenderRefElement2);
					         Attr attrAppenderRef2 = doc.createAttribute("ref");
					         attrAppenderRef2.setValue("FILE");
					         appenderRefElement2.setAttributeNode(attrAppenderRef2);
					         					        
			         // write the content into xml file
			         TransformerFactory transformerFactory = TransformerFactory.newInstance();
			         Transformer transformer = transformerFactory.newTransformer();
			         // 
			         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			         
			         DOMSource source = new DOMSource(doc);			         
			         StreamResult result = new StreamResult(f);
			         transformer.transform(source, result);
			         			         			         
			         // Output to console for testing
			         if (consolenAusgabeXML) {
						 StreamResult consoleResult = new StreamResult(System.out);
				         transformer.transform(source, consoleResult);
					 }
		         
			         setConfigLocationXML();
			      } catch (Exception e) {
			         e.printStackTrace();
			      }
	}

	private void setConfigLocationXML() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
    	File file = new File(IAppData.getAppDataFile("logs") + File.separator + dateiNameConfiXML);
    	 
    	// this will force a reconfiguration
    	context.setConfigLocation(file.toURI());
	}
	
}
