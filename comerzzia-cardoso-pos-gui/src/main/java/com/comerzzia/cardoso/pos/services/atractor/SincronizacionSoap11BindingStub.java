package com.comerzzia.cardoso.pos.services.atractor;

/**
 * GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class SincronizacionSoap11BindingStub extends org.apache.axis.client.Stub implements SincronizacionPortType{

	private java.util.Vector cachedSerClasses = new java.util.Vector();
	private java.util.Vector cachedSerQNames = new java.util.Vector();
	private java.util.Vector cachedSerFactories = new java.util.Vector();
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

	static org.apache.axis.description.OperationDesc[] _operations;

	static{
		_operations = new org.apache.axis.description.OperationDesc[1];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1(){
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("peticion");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://sync.atractor.mpsistemas.es", "datos"), org.apache.axis.description.ParameterDesc.IN,
		        new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		param.setOmittable(true);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://sync.atractor.mpsistemas.es", "return"));
		oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

	}

	public SincronizacionSoap11BindingStub() throws org.apache.axis.AxisFault{
		this(null);
	}

	public SincronizacionSoap11BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault{
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public SincronizacionSoap11BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault{
		if(service == null){
			super.service = new org.apache.axis.client.Service();
		}
		else{
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
	}

	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException{
		try{
			org.apache.axis.client.Call _call = super._createCall();
			if(super.maintainSessionSet){
				_call.setMaintainSession(super.maintainSession);
			}
			if(super.cachedUsername != null){
				_call.setUsername(super.cachedUsername);
			}
			if(super.cachedPassword != null){
				_call.setPassword(super.cachedPassword);
			}
			if(super.cachedEndpoint != null){
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if(super.cachedTimeout != null){
				_call.setTimeout(super.cachedTimeout);
			}
			if(super.cachedPortName != null){
				_call.setPortName(super.cachedPortName);
			}
			java.util.Enumeration keys = super.cachedProperties.keys();
			while(keys.hasMoreElements()){
				java.lang.String key = (java.lang.String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			return _call;
		}
		catch(java.lang.Throwable _t){
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
		}
	}

	public java.lang.String peticion(java.lang.String datos) throws java.rmi.RemoteException{
		if(super.cachedEndpoint == null){
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("urn:peticion");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://sync.atractor.mpsistemas.es", "peticion"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { datos });
			if(_resp instanceof java.rmi.RemoteException){
				throw (java.rmi.RemoteException) _resp;
			}
			else{
				extractAttachments(_call);
				try{
					return (java.lang.String) _resp;
				}
				catch(java.lang.Exception _exception){
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		}
		catch(org.apache.axis.AxisFault axisFaultException){
			throw axisFaultException;
		}
	}
}
