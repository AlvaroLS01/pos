


package com.comerzzia.pos.core.gui.components.events;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Control;
import javafx.scene.input.KeyEvent;


public class KeyboardEventBean {

    public static String TYPE_FILTER = "FILTER";  // Tipo Filtro: De pantalla a elemento -->
    public static String TYPE_HANDLER = "HANDLER"; // Tipo Handler: De elemento a pantalla <--
    
    private EventHandler<KeyEvent> keyEventHandler;
    private EventType<KeyEvent> keyEventType;
    private Control control; //componente donde se registra el evento
    protected String type;

    
    /**
     * 
     * @param keyEventHandler
     * @param keyEventType 
     */
    public KeyboardEventBean(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType) {
        this.keyEventHandler = keyEventHandler;
        this.keyEventType = keyEventType;
    }
    
    
     /**
     * 
     * @param keyEventHandler
     * @param keyEventType 
     * @param control 
     * @param type 
     */
    public KeyboardEventBean(EventHandler<KeyEvent> keyEventHandler, EventType<KeyEvent> keyEventType, Control control, String type) {
        this.keyEventHandler = keyEventHandler;
        this.keyEventType = keyEventType;
        this.control = control;
        this.type = type;
    }

    /**
     * @return keyEventHandler
     */
    public EventHandler<KeyEvent> getKeyEventHandler() {
        return keyEventHandler;
    }

    /**
     * @param keyEventHandler keyEventHandler 
     */
    public void setKeyEventHandler(EventHandler<KeyEvent> keyEventHandler) {
        this.keyEventHandler = keyEventHandler;
    }

    /**
     * @return keyEventType
     */
    public EventType<KeyEvent> getKeyEventType() {
        return keyEventType;
    }

    /**
     * @param keyEventType keyEventType 
     */
    public void setKeyEventType(EventType<KeyEvent> keyEventType) {
        this.keyEventType = keyEventType;
    }
    
    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Función que elimina el evento. Para usarlo debemos de haber seteado el control
     */
    public void eliminaEvento() {
        if (type!=null && type.equals(TYPE_FILTER)){
            getControl().removeEventFilter(keyEventType,keyEventHandler);
        }
        else{
            getControl().removeEventHandler(keyEventType,keyEventHandler);
        }        
    }

    /**
     * Devuelve true si el evento es de tipo filtro.
     * @return 
     */
    public boolean isTypeFilter() {
        return (type!=null && type.equals(TYPE_FILTER));
    }


	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((keyEventHandler == null) ? 0 : keyEventHandler.hashCode());
	    result = prime * result + ((keyEventType == null) ? 0 : keyEventType.hashCode());
	    return result;
    }


	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    KeyboardEventBean other = (KeyboardEventBean) obj;
	    if (keyEventHandler == null) {
		    if (other.keyEventHandler != null)
			    return false;
	    }
	    else if (!keyEventHandler.equals(other.keyEventHandler))
		    return false;
	    if (keyEventType == null) {
		    if (other.keyEventType != null)
			    return false;
	    }
	    else if (!keyEventType.equals(other.keyEventType))
		    return false;
	    return true;
    }
    
    
}
