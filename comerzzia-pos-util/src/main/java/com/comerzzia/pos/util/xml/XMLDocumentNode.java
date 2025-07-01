package com.comerzzia.pos.util.xml;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLDocumentNode {

	private static Logger log = Logger.getLogger(XMLDocumentNode.class);
	
	private Node node;
	private Document document;
	
	public XMLDocumentNode(XMLDocument document, String name){
		this.node = document.getDocument().createElement(name);
		this.document = document.getDocument();
	}

	public XMLDocumentNode(XMLDocument document, String name, String value){
		this.node = document.getDocument().createElement(name);
		this.document = document.getDocument();
		this.node.setTextContent(value);
	}

	protected XMLDocumentNode(Document document, Node node){
		this.node = node;
		this.document = document;
	}
	
	/** Returns new node imported from other node of a different XML document.
	 * @param document - Current XML document where the node will be imported
	 * @param sourceNode - Source node
	 * @return XMLDocumentNode - New node
	 */
	public static XMLDocumentNode importNode(XMLDocument document, Node sourceNode){
		Node node = document.getDocument().importNode(sourceNode, true);
		return new XMLDocumentNode(document.getDocument(), node);
	}

	/** Add CDATA section to node.*/
	public void addCDataSection(String cdata){
		CDATASection cdataSection = this.document.createCDATASection(cdata);
		this.node.appendChild(cdataSection);
	}

	
	/** Add child node to node. */
	public void addChild(XMLDocumentNode node){
		this.node.appendChild(node.getNode());
	}

	/** Add child node to node with the specified name and value. */
	public void addChild(String name, String value){
		Node node = document.createElement(name);
		node.setTextContent(value);
		this.node.appendChild(node);
	}

	/** Add attribute to node with the specified name and value. */
	public void addAttribute(String name, String value){
		Attr a = document.createAttribute(name);
		a.setNodeValue(value);
		node.getAttributes().setNamedItem(a);
	}

	/** Returns object Node from node. */
	public Node getNode(){
		return this.node;
	}
	
	/** 
	 * Search node by name in current node whole tree.
	 * This method is like {@link #getNode(String, boolean) getNode} with optional equals to false.
	 * @param name - Name of the searched node
	 * @return XMLDocumentNode - Searched node
	 * @throws XMLDocumentNodeNotFoundException If node is not found
	 */
	public XMLDocumentNode getNode(String name) throws XMLDocumentNodeNotFoundException{
        return getNode(name, false);
	}

	/** 
	 * Search node by name in current node whole tree.
	 * @param name - Name of the searched node
	 * @param optional - If the node is optional or not
	 * @return XMLDocumentNode - Searched node. Null if it is not found and is optional
	 * @throws XMLDocumentNodeNotFoundException If node is not found and is not optional
	 */
	public XMLDocumentNode getNode(String name, boolean optional) throws XMLDocumentNodeNotFoundException{
        Node node = null;
        for (node = this.node.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) node).getTagName().equals(name)) {
                    return new XMLDocumentNode(document,node);
                }
            }
        }
        if (!optional) {
            throw new XMLDocumentNodeNotFoundException("The node " + this.node.getNodeName() + " does not contain the node " + name);
        }
        return null;
	}

	/** 
	 * Returns node children list.
	 * @return List<XMLDocumentNode> - Children list of node
	 */
	public List<XMLDocumentNode> getChildren(){
		NodeList nodeList = node.getChildNodes();
		List<XMLDocumentNode> xmlNodeList = new ArrayList<XMLDocumentNode>();
		for (int i=0; i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			if (node instanceof Element){
				xmlNodeList.add(new XMLDocumentNode(document, node));
			}
		}
		return xmlNodeList;
	}
	
	public boolean hasChildren(){
		NodeList nodeList = node.getChildNodes();
		for (int i=0; i<nodeList.getLength();i++){
			Node nodoHijo = nodeList.item(i);
			if (nodoHijo.getNodeType()  == Node.ELEMENT_NODE){
				return true;
			}
		}
		return false;
	}

	/** 
	 * Returns node children list with a specified name.
	 * @return List<XMLDocumentNode> - Children list of node
	 */
	public List<XMLDocumentNode> getChildren(String name){
		NodeList nodeList = node.getChildNodes();
		List<XMLDocumentNode> xmlNodeList = new ArrayList<XMLDocumentNode>();
		for (int i=0; i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			if (node instanceof Element){
                if (((Element) node).getTagName().equals(name)) {
                	xmlNodeList.add(new XMLDocumentNode(document, node));
                }
			}
		}
		return xmlNodeList;
	}
	
	public String getAttributeValue(String attribute, boolean optional) throws XMLDocumentNodeNotFoundException{
        String value = ((Element)node).getAttribute(attribute);
        if ((value == null || value.isEmpty()) && !optional) {
            throw new XMLDocumentNodeNotFoundException("The node " + this.node.getNodeName() + " does not contain de attribute " + attribute);
        }
        return value;
	}
	public Long getAttributeValueAsLong(String attribute, boolean optional) throws XMLDocumentNodeNotFoundException{
        return Long.parseLong(getAttributeValue(attribute, optional));
	}
	public Integer getAttributeValueAsInteger(String attribute, boolean optional) throws XMLDocumentNodeNotFoundException{
        return Integer.parseInt(getAttributeValue(attribute, optional));
	}
	public Double getAttributeValueAsDouble(String attribute, boolean optional) throws XMLDocumentNodeNotFoundException{
        return Double.parseDouble(getAttributeValue(attribute, optional));
	}
	public BigDecimal getAttributeValueAsBigDecimal(String attribute, boolean optional) throws XMLDocumentNodeNotFoundException{
        return new BigDecimal(getAttributeValueAsDouble(attribute, optional));
	}
	public Date getAttributeValueAsDate(String attribute, boolean optional) throws XMLDocumentNodeNotFoundException{
        String fecha = getAttributeValue(attribute, optional);
        return getDate(fecha);
	}

	private Date getDate(String date) {
		if (date != null && date.length()> 0){
	        try {
	            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
	            df.setLenient(false);
	            return df.parse(date);
	        }
	        catch (ParseException e) {
	        }
        }
        return null;
	}
	public boolean getAttributeValueAsBoolean(String attribute, boolean optional) throws XMLDocumentNodeNotFoundException{
        return Boolean.valueOf(getAttributeValue(attribute, optional));
	}
	
	/** 
	 * The name of the node.
	 * @return String
	 */
	public String getName(){
		return this.node.getNodeName();
	}
	
	/** 
	 * The value of the node as string.
	 * @return String
	 */
	public String getValue(){
		return this.node.getTextContent();
	}

	/** 
	 * The value of the node as Long.
	 * @return Long
	 */
	public Long getValueAsLong(){
		return Long.parseLong(this.node.getTextContent());
	}
	
	/** 
	 * The value of the node as Integer.
	 * @return Integer
	 */
	public Integer getValueAsInteger(){
		return Integer.parseInt(this.node.getTextContent());
	}
	
	/** 
	 * The value of the node as Double.
	 * @return Double
	 */
	public Double getValueAsDouble(){
		return Double.parseDouble(this.node.getTextContent());
	}

	/** 
	 * The value of the node as BigDecimal.
	 * @return Double
	 */
	public BigDecimal getValueAsBigDecimal(){
		return new BigDecimal(getValueAsDouble());
	}

	/**
	 * The value of the node as Date or null.
	 * @return Double
	 */
	public Date getValueAsFecha(){
		return getDate(getValue());
	}
	
	/** 
	 * The value of the node as Boolean.
	 * @return Boolean
	 */
	public boolean getValueAsBoolean(){
		return Boolean.valueOf(this.node.getTextContent());
	}
	
	/** 
	 * Set node value with the one specified
	 * @param value String
	 */
	public void setValue(String value){
		if (value != null){
			this.node.setTextContent(value);
		}
	}

	/** 
	 * Set node value with the one specified.
	 * @param value Long
	 */
	public void setValue(Long value){
		if (value != null){
			this.node.setTextContent(value.toString());
		}
	}

	/** 
	 * Set node value with the one specified.
	 * @param value Integer
	 */
	public void setValue(Integer value){
		if (value != null){
			this.node.setTextContent(value.toString());
		}
	}

	/** 
	 * Set node value with the one specified.
	 * @param value Double
	 */
	public void setValue(Double value){
		if (value != null){
			this.node.setTextContent(value.toString());
		}
	}

	/** 
	 * Set node value with the one specified.
	 * @param value BigDecimal
	 */
	public void setValue(BigDecimal value){
		if (value != null){
			this.node.setTextContent(value.toString());
		}
	}

	/** 
	 * Set node value with the one specified.
	 * @param value Boolean
	 */
	public void setValue(Boolean value){
		if (value != null){
			this.node.setTextContent(value.toString());
		}
	}
	
	/** Object overloaded toString method. If there is any error during serialization, ## ERROR ## is returned. */
	@Override
	public String toString() {
		try {
			StringWriter sw = new StringWriter();
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
			return sw.toString();
		}
		catch(TransformerException e) {
			log.error("toString() - " + e.getMessage());
			return "## ERROR ##";
		}
	}

	/** Remove node attribute by name. */
	public void removeAttribute(String name){
		node.getAttributes().removeNamedItem(name);
	}
}
