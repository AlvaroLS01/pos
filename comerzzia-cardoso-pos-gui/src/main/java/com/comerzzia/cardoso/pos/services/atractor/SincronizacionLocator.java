package com.comerzzia.cardoso.pos.services.atractor;

/**
 * GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK
 */
@SuppressWarnings({"serial", "unchecked", "rawtypes"})
public class SincronizacionLocator extends org.apache.axis.client.Service implements Sincronizacion{

	public SincronizacionLocator(){
	}

	public SincronizacionLocator(org.apache.axis.EngineConfiguration config){
		super(config);
	}

	public SincronizacionLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException{
		super(wsdlLoc, sName);
	}

	private java.lang.String SincronizacionHttpSoap11Endpoint_address = "http://127.0.0.1:8080/atractor/sync/services/Sincronizacion.SincronizacionHttpSoap11Endpoint/";

	public java.lang.String getSincronizacionHttpSoap11EndpointAddress(){
		return SincronizacionHttpSoap11Endpoint_address;
	}

	private java.lang.String SincronizacionHttpSoap11EndpointWSDDServiceName = "SincronizacionHttpSoap11Endpoint";

	public java.lang.String getSincronizacionHttpSoap11EndpointWSDDServiceName(){
		return SincronizacionHttpSoap11EndpointWSDDServiceName;
	}

	public void setSincronizacionHttpSoap11EndpointWSDDServiceName(java.lang.String name){
		SincronizacionHttpSoap11EndpointWSDDServiceName = name;
	}

	public SincronizacionPortType getSincronizacionHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException{
		java.net.URL endpoint;
		try{
			endpoint = new java.net.URL(SincronizacionHttpSoap11Endpoint_address);
		}
		catch(java.net.MalformedURLException e){
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getSincronizacionHttpSoap11Endpoint(endpoint);
	}

	public SincronizacionPortType getSincronizacionHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException{
		try{
			SincronizacionSoap11BindingStub _stub = new SincronizacionSoap11BindingStub(portAddress, this);
			_stub.setPortName(getSincronizacionHttpSoap11EndpointWSDDServiceName());
			return _stub;
		}
		catch(org.apache.axis.AxisFault e){
			return null;
		}
	}

	public void setSincronizacionHttpSoap11EndpointEndpointAddress(java.lang.String address){
		SincronizacionHttpSoap11Endpoint_address = address;
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException{
		try{
			if(SincronizacionPortType.class.isAssignableFrom(serviceEndpointInterface)){
				SincronizacionSoap11BindingStub _stub = new SincronizacionSoap11BindingStub(
				        new java.net.URL(SincronizacionHttpSoap11Endpoint_address), this);
				_stub.setPortName(getSincronizacionHttpSoap11EndpointWSDDServiceName());
				return _stub;
			}
		}
		catch(java.lang.Throwable t){
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException{
		if(portName == null){
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if("SincronizacionHttpSoap11Endpoint".equals(inputPortName)){
			return getSincronizacionHttpSoap11Endpoint();
		}
		else{
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName(){
		return new javax.xml.namespace.QName("http://sync.atractor.mpsistemas.es", "Sincronizacion");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts(){
		if(ports == null){
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://sync.atractor.mpsistemas.es", "SincronizacionHttpSoap11Endpoint"));
		}
		return ports.iterator();
	}

	/**
	* Set the endpoint address for the specified port name.
	*/
	public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException{
		if("SincronizacionHttpSoap11Endpoint".equals(portName)){
			setSincronizacionHttpSoap11EndpointEndpointAddress(address);
		}
		else{
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	* Set the endpoint address for the specified port name.
	*/
	public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException{
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
