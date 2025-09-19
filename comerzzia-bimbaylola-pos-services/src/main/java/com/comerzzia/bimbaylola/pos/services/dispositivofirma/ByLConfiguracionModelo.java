package com.comerzzia.bimbaylola.pos.services.dispositivofirma;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;

public class ByLConfiguracionModelo {

	private static final String TAG_OPERACIONES = "comandos";
	private static final String ATT_NOMBRE = "nombre";
	private static final String ATT_VALOR = "valor";

	private XMLDocumentNode configConexion;
	private XMLDocumentNode configModelo; // Necesitamos almacenar toda la configuración del modelo. Para el caso en el
										  // que el modelo tiene configuradas operaciones
	private XMLDocumentNode configOperaciones;
	private String nombreConexion;
	private String tipoConexion;
	private String manejador;

	public ByLConfiguracionModelo(XMLDocumentNode configModelo, XMLDocumentNode configConexion, String nombreConexion, String tipoConexion, String manejador) {
		try {
			this.configModelo = configModelo;
			this.configConexion = configConexion;
			this.nombreConexion = nombreConexion;
			this.tipoConexion = tipoConexion;
			this.manejador = manejador;

			// Leemos las operaciones si el dispositivo las tiene
			configOperaciones = configModelo.getNodo(TAG_OPERACIONES, true);
		}
		catch (XMLDocumentNodeNotFoundException ex) {
			Logger.getLogger(ByLConfiguracionModelo.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public XMLDocumentNode getConfigConexion() {
		return configConexion;
	}

	public void setConfigConexion(XMLDocumentNode configConexion) {
		this.configConexion = configConexion;
	}

	public String getNombreConexion() {
		return nombreConexion;
	}

	public void setNombreConexion(String nombreConexion) {
		this.nombreConexion = nombreConexion;
	}

	public String getTipoConexion() {
		return tipoConexion;
	}

	public void setTipoConexion(String tipoConexion) {
		this.tipoConexion = tipoConexion;
	}

	public XMLDocumentNode getConfigModelo() {
		return configModelo;
	}

	public void setConfigModelo(XMLDocumentNode configModelo) {
		this.configModelo = configModelo;
	}

	public XMLDocumentNode getConfigOperaciones() {
		return configOperaciones;
	}

	public String getManejador() {
		return manejador;
	}

	public void setManejador(String manejador) {
		this.manejador = manejador;
	}

}
