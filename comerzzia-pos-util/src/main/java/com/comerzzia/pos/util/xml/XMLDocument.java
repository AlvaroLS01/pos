package com.comerzzia.pos.util.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLDocument {
	
	private static Logger log = Logger.getLogger(XMLDocument.class);

	/**
	 * DocumentBuilder Cache
	 */
	protected static InheritableThreadLocal builderThreadLocal = new InheritableThreadLocal() {
		@Override
		protected Object initialValue() {
			try {
				return createDocumentBuilder();
			} catch (XMLDocumentException e) {
				throw new RuntimeException(e);
			}
		}
	};

	private Document document;
	private Exception exception;
	private XMLDocumentNode root;

	/** Constructor with empty document. */
	public XMLDocument() {
        try {
			this.document = getDocumentBuilder().newDocument();
		} catch (XMLDocumentException e) {
			exception = e;
		}
	}

	/** Constructor from specified document. */
	public XMLDocument(Document document){
		this.document = document;
	}

	/** Constructor from URL content. */
	public XMLDocument(URL url) throws XMLDocumentException{
		try{
        	log.trace("XMLDocument() - Obteniendo xml a partir de URL: " + url.toString());
        	InputSource is = new InputSource(url.openStream());
			this.document = getDocumentBuilder().parse(is);
        }
		catch(XMLDocumentException e){
			throw e;
		}
        catch(Exception e){
        	String msg = "Error obteniendo xml a partir de la url indicada: " + e.getMessage();
			log.error("XMLDocument() - " + msg);
			throw new XMLDocumentException(msg, e);
        }
	}

	/** Constructor from file content. */
	public XMLDocument(File file) throws XMLDocumentException{
		try{
        	log.trace("XMLDocument() - Obteniendo xml a partir de File: " + file.getAbsolutePath());
    		this.document = getDocumentBuilder().parse(file);
        }
		catch(XMLDocumentException e){
			throw e;
		}
        catch(Exception e){
        	String msg = "Error obteniendo xml a partir del fichero indicado: " + e.getMessage();
			log.error("XMLDocument() - " + msg);
			throw new XMLDocumentException(msg, e);
        }
	}

	/** Constructor from byte array. */
	public XMLDocument(byte[] bytes) throws XMLDocumentException{
    	log.trace("XMLDocument() - Obteniendo xml a partir de array de bytes.");
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		getXMLDocument(is);
	}

	/** Constructor from InputStream. */
	public XMLDocument(InputStream is) throws XMLDocumentException{
		getXMLDocument(is);
	}
	
	private void getXMLDocument(InputStream is) throws XMLDocumentException{
		try{
	    	log.trace("XMLDocument() - Obteniendo xml a partir de InputStream.");
    		this.document = getDocumentBuilder().parse(is);
        }
		catch(XMLDocumentException e){
			throw e;
		}
        catch(Exception e){
        	String msg = "Error obteniendo xml a partir de array de bytes: " + e.getMessage();
			log.error("XMLDocument() - " + msg);
			throw new XMLDocumentException(msg, e);
        }
	}

	/** 
	 * Set XML document root node. 
	 * @param node - Root node.
	 */
	public void setRoot(XMLDocumentNode node){
		document.appendChild(node.getNode());
		this.root = new XMLDocumentNode(document,document.getDocumentElement());
		
	}

	/**
	 * XML document root node.
	 */
	public XMLDocumentNode getRoot(){
		if(root == null) {
			this.root = new XMLDocumentNode(document,document.getDocumentElement());
		}
		return root;
	}
	
	/** 
	 * Search node by name in root node whole tree.
	 * This method is like {@link #getNode(String, boolean) getNode} with optional equals to false.
	 * @param name - Name of the searched node
	 * @return XMLDocumentNode - Searched node
	 * @throws XMLDocumentNodeNotFoundException If node is not found
	 */
	public XMLDocumentNode getNode(String name) throws XMLDocumentNodeNotFoundException{
        return getNode(name, false);
	}

	
	/** 
	 * Search node by name in root node whole tree.
	 * @param name - Name of the searched node
	 * @param optional - If the node is optional or not
	 * @return XMLDocumentNode - Searched node. Null if it is not found and is optional
	 * @throws XMLDocumentNodeNotFoundException If node is not found and is not optional
	 */
	public XMLDocumentNode getNode(String name, boolean optional) throws XMLDocumentNodeNotFoundException{
        return getRoot().getNode(name, optional);
	}
	

	/** 
	 * Obtain XML nodes list with the specified name.
	 * @param name - Tag name of the searched nodes
	 * @return List<XMLDocumentNode> - Nodes list
	 */
	public List<XMLDocumentNode> getNodes(String name){
		NodeList nodeList = document.getElementsByTagName(name);
		List<XMLDocumentNode> xmlNodeList = new ArrayList<XMLDocumentNode>();
		for (int i=0; i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			if (node instanceof Element){
				xmlNodeList.add(new XMLDocumentNode(document, nodeList.item(i)));
			}
		}
		return xmlNodeList;
	}
	
	/** 
	 * Obtain root node children list.
	 * @return List<XMLDocumentNode> - Children list of root node
	 */
	public List<XMLDocumentNode> getChildren(){
		NodeList nodeList = document.getChildNodes();
		List<XMLDocumentNode> xmlNodeList = new ArrayList<XMLDocumentNode>();
		for (int i=0; i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			if (node instanceof Element){
				xmlNodeList.add(new XMLDocumentNode(document, nodeList.item(i)));
			}
		}
		return xmlNodeList;
	}


	/** Document object of XML.*/
	public Document getDocument(){
		return this.document;
	}
	

	/** 
	 * Returns byte array with the XML content using specified encoding.
     * @param encoding - If it is null, no one will be used
     * @return byte[] - XML content serialization
     * @throws XMLDocumentTransformerException
     */
	public byte[] getBytes(String encoding) throws XMLDocumentTransformerException{
		return getString(encoding).getBytes();
    }
	
	public byte[] getBytes() throws XMLDocumentTransformerException {
		return getString().getBytes();
	}
	
	/** 
	 * Returns string with the XML content. Like {@link #getString(String) getString} with null encoding.
     * @return String - String with XML content
	 * @throws XMLDocumentTransformerException If any error happens during serialization
     */
	public String getString() throws XMLDocumentTransformerException{
		try {
			return XMLDocumentUtils.DocumentToString(document);
		} catch (XMLDocumentException e) {
			throw new XMLDocumentTransformerException(e);
		}
	}

	/** 
	 * Returns string with the XML content using specified encoding.
     * @param encoding - If it is null, no one will be used
     * @return String - XML content serialization
     * @throws XMLDocumentTransformerException If any error happens during serialization
     */
	public String getString(String encoding) throws XMLDocumentTransformerException{
		try {
			return XMLDocumentUtils.DocumentToString(document, encoding);
		} catch (XMLDocumentException e) {
			throw new XMLDocumentTransformerException(e);
		}
	}
	
	/**
	 * Returns string with the XML content using specified encoding and optional indentation.
	 * 
	 * @param encoding - If it is null, no one will be used
	 * @param indent - If it should be indented or not
	 * @return String - XML content serialization
	 * @throws XMLDocumentTransformerException If any error happens during serialization
	 */
	public String getString(String encoding, boolean indent) throws XMLDocumentTransformerException{
		try {
			return XMLDocumentUtils.DocumentToString(document, encoding, indent);
		} catch (XMLDocumentException e) {
			throw new XMLDocumentTransformerException(e);
		}
	}
	
	public void toFile(File file, String encoding, boolean indent) throws XMLDocumentTransformerException{
		FileOutputStream fileOutput = null;
		try{
			fileOutput = new FileOutputStream(file);
			Result result = new StreamResult(fileOutput);
			DOMSource source = new DOMSource(document);
			TransformerFactory tFactory = TransformerFactory.newInstance();
//			tFactory.setAttribute("indent-number", 2);
			Transformer transformer = tFactory.newTransformer();
			if (encoding != null) {
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			}
			if (indent){
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			}
			transformer.transform(source, result);
		}
		catch(Exception e){
			String msg = "Error applying XML converstion for dumping to file: " + e.getMessage();
			log.error("toFile() - " + msg);
			throw new XMLDocumentTransformerException(msg);
		}
		finally{
			IOUtils.closeQuietly(fileOutput);
		}
	}
	
	/** Object overloaded toString method. If there is any error during serialization, ## ERROR ## is returned. */
	@Override
	public String toString(){
		try {
			return getString("UTF-8");
		} catch (XMLDocumentTransformerException e) {
			log.error("toString() - " + e.getMessage());
			return "## ERROR ##";
		}
	}

	private static DocumentBuilder createDocumentBuilder() throws XMLDocumentException {
        try {
        	log.debug("createDocumentBuilder() - Creando nuevo DocumentBuilder");
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder;
        } 
        catch (Exception e) {
        	String msg = "Error creating DocumentBuilder: " + e.getMessage();
			log.error("getDocumentBuilder() - " + msg);
			throw new XMLDocumentException(msg, e);
        }
    }

	private static DocumentBuilder getDocumentBuilder() throws XMLDocumentException {
        try {
            return (DocumentBuilder) builderThreadLocal.get();
        }
        catch (Exception e) {
        	String msg = "Error creating DocumentBuilder: " + e.getMessage();
			log.error("getDocumentBuilder() - " + msg);
			throw new XMLDocumentException(msg, e);
        }
    }

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public void setDocument(String string) {
		try {
			document = XMLDocumentUtils.createDocumentFromString(string);
		} 
		catch (Exception e) {
			exception = e;
		} 
	}

	public void setDocument(Blob blob) {
		try {
			document = XMLDocumentUtils.createDocumentBuilder().parse(blob.getBinaryStream());
		} 
		catch (Exception e) {
			exception = e;
		} 
	}
	

	public Exception getException() {
		return exception;
	}

}
