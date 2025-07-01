


package com.comerzzia.pos.core.gui.components.buttonsgroup;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import javafx.scene.input.KeyCodeCombination;


@XmlRootElement(name="Button")
@XmlAccessorType(XmlAccessType.FIELD)
public class ButtonConfigurationBean {
	
	@XmlTransient
	protected static Logger log = Logger.getLogger(ButtonConfigurationBean.class);
    
	public static final String TYPE_ACTION = "ACCION";
	public static final String TYPE_OPERATION = "OPERACION";
	public static final String TYPE_METHOD = "METODO";
	public static final String TYPE_ITEM = "ITEM";
	public static final String TYPE_EMPTY = "HUECO";
	public static final String TYPE_EMPTY_N = "HUECO_N";
	public static final String TYPE_PAYMENT = "PAGO";
	public static final String TYPE_LABEL = "LABEL";
	
	protected String text;
	protected String type;
	protected String key;
	protected String shortcut; // Tecla a pulsar para ejecutar la acción
	protected String imagePath;
	protected String visible;
	protected String cssClass;
	protected String wrapText;
    
    Map<String,String> params;   // Mapa de parámetros de la acción
    @XmlTransient
    protected KeyCodeCombination keyCodeCombination;
    
    /**
     * Constructor vacío
     */
    public ButtonConfigurationBean() {
    }
    
    /**
     *  Constructor de acción para una botonera
     * @param imagen
     * @param texto
     * @param teclaAccesoRapido
     * @param clave
     * @param tipoAccion 
     */
    public ButtonConfigurationBean(String rutaImagen, String texto, String teclaAccesoRapido, String clave, String tipo) {
    	params  = new HashMap<>();
    	this.imagePath = rutaImagen;
    	this.text = texto;
    	this.shortcut = teclaAccesoRapido;
    	this.key = clave;
    	this.type = tipo;
    }
    
    /**
     * @return  texto
     */
    public String getText() {
        return text;
    }

    /**
     * @param texto  texto a establecer
     */
    public void setText(String texto) {
        this.text = texto;
    }

    /**
     * @return  teclaAccesoRapido
     */
    public String getShortcut() {
        return shortcut;
    }

    /**
     * @param teclaAccesoRapido  teclaAccesoRapido a establecer
     */
    public void setShortcut(String teclaAccesoRapido) {
        this.shortcut = teclaAccesoRapido;
    }
    
    /**
     * @return  nombreAcción
     */
    public String getKey() {
        return key;
    }

    /**
     * @param clave  clave a establecer
     */
    public void setKey(String clave) {
        this.key = clave;
    }

    /**
     * @return  tipoAccion
     */
    public String getType() {
        return type;
    }

    /**
     * @param tipo  tipo a establecer
     */
    public void setType(String tipo) {
        this.type = tipo;
    }
    
    /**
     * introduce en el mapa de parametros de la acción un par clave/valor
     * @param key
     * @param valor
     */
    public void setParam(String key, String valor){
        params.put(key,valor);
    }
    
    /**
     *  Recupera de un mapa de parámetros una acción con clave key
     * @param key
     * @return 
     */
    public String getParam(String key){
        return params.get(key);
    }

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String rutaImagen) {
		this.imagePath = rutaImagen;
	}

	public KeyCodeCombination getKeyCodeCombination() {
		if (keyCodeCombination == null && getShortcut() != null && !getShortcut().isEmpty()) {
			try {
				keyCodeCombination = new KeyCodeCombinationParser().parse(getShortcut());
			} catch (Exception e) {
				shortcut = null; //Pongo a null para que no intente crear el keyCodeCombination más veces
				log.error("handleKeyEvent() - Error al crear el KeyCodeCombination a partir de la cadena: " + getShortcut(), e);
			}
		}
		return keyCodeCombination;
	}

	public Boolean isVisible() {
		return visible == null || visible.equals("S") || visible.equals("Y") || Boolean.valueOf(visible);
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}
	
	public void setVisible(Boolean visible) {
		this.visible = visible!=null?visible.toString():"false";
	}
	
    public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public boolean isWrapText() {
		return wrapText == null || wrapText.equals("S") || wrapText.equals("Y") || Boolean.valueOf(wrapText);
	}

	public void setWrapText(String wrapText) {
		this.wrapText = wrapText;
	}

	public void setWrapText(Boolean wrapText) {
		this.wrapText = wrapText!=null?wrapText.toString():"false";;
	}

	public Map<String, String> getParams() {
        return params;
    }

	@Override
	public String toString() {
		return "ButtonConfigurationBean [text=" + text + ", type=" + type + ", key=" + key + ", shortcut=" + shortcut
				+ ", imagePath=" + imagePath + ", visible=" + visible + ", params=" + params + ", keyCodeCombination="
				+ keyCodeCombination + "]";
	}

}
