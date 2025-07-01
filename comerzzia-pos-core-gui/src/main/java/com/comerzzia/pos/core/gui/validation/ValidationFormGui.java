

package com.comerzzia.pos.core.gui.validation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Path;

import org.apache.log4j.Logger;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.TextField;


@Component
@Scope("prototype")
public abstract class ValidationFormGui {

    // Logger
    private static final Logger log = Logger.getLogger(ValidationFormGui.class.getName());
    // Mapa de asociaciones entre campos y el formulario
    private Map<String, Node> formFields;
    
    private Path lastPath;
    
    /**
     * Constructor por defecto. Las clases heredadas deberán hacer super()
     */
    public ValidationFormGui() {
        formFields = new HashMap<>();
    }

    /**
     * Limpia el formulario con los valores por defecto
     */
    public abstract void clearForm();

    /**
     * Asigna a un nombre de campo un contenedor de UI
     *
     * @param field nombre del campo que enlazamos con el componente
     * @param container contenedor en pantalla de la propiedad
     */
    public void setFormField(String field, Node container) {
        log.trace("setFormField() ");
        lastPath = PathImpl.createPathFromString(field);
        formFields.put(lastPath.toString(), container);
    }

    /**
     * Método estático que obtiene el nombre de un campo en función de su getter
     * o setter
     *
     * @param method
     * @return
     */
    public static String getFieldName(Method method) {
        log.debug("getFieldName() - " + method);
        try {
            Class<?> clase = method.getDeclaringClass();
            BeanInfo info = Introspector.getBeanInfo(clase);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod())) {
                    log.debug("getFieldName() - DN: " + pd.getDisplayName());
                    return pd.getName();
                }
            }
        } catch (IntrospectionException e) {
            log.error("getFieldName() - IntrospectionException / " + method + " - mensaje: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("getFieldName() - " + method + " - mensaje: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Elimina el enlace con los elementos del formulario.
     */
    public void deleteForm() {
        log.debug("deleteForm() ");
        formFields.clear();
    }

    /**
     * Establece un estilo para un campo que contiene un error identificado por el Path
     * @param rutaComponente
     * @param enError 
     */
    public void setErrorStyle(Path rutaComponente, boolean enError) {
        Node campo = formFields.get(rutaComponente.toString());
        if (campo != null) {            
            setErrorStyle(campo, enError);            
        } else {
            log.debug("setEstiloError() Ruta: " + rutaComponente.toString() + " no tiene componente en pantalla asociado");
        }
    }
    
    /**
     * Función auxiliar que establece o quita el estilo de error al campo
     * @param campo
     * @param modoError 
     */
    private void setErrorStyle(Node campo, boolean modoError) {
         if (modoError && !campo.getStyleClass().contains("error-formulario")) {
                campo.getStyleClass().add("error-formulario");
         } else if (!modoError && campo.getStyleClass().contains("error-formulario")) {
                campo.getStyleClass().remove("error-formulario");
         }
    }
    
    /**
     * Elimina los estilos de error del formulario
     */
    public void clearErrorStyle(){
         for (Node container : formFields.values() ){
        	 if(container != null) {
        		 setErrorStyle(container, false);
        	 }
         }   
    }
        
    /**
     * Establece el foco en un campo identificado por el Path
     * @param rutaComponente
     */
    public void setFocus(Path rutaComponente) {        
        if (formFields.containsKey(rutaComponente.toString())){
            Node campo = formFields.get(rutaComponente.toString());
            
            if (campo != null) {
                   campo.requestFocus();            
            } else {
                log.debug("setFocus() Ruta: " + rutaComponente.toString() + " no tiene componente en pantalla asociado");
            }
        }
    }
    
    /**
     * Función auxiliar que elimina los espacios en blanco al inicio y al final del campo
     * 
     * @param tf
     * @return
     */
    public String trimTextField(TextField tf){
    	String tfStringTrim = null;
    	if(tf !=null && tf.getText() != null){
    		tfStringTrim = tf.getText().trim();
    		tf.setText(tfStringTrim);
    	}
    	return tfStringTrim;
    }
    
}
