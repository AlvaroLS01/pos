package com.comerzzia.cardoso.pos.services.atractor;

/**
 * GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK
 */
public class SincronizacionPortTypeProxy implements SincronizacionPortType{

	private String _endpoint = null;
	private SincronizacionPortType sincronizacionPortType = null;

	public SincronizacionPortTypeProxy(){
		_initSincronizacionPortTypeProxy();
	}

	public SincronizacionPortTypeProxy(String endpoint){
		_endpoint = endpoint;
		_initSincronizacionPortTypeProxy();
	}

	private void _initSincronizacionPortTypeProxy(){
		try{
			sincronizacionPortType = (new SincronizacionLocator()).getSincronizacionHttpSoap11Endpoint();
			if(sincronizacionPortType != null){
				if(_endpoint != null)
					((javax.xml.rpc.Stub) sincronizacionPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) sincronizacionPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
			}

		}
		catch(javax.xml.rpc.ServiceException serviceException){
		}
	}

	public String getEndpoint(){
		return _endpoint;
	}

	public void setEndpoint(String endpoint){
		_endpoint = endpoint;
		if(sincronizacionPortType != null)
			((javax.xml.rpc.Stub) sincronizacionPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public SincronizacionPortType getSincronizacionPortType(){
		if(sincronizacionPortType == null)
			_initSincronizacionPortTypeProxy();
		return sincronizacionPortType;
	}

	public java.lang.String peticion(java.lang.String datos) throws java.rmi.RemoteException{
		if(sincronizacionPortType == null)
			_initSincronizacionPortTypeProxy();
		return sincronizacionPortType.peticion(datos);
	}

}