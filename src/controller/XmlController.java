package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Path xmlFile;

    public XmlController() {
        this(Path.of("xml", "users.xml"));
    }

    public XmlController(Path xmlFile) {
        this.xmlFile = xmlFile.toAbsolutePath();
    }

    /** Gibt true zurueck, falls der Benutzername bereits vergeben ist. */
    public synchronized boolean userExists(String username) throws IOException {
        return findUser(loadXml(), username) != null;
    }

    /** Registriert einen normalen Benutzer. false = Benutzername existiert bereits. */
    public synchronized boolean registerUser(String username, String password)
            throws IOException {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Benutzername und Passwort duerfen nicht leer sein.");
        }

        Document doc = loadXml();
        String cleanName = username.trim();
        if (findUser(doc, cleanName) != null) {
            return false;
        }

        Element user = doc.createElement("user");
        addElement(doc, user, "name", cleanName);
        addElement(doc, user, "pwHash", hashPassword(password));
        addElement(doc, user, "registeredDate", LocalDate.now().format(DATE_FORMAT));
        addElement(doc, user, "lastLoginDate", "");
        addElement(doc, user, "isAdmin", "false");
        doc.getDocumentElement().appendChild(user);

        saveXml(doc);
        return true;
    }

    /** Prueft die Zugangsdaten und setzt bei Erfolg lastLoginDate auf heute. */
    public synchronized boolean login(String username, String password)
            throws IOException {
        if (username == null || password == null) {
            return false;
        }

        Document doc = loadXml();
        Element user = findUser(doc, username);
        if (user == null) {
            return false;
        }

        byte[] storedDigest;
        try {
            storedDigest = HexFormat.of().parseHex(getValue(user, "pwHash"));
        } catch (IllegalArgumentException ex) {
            return false; // Beschaedigter oder ungueltiger Hash in der XML
        }

        if (!MessageDigest.isEqual(storedDigest, sha256(password))) {
            return false;
        }

        NodeList dates = user.getElementsByTagName("lastLoginDate");
        String today = LocalDate.now().format(DATE_FORMAT);
        if (dates.getLength() == 0) {
            addElement(doc, user, "lastLoginDate", today);
        } else {
            dates.item(0).setTextContent(today);
        }

        saveXml(doc);
        return true;
    }

    /** Liest die Admin-Rolle; autorisiere die Aktion erst nach erfolgreichem Login. */
    public synchronized boolean isAdmin(String username) throws IOException {
        Element user = findUser(loadXml(), username);
        return user != null && Boolean.parseBoolean(getValue(user, "isAdmin"));
    }

    /** SHA-256 als Hex-String, passend zu den bereits vorhandenen XML-Eintraegen. */
    public static String hashPassword(String password) {
        return HexFormat.of().formatHex(sha256(password));
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 wird von dieser JVM nicht unterstuetzt.", ex);
        }
    }

    private Document loadXml() throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            var builder = factory.newDocumentBuilder();
            if (Files.notExists(xmlFile)) {
                Document doc = builder.newDocument();
                doc.appendChild(doc.createElement("users"));
                return doc;
            }

            try (InputStream in = Files.newInputStream(xmlFile)) {
                Document doc = builder.parse(in);
                if (doc.getDocumentElement() == null
                        || !"users".equals(doc.getDocumentElement().getTagName())) {
                    throw new IOException("Die XML-Datei muss ein <users>-Wurzelelement haben.");
                }
                return doc;
            }
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException("Benutzer-XML konnte nicht geladen werden.", ex);
        }
    }

    private void saveXml(Document doc) throws IOException {
        Files.createDirectories(xmlFile.getParent());
        removeBlankTextNodes(doc.getDocumentElement());
        Path temp = Files.createTempFile(xmlFile.getParent(), "users-", ".tmp");

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            var transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            try (OutputStream out = Files.newOutputStream(temp)) {
                transformer.transform(new DOMSource(doc), new StreamResult(out));
            }

            try {
                Files.move(temp, xmlFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp, xmlFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (TransformerException ex) {
            throw new IOException("Benutzer-XML konnte nicht gespeichert werden.", ex);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static Element findUser(Document doc, String username) {
        if (username == null) {
            return null;
        }
        NodeList users = doc.getDocumentElement().getElementsByTagName("user");
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            if (getValue(user, "name").equalsIgnoreCase(username.trim())) {
                return user;
            }
        }
        return null;
    }

    private static String getValue(Element parent, String elementName) {
        NodeList nodes = parent.getElementsByTagName(elementName);
        return nodes.getLength() == 0 ? "" : nodes.item(0).getTextContent().trim();
    }

    private static void addElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value);
        parent.appendChild(element);
    }

    private static void removeBlankTextNodes(Node parent) {
        for (Node child = parent.getFirstChild(); child != null; ) {
            Node next = child.getNextSibling();
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().isBlank()) {
                parent.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeBlankTextNodes(child);
            }
            child = next;
        }
    }
}
