package com.comerzzia.cardoso.pos.services.atractor;

/**
 * GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK
 */
public interface Sincronizacion extends javax.xml.rpc.Service{

	public java.lang.String getSincronizacionHttpSoap11EndpointAddress();

	public SincronizacionPortType getSincronizacionHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException;

	public SincronizacionPortType getSincronizacionHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;

}
