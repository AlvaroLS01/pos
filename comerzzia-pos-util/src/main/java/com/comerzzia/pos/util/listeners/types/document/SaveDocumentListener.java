package com.comerzzia.pos.util.listeners.types.document;

import com.comerzzia.pos.util.listeners.POSListener;

public interface SaveDocumentListener extends POSListener {

	public void executeBeforeSave(Object[] args) throws Exception;

	public void executeAfterSave(Object[] args) throws Exception;

	public void executeAfterCommit(Object[] args) throws Exception;

}
