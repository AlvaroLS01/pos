
 package com.comerzzia.pos.core.devices.configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

public class DeviceModelConfiguration {

    private static final String TAG_OPERATIONS = "commands";

    private XMLDocumentNode configConexion;
    private XMLDocumentNode modelConfiguration;  // Necesitamos almacenar toda la configuración del modelo. Para el caso en el que el modelo tiene configuradas operaciones
    private XMLDocumentNode operationsConfiguration;
    private String connectionName;
    private String connectionType;
	private String manager;

    public DeviceModelConfiguration(XMLDocumentNode configModelo, XMLDocumentNode configConexion, String nombreConexion, String tipoConexion, String manejador) {
		try {
            this.modelConfiguration = configModelo;
            this.configConexion = configConexion;
            this.connectionName = nombreConexion;
            this.connectionType = tipoConexion;
            this.manager = manejador;

            // Leemos las operaciones si el dispositivo las tiene
            operationsConfiguration = configModelo.getNode(TAG_OPERATIONS, true);
        }
        catch (XMLDocumentNodeNotFoundException ex) {
            Logger.getLogger(DeviceModelConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public XMLDocumentNode getConnectionConfig() {
        return configConexion;
    }

    public void setConnectionConfig(XMLDocumentNode configConexion) {
        this.configConexion = configConexion;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public XMLDocumentNode getModelConfiguration() {
        return modelConfiguration;
    }

    public void setModelConfiguration(XMLDocumentNode modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    public XMLDocumentNode getOperationsConfiguration() {
            return operationsConfiguration;
    }

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

}
