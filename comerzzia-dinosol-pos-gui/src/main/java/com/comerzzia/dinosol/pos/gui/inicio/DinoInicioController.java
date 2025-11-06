package com.comerzzia.dinosol.pos.gui.inicio;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.inicio.InicioController;
import com.comerzzia.pos.services.core.sesion.Sesion;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Primary
@Component
public class DinoInicioController extends InicioController{

	@Autowired 
	private Sesion sesion;
    @FXML
    private ImageView imageLogo;
    
    private Logger log = Logger.getLogger(DinoInicioController.class);
    
    @Override
	public void initialize(URL url, ResourceBundle rb) {

		String imgBase = "/skins/dinosol/com/comerzzia/pos/gui/inicio/logo_dinosol_";
		// Id tratamiento impuetos canarias
		try {
			if (new Long(10001).equals(sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos())) {
				imgBase += "canarias.png";
			}
			else {
				imgBase += "baleares.png";
			}
		} catch (Throwable e) {
			//Se carga el de baleares porque no tiene ninguna referencia a la provincia en el logo
			log.error("initialize() - Se ha producido un error cargando el log, se cargara el de baleares por defecto: "+e.getMessage(),e);
			imgBase = "/skins/dinosol/com/comerzzia/pos/gui/inicio/logo_dinosol_baleares.png";
		}
		Image newImage = new Image(imgBase);
		imageLogo.setImage(newImage);

	}    
    
    
}
