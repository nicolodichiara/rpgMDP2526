package it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Gestore degli errori di parsing XML (JAXP).
 * Senza un ErrorHandler registrato sul DocumentBuilder,
 * gli errori di parsing passano inosservati.
 */
public class XmlErrorHandler implements ErrorHandler {

    @Override
    public void warning(SAXParseException exception) {
        System.out.println("ATTENZIONE XML (riga " + exception.getLineNumber() + "): "
                + exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }
}
