/*
 * Copyright (C) 2017 phramusca ( https://github.com/phramusca/JaMuz/ )
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rommanager.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class XML {
	
	public static Document open(String filename) {
		try {
			File file = new File(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			LogManager.getInstance().error(XML.class, "Error opening XML file: " + filename, ex);
			return null;
        }
	}

    public static Document newDoc() {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            return document;
        } catch (ParserConfigurationException ex) {
            LogManager.getInstance().error(XML.class, "Error creating new XML document", ex);
            return null;
        }
    }
    
    public static void save(String filename, Document document) {
        save(new File(filename), document);
    }
    
    public static void save(File filename, Document document) {
        try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
            //To indent with 4 spaces. Causes issues with tab indented read files
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(filename);
			// If you use
			// StreamResult result = new StreamResult(System.out);
			// the output will be pushed to the standard output ...
			// You can use that for debugging 
			transformer.transform(domSource, streamResult);
			System.out.println("Done creating XML File");
		} catch (TransformerException ex) {
			LogManager.getInstance().error(XML.class, "Error saving XML file: " + filename, ex);
		}
    }
    
    public static Element getElementByValue(String value) {
        XPath xPath = (XPath) XPathFactory.newInstance().newXPath();
//        String failure Expression = "//gameList/game";
//        Node node = (Node) xPath.compile(failureExpression).evaluate(doc,
//        XPathConstants.NODE);
        return null;
    }
    
    public static List<Element> evaluateXPath(Document document, String xpathExpression) {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        List<Element> values = new ArrayList<>();
        try {
            XPathExpression expr = xpath.compile(xpathExpression);
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                values.add((Element)nodes.item(i));
            }
        } catch (XPathExpressionException ex) {
            LogManager.getInstance().error(XML.class, "Error evaluating XPath: " + xpathExpression, ex);
        }
        return values;
    }
    
	public static String getNodeValue(Document doc, String TagNameLev1, String TagNameLev2) {
		NodeList nodeLst = doc.getElementsByTagName(TagNameLev1);
		Node fstNode = nodeLst.item(0);
		Element myElement = (Element) fstNode;
		NodeList myElementList = myElement.getElementsByTagName(TagNameLev2);
		Element mySubElement = (Element) myElementList.item(0);
		return getElementValue(mySubElement);
	}

	public static ArrayList<Element> getElements(Document doc, String tagName) {
		ArrayList<Element> elements=new ArrayList<>();
		NodeList nodeList = doc.getElementsByTagName(tagName);
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			elements.add((Element) node);
		}
		return elements;
	}
	
	public static Element getElement(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);
		Node node = nodeList.item(0);
		return (Element) node;
	}
	
	public static String getElementValue(Element element, String tagName) {
		return getElementValue(getElement(element, tagName));
	}
	
	public static String getAttribute(Element element, String attribute) {
		return element.getAttribute(attribute);
	}
	
	public static String getElementValue(Element element) {
		if(element==null) {
			return "";
		}
		NodeList mySubElementList = element.getChildNodes();
		Node node = (Node) mySubElementList.item(0);
		if(node==null) {
			return "";
		}
		return node.getNodeValue();
	}
}
