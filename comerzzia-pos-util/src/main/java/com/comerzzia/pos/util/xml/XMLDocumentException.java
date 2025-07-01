
package com.comerzzia.pos.util.xml;


public class XMLDocumentException extends Exception {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -5086664177325890173L;

	/** 
     * Default constructor without message error
     */
    public XMLDocumentException() {
    }
        
    /**
     * Constructor with message error
     * @param msg Message error
     */
    public XMLDocumentException(String msg) {
        super(msg);
    }
    
    /**
     * Constructor with message error and cause
     * @param msg Message error
     * @param e Source cause of the error
     */
    public XMLDocumentException(String msg, Throwable e) {
        super(msg);
        initCause(e);
    }
}
