package com.comerzzia.pos.core.gui.about;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.hashcontrolled.HashControlledDevice;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;
import com.comerzzia.pos.core.devices.device.scale.DeviceScaleDummy;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.version.SoftwareVersion;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

@Component
@Lazy(value = true)
@CzzScene
public class AboutController extends SceneController implements Initializable {
    
     @FXML
     protected Label lbVersion; // etiqueta de version en ventana acerca de
     @FXML
     protected Label lbLegalDisclaimer, lbLegalDisclaimer2; // etiqueta de aviso legal en ventana acerca de
     @FXML
     protected Label lbUrl; // etiqueta de url en ventana acerca de
     @FXML
     protected VBox vbInstallationData;
     
     
     @Autowired
     protected Session sesion;
     
     /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

	@Override
    public void onSceneOpen() {
		
    }
	
    @Override
    public void initializeComponents() {
    	lbLegalDisclaimer.setText(SoftwareVersion.get().toString());
		
		String year = new SimpleDateFormat("yyyy").format(new Date());
    	lbLegalDisclaimer2.setText(I18N.getText("2008-{0} Comerzzia S.L. Todos los derechos reservados. comerzzia es una marca registrada de Comerzzia S.L.", year));
    	
    	
    	String labelText = "";
    	DeviceScale scale = Devices.getInstance().getScale();
    	if(scale instanceof HashControlledDevice) {
			HashControlledDevice hashScale = (HashControlledDevice) scale;
			String libraryHash = hashScale.getLibraryHash();
			
			if(!libraryHash.isEmpty()) {
				vbInstallationData.getChildren().add(new Label(I18N.getText("Hash librería de balanza: ")+libraryHash));
			}
		}
    	
    	if(!(scale instanceof DeviceScaleDummy)) {
    		vbInstallationData.getChildren().add(new Label(I18N.getText("Nº Cert: ")+scale.getCertNumber()));
    		vbInstallationData.getChildren().add(new Label(I18N.getText("Nombre fabricante: ")+"Comerzzia"));
    		vbInstallationData.getChildren().add(new Label(I18N.getText("Tipo de designación: ")+"Comerzzia Enterprise Suite Module czz-POS"));
    	}
    	
    	for(Entry<String, String> entryModule:SoftwareVersion.get().getModules().entrySet()) {
    		labelText = labelText.concat(entryModule.getKey()+": "+entryModule.getValue()+"\n");
    	}
    	
    	Label lbRepositoryRevision = new Label("");
    	if(SoftwareVersion.get().getVersionRevision()!=null) {
    		lbRepositoryRevision.setText(I18N.getText("Revisión software: ")+SoftwareVersion.get().getVersionRevision());
    	}
    	vbInstallationData.getChildren().add(lbRepositoryRevision);
    	vbInstallationData.getChildren().add(new Label(I18N.getText("Versión software:")));
    	TextArea textArea = new TextArea(labelText);
    	textArea.setEditable(false);
    	vbInstallationData.getChildren().add(textArea);
    }

	@Override
	protected void initializeFocus() {
	}
    
}
