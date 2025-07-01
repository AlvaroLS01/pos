package com.comerzzia.pos.core.gui.exception;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.comerzzia.pos.core.services.config.ConfigurationException;
import com.comerzzia.pos.util.config.AppConfig;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class PreinitializationExceptionController implements Initializable {

	protected Stage stage;
	protected Throwable exception;

	@FXML
	protected Label lbMessage, lbTitle;
	@FXML
	protected TextArea taException;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	public void closeCancel() {
		stage.close();
	}
	
	public void copyTrace() {
		StringSelection stringSelection = new StringSelection(buildExceptionData());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	public void setException(Throwable exception) {
		this.exception = exception;
		taException.setText(ExceptionUtils.getStackTrace(exception));
		lbMessage.setText(getUserfriendlyMessage(exception));
	}

	protected String getUserfriendlyMessage(Throwable e) {
		String message = "Ha ocurrido un error al inicializar el POS.";
		Throwable rootCause = ExceptionUtils.getRootCause(e);
		if (rootCause != null && rootCause instanceof ConfigurationException) {
			message = ExceptionUtils.getRootCause(e).getMessage();
		} else if (e instanceof BeanCreationException) {
			BeanCreationException bie = (BeanCreationException) e;
			if ("emf".equals(bie.getBeanName())) {
				message = "Ha ocurrido un error al inicializar el sistema de persistencia. Revise la configuración de base de datos.";
			}else if("mainStageManager".equals(bie.getBeanName())) {
				message = "Ha ocurrido un error al inicializar la ventana principal. Revise la configuración de spring.";
				try {
					if(AppConfig.getCurrentConfiguration().getDeveloperMode()) {
						message += "\nLos recursos de inicialización pueden no haber sido generados correctamente durante la compilación.";
					}
					
				} catch (Throwable ex) {}
			}
		}
		return message;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public String buildExceptionData() {
		StringBuilder sb = new StringBuilder();
		sb.append("Software information: \n");
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			Resource[] resources = resolver.getResources("classpath*:/META-INF/MANIFEST.MF");
			if (resources != null) {
				for (Resource resource : resources) {
					if (resource == null) {
						continue;
					}
					
					  URL url = resource.getURL();
		                InputStream is = url.openStream();
		                
		                if (is != null) {
		                	Manifest manifest = new Manifest(is);
		                    Attributes mainAttribs = manifest.getMainAttributes();	      
		                    String module = mainAttribs.getValue("comerzzia-module");
		                    String moduleVersion = mainAttribs.getValue("comerzzia-module-version");
		                    
		                    if (StringUtils.isNotEmpty(module) && StringUtils.isNotEmpty(moduleVersion)) {
		                    	sb.append("\t").append(module).append(": ").append(moduleVersion).append("\n");
		                    }
		                }
				}
			}
		} catch (IOException ignore) {
			sb.append("\t").append("Error getting software version").append("\n");
		}
        sb.append("Stack Trace: ").append(ExceptionUtils.getStackTrace(exception));
        
        return sb.toString();
	}
	
}
