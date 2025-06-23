package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Cabfac {

	public static final String LABEL_CAB = "CABFAC";

	public static final String MONEDA = "COP";
	public static final String TIPO_FACTURA_VENTA = "01";
	public static final String TIPO_FACTURA_DEVOLUCION = "91";
	public static final String TIPO_DOC_VENTA = "F";
	public static final String TIPO_DOC_DEVOLUCION = "NC";
	public static final String TIPO_OPERA_VENTAS = "10";
	public static final String TIPO_OPERA_DEVOLUCION_REFERENCIADA = "20";
	public static final String TIPO_OPERA_DEVOLUCION_SIN_REFERENCIA = "22";
	
	public static final BigDecimal CODIGO_IDTENFICACION_CONSUMIDOR_FINAL = new BigDecimal(13);

	public String tipodoc;
	public String id;
	public String prefijoRangoFolios;
	public String inicioRangoFolios;
	public String finRangoFolios;
	public String claveTecnica;
	public String numeroResolucion;
	public Date fechaResolucion;
	public Date fechaVigenciaInicio;
	public Date fechaVigenciaFin;
	public Date fechaEmision; 
	public Date horaEmision; 
	public String tipoFactura; 
	public String terminosPago;
	public String referenciaFacturaId;
	public String referenciaFacturaUUID;
	public Date referenciaFacturaFecha;
	public String referenciaFacturaCodigo;
	public String numeroPedido;
	public Date fechaPedido;
	public String numeroAlbaran;
	public Date fechaAlbaran;
	public String emisorTipoPersona; 
	public String emisorId; 
	public String emisorEmail;
	public String ambiente;
	public BigDecimal emisorTipoIdentificacion;
	public String emisorNombre;
	public String emisorDireccion;
	public String emisorCiudad;
	public String emisorRegion;
	public String emisorDistrito;
	public String emisorCodigoPostal;
	public String emisorPais; 
	public String emisorRazonSocial;
	public String emisorRegimen;
	public String receptorTipoPersona; 
	public String receptorId;
	public BigDecimal receptorTipoIdentificacion;
	public String receptorNombre;
	public String receptorRegion;
	public String receptorDistrito;
	public String receptorCiudad;
	public String receptorDireccion;
	public String receptorCodigoPostal;
	public String receptorPais;
	public String receptorRegimen;
	public String receptorRazonSocial;
	public String receptorNombrePersona;
	public String receptorApellidoPersona;
	public String lugarEntregaDireccion;
	public String lugarEntregaPais;
	public String lugarEntregaCiudad;
	public String lugarEntregaCodigoPostal;
	public String lugarEntregaCondiciones;
	public String lugarExpedicionDireccion;
	public String lugarExpedicionPais;
	public String lugarExpedicionCiudad;
	public String lugarExpedicionCodigoPostal;
	public BigDecimal totalLineas;
	public BigDecimal totalBaseImponible;
	public BigDecimal totalFactura;
	public String numeroCuentaBancaria;
	public Date fechaVencimiento;
	public String correoElectronicoReceptor;
	public String monedaOrigen;
	public String monedaDestino;
	public BigDecimal tasaCambio;
	public String fechaTasaCambio;
	public BigDecimal totalDescuentos;
	public BigDecimal totalCargos;
	public BigDecimal valorAnticipo;
	public String fechaAnticipo;
	public String avisoRecepcion;
	public String lugarEntregaEAN;
	public String emisorRegistroMercantil;
	public String emisorTelefono;
	public String emisorObservaciones;
	public String receptorCodigoInterno;
	public String codigoLTA;
	public String numeroLineas;
	public String dvEmisor;
	public String dvReceptor;
	public String fechaIniFacturacion;
	public String horaIniFacturacion;
	public String fechaFinFacturacion;
	public String horaFinFacturacion;
	public String idAnticipo;
	public String fechaRecibidoAnticipo;
	public String instruccionesAnticipo;
	public BigDecimal totalBrutoTrib;
	public String lugarExpedicionRegion;
	public String lugarEntregaRegion;
	public BigDecimal totalAnticipos;
	public String codigoMunicipioEmisor;
	public String codigoMunicipioLugarEntrega;
	public String codigoMunicipioReceptor;
	public String codigoMunicipioLugarExpedicion;
	public String tipoOperacion;
	public String descNatu;
	public String codigoMunicipioFiscalEmisor;
	public String emisorDistritoFiscal;
	public String emisorCidudadFiscal;
	public String emisorCodigoPostalFiscal;
	public String emisorRegionFiscal;
	public String emisorDireccionFiscal;
	public String emisorPaisFiscal;
	public String codigoMunicipioFiscalReceptor;
	public String receptorDistritoFiscal;
	public String receptorCiudadFiscal;
	public String receptorCodigoPostalFiscal;
	public String receptorRegionFiscal;
	public String receptorDireccionFiscal;
	public String receptorPaisFiscal;
	public Date fechaEntrega;
	public Date horaEntrega;
	public String transportistaId;
	public String dvTransportista;
	public BigDecimal transportistaTipoIdentificacion;
	public String transportistaNombre;
	public String codigoMunicipioTransportista;
	public String transportistaDireccion;
	public String transportistaCiudad;
	public String transportistaRegion;
	public String transportistaCodigoPostal;
	public String transportistaPais;
	public String transportistaRazonSocial;
	public String codigoMunicipioFiscalTransportista;
	public String transportistaCiudadFiscal;
	public String transportistaCodigoPostalFiscal;
	public String transportistaRegionFiscal;
	public String transportistaDireccionFiscal;
	public String transportistaPaisFiscal;
	public String transportistaRegistroMercantil;
	public String transportistaTelefono;
	public String transportistaEmail;
	public String transportistaNombreContacto;
	public String transportistaObservaciones;
	public String metodopagoTransporte;
	public Date fechaVencimientoDoc;
	public String emisorCodigoActividadEconomica;
	public String emisorNombreContacto;
	public String receptorObservaciones;
	public String receptorTelefono;
	public String receptorNombreContacto;
	public String receptorRegistroMercantil;
	public String transportistaRegimen;
	public String tipoImpuestoEmisor;
	public String tipoImpuestoReceptor;
	public String tipoExtClaro;
	public String numeroCumplimiento;
	public BigDecimal redondeoPagable;
	public String monedaOrigenAlternativa;
	public String monedaDestinoAlternativa;
	public BigDecimal tasaCambioAlternativa;
	public String fechaTasaCambioAlternativa;

	public Cabfac() {
		emisorTipoPersona = "1";
		receptorTipoPersona = "2";
		emisorTipoIdentificacion = new BigDecimal(31);
	}

	public String getTipodoc() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipodoc, 2);
	}

	public String getId() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(id, 15);
	}

	public String getPrefijoRangoFolios() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(prefijoRangoFolios, 15);
	}

	public String getInicioRangoFolios() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(inicioRangoFolios, 15);
	}

	public String getFinRangoFolios() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(finRangoFolios, 15);
	}

	public String getClaveTecnica() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(claveTecnica, 250);
	}

	public String getNumeroResolucion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroResolucion, 20);
	}

	public String getFechaResolucion() {
		return EdicomFormat.devuelveFechaFormateada(fechaResolucion);
	}

	public String getFechaVigenciaInicio() {
		return EdicomFormat.devuelveFechaFormateada(fechaVigenciaInicio);
	}

	public String getFechaVigenciaFin() {
		return EdicomFormat.devuelveFechaFormateada(fechaVigenciaFin);
	}

	public String getFechaEmision() {
		return EdicomFormat.devuelveFechaFormateada(fechaEmision);
	}

	public String getHoraEmision() {
		return EdicomFormat.devuelveHoraFormateada(horaEmision);
	}

	public String getTipoFactura() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipoFactura, 15);
	}

	public String getMoneda() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(MONEDA, 15);
	}

	@Deprecated
	public String getTerminosPago() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(terminosPago, 15);
	}

	@Deprecated
	public String getReferenciaFacturaId() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(referenciaFacturaId, 30);
	}

	@Deprecated
	public String getReferenciaFacturaUUID() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(referenciaFacturaUUID, 50);
	}

	@Deprecated
	public String getReferenciaFacturaFecha() {
		return EdicomFormat.devuelveHoraFormateada(referenciaFacturaFecha);
	}

	@Deprecated
	public String getReferenciaFacturaCodigo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(referenciaFacturaCodigo, 25);
	}

	@Deprecated
	public String getNumeroPedido() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroPedido, 15);
	}

	@Deprecated
	public String getFechaPedido() {
		return EdicomFormat.devuelveHoraFormateada(fechaPedido);
	}

	@Deprecated
	public String getNumeroAlbaran() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroAlbaran, 15);
	}

	@Deprecated
	public String getFechaAlbaran() {
		return EdicomFormat.devuelveHoraFormateada(fechaAlbaran);
	}

	public String getEmisorTipoPersona() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorTipoPersona, 15);
	}

	public String getEmisorId() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorId, 15);
	}

	public String getEmisorTipoIdentificacion() {
		return EdicomFormat.rellenaCerosNumericos(emisorTipoIdentificacion, 15, 0);
	}

	public String getEmisorNombre() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorNombre, 255);
	}

	public String getEmisorDireccion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorDireccion, 255);
	}

	public String getEmisorCiudad() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorCiudad, 255);
	}

	public String getEmisorRegion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorRegion, 255);
	}

	public String getEmisorDistrito() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorDistrito, 255);
	}

	public String getEmisorCodigoPostal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorCodigoPostal, 15);
	}

	public String getEmisorPais() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorPais, 15);
	}

	public String getEmisorRazonSocial() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorRazonSocial, 255);
	}

	public String getEmisorRegimen() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorRegimen, 15);
	}

	public String getReceptorTipoPersona() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorTipoPersona, 15);
	}

	public String getReceptorId() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorId, 15);
	}

	public String getReceptorTipoIdentificacion() {
		return EdicomFormat.rellenaCerosNumericos(receptorTipoIdentificacion, 15, 0);
	}

	public String getReceptorNombre() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorNombre, 255);
	}

	public String getReceptorRegion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorRegion, 255);
	}

	public String getReceptorDistrito() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorDistrito, 255);
	}

	public String getReceptorCiudad() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorCiudad, 255);
	}

	public String getReceptorDireccion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorDireccion, 255);
	}

	public String getReceptorCodigoPostal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorCodigoPostal, 15);
	}

	public String getReceptorPais() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorPais, 15);
	}

	public String getReceptorRegimen() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorRegimen, 15);
	}

	public String getReceptorRazonSocial() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorRazonSocial, 255);
	}

	public String getReceptorNombrePersona() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorNombrePersona, 100);
	}

	public String getReceptorApellidoPersona() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorApellidoPersona, 100);
	}

	public String getLugarEntregaDireccion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarEntregaDireccion, 255);
	}

	public String getLugarEntregaPais() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarEntregaPais, 15);
	}

	public String getLugarEntregaCiudad() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarEntregaCiudad, 255);
	}

	public String getLugarEntregaCodigoPostal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarEntregaCodigoPostal, 15);
	}

	public String getLugarEntregaCondiciones() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarEntregaCondiciones, 15);
	}

	public String getLugarExpedicionDireccion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarExpedicionDireccion, 255);
	}

	public String getLugarExpedicionPais() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarExpedicionPais, 15);
	}

	public String getLugarExpedicionCiudad() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarExpedicionCiudad, 255);
	}

	public String getLugarExpedicionCodigoPostal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarExpedicionCodigoPostal, 15);
	}

	public String getTotalLineas() {
		return EdicomFormat.rellenaCerosNumericos(totalLineas, 15, 6);
	}

	public String getTotalBaseImponible() {
		return EdicomFormat.rellenaCerosNumericos(totalBaseImponible, 15, 6);
	}

	public String getTotalFactura() {
		return EdicomFormat.rellenaCerosNumericos(totalFactura, 15, 6);
	}

	@Deprecated
	public String getNumeroCuentaBancaria() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroCuentaBancaria, 255);
	}

	@Deprecated
	public String getFechaVencimiento() {
		return EdicomFormat.devuelveHoraFormateada(fechaVencimiento);
	}

	public String getCorreoElectronicoReceptor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(correoElectronicoReceptor, 255);
	}

	public String getMonedaOrigen() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(monedaOrigen, 3);
	}

	public String getMonedaDestino() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(monedaDestino, 3);
	}

	public String getTasaCambio() {
		return EdicomFormat.rellenaCerosNumericos(tasaCambio, 20, 4);
	}

	public String getFechaTasaCambio() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(fechaTasaCambio, 15);
	}

	public String getTotalDescuentos() {
		return EdicomFormat.rellenaCerosNumericos(totalDescuentos, 20, 6);
	}

	public String getTotalCargos() {
		return EdicomFormat.rellenaCerosNumericos(totalCargos, 20, 6);
	}

	public String getValorAnticipo() {
		return EdicomFormat.rellenaCerosNumericos(valorAnticipo, 20, 6);
	}

	public String getFechaAnticipo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(fechaAnticipo, 15);
	}

	public String getAvisoRecepcion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(avisoRecepcion, 25);
	}

	public String getLugarEntregaEAN() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarEntregaEAN, 14);
	}

	public String getEmisorRegistroMercantil() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorRegistroMercantil, 25);
	}

	public String getEmisorTelefono() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorTelefono, 25);
	}

	public String getEmisorObservaciones() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorObservaciones, 255);
	}

	public String getReceptorCodigoInterno() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorCodigoInterno, 25);
	}

	public String getCodigoLTA() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoLTA, 25);
	}

	public String getAmbiente() {
		return ambiente;
	}
	
	public String getEmisorEmail() {
		return emisorEmail;
	}

	public String getNumeroLineas() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroLineas, 15);
	}

	public String getDvEmisor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(dvEmisor, 1);
	}

	public String getDvReceptor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(dvReceptor, 1);
	}

	public String getFechaIniFacturacion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(fechaIniFacturacion, 8);
	}

	public String getHoraIniFacturacion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(horaIniFacturacion, 8);
	}

	public String getFechaFinFacturacion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(fechaFinFacturacion, 8);
	}

	public String getHoraFinFacturacion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(horaFinFacturacion, 8);
	}

	public String getIdAnticipo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(idAnticipo, 20);
	}

	public String getFechaRecibidoAnticipo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(fechaRecibidoAnticipo, 15);
	}

	public String getInstruccionesAnticipo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(instruccionesAnticipo, 20);
	}

	public String getTotalBrutoTrib() {
		return EdicomFormat.rellenaCerosNumericos(totalBrutoTrib, 15, 6);
	}

	public String getLugarExpedicionRegion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarExpedicionRegion, 255);
	}

	public String getLugarEntregaRegion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(lugarEntregaRegion, 255);
	}

	public String getTotalAnticipos() {
		return EdicomFormat.rellenaCerosNumericos(totalAnticipos, 15, 6);
	}

	public String getCodigoMunicipioEmisor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioEmisor, 255);
	}

	public String getCodigoMunicipioLugarEntrega() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioLugarEntrega, 255);
	}

	public String getCodigoMunicipioReceptor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioReceptor, 255);
	}

	public String getCodigoMunicipioLugarExpedicion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioLugarExpedicion, 255);
	}

	public String getTipoOperacion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipoOperacion, 255);
	}

	public String getDescNatu() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(descNatu, 999);
	}

	public String getCodigoMunicipioFiscalEmisor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioFiscalEmisor, 255);
	}

	public String getEmisorDistritoFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorDistritoFiscal, 255);
	}

	public String getEmisorCidudadFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorCidudadFiscal, 255);
	}

	public String getEmisorCodigoPostalFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorCodigoPostalFiscal, 15);
	}

	public String getEmisorRegionFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorRegionFiscal, 255);
	}

	public String getEmisorDireccionFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorDireccionFiscal, 255);
	}

	public String getEmisorPaisFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorPaisFiscal, 15);
	}

	public String getCodigoMunicipioFiscalReceptor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioFiscalReceptor, 255);
	}

	public String getReceptorDistritoFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorDistritoFiscal, 255);
	}

	public String getReceptorCiudadFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorCiudadFiscal, 255);
	}

	public String getReceptorCodigoPostalFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorCodigoPostalFiscal, 15);
	}

	public String getReceptorRegionFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorRegionFiscal, 255);
	}

	public String getReceptorDireccionFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorDireccionFiscal, 255);
	}

	public String getReceptorPaisFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorPaisFiscal, 15);
	}

	public String getFechaEntrega() {
		return EdicomFormat.devuelveFechaFormateada(fechaEntrega);
	}

	public String getHoraEntrega() {
		return EdicomFormat.devuelveHoraFormateada(horaEntrega);
	}

	public String getTransportistaId() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaId, 15);
	}

	public String getDvTransportista() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(dvTransportista, 1);
	}

	public String getTransportistaTipoIdentificacion() {
		return EdicomFormat.rellenaCerosNumericos(transportistaTipoIdentificacion, 15, 0);
	}

	public String getTransportistaNombre() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaNombre, 255);
	}

	public String getCodigoMunicipioTransportista() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioTransportista, 255);
	}

	public String getTransportistaDireccion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaDireccion, 255);
	}

	public String getTransportistaCiudad() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaCiudad, 255);
	}

	public String getTransportistaRegion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaRegion, 255);
	}

	public String getTransportistaCodigoPostal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaCodigoPostal, 15);
	}

	public String getTransportistaPais() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaPais, 15);
	}

	public String getTransportistaRazonSocial() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaRazonSocial, 255);
	}

	public String getCodigoMunicipioFiscalTransportista() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigoMunicipioFiscalTransportista, 255);
	}

	public String getTransportistaCiudadFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaCiudadFiscal, 255);
	}

	public String getTransportistaCodigoPostalFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaCodigoPostalFiscal, 15);
	}

	public String getTransportistaRegionFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaRegionFiscal, 255);
	}

	public String getTransportistaDireccionFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaDireccionFiscal, 255);
	}

	public String getTransportistaPaisFiscal() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaPaisFiscal, 15);
	}

	public String getTransportistaRegistroMercantil() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaRegistroMercantil, 25);
	}

	public String getTransportistaTelefono() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaTelefono, 25);
	}

	public String getTransportistaEmail() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaEmail, 100);
	}

	public String getTransportistaNombreContacto() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaNombreContacto, 50);
	}

	public String getTransportistaObservaciones() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaObservaciones, 500);
	}

	public String getMetodopagoTransporte() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(metodopagoTransporte, 255);
	}

	public String getFechaVencimientoDoc() {
		return EdicomFormat.devuelveHoraFormateada(fechaVencimientoDoc);
	}

	public String getEmisorCodigoActividadEconomica() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorCodigoActividadEconomica, 255);
	}

	public String getEmisorNombreContacto() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(emisorNombreContacto, 50);
	}

	public String getReceptorObservaciones() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorObservaciones, 500);
	}

	public String getReceptorTelefono() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorTelefono, 25);
	}

	public String getReceptorNombreContacto() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorNombreContacto, 50);
	}

	public String getReceptorRegistroMercantil() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(receptorRegistroMercantil, 25);
	}

	public String getTransportistaRegimen() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(transportistaRegimen, 15);
	}

	public String getTipoImpuestoEmisor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipoImpuestoEmisor, 2);
	}

	public String getTipoImpuestoReceptor() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipoImpuestoReceptor, 2);
	}

	public String getTipoExtClaro() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipoExtClaro, 2);
	}

	public String getNumeroCumplimiento() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroCumplimiento, 25);
	}

	public String getRedondeoPagable() {
		return EdicomFormat.rellenaCerosNumericos(redondeoPagable, 15, 6);
	}

	public String getMonedaOrigenAlternativa() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(monedaOrigenAlternativa, 3);
	}

	public String getMonedaDestinoAlternativa() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(monedaDestinoAlternativa, 3);
	}

	public String getTasaCambioAlternativa() {
		return EdicomFormat.rellenaCerosNumericos(tasaCambioAlternativa, 20, 4);
	}

	public String getFechaTasaCambioAlternativa() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(fechaTasaCambioAlternativa, 15);
	}

	public void setTipodoc(String tipodoc) {
		this.tipodoc = tipodoc;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPrefijoRangoFolios(String prefijoRangoFolios) {
		this.prefijoRangoFolios = prefijoRangoFolios;
	}

	public void setInicioRangoFolios(String inicioRangoFolios) {
		this.inicioRangoFolios = inicioRangoFolios;
	}

	public void setFinRangoFolios(String finRangoFolios) {
		this.finRangoFolios = finRangoFolios;
	}

	public void setClaveTecnica(String claveTecnica) {
		this.claveTecnica = claveTecnica;
	}

	public void setNumeroResolucion(String numeroResolucion) {
		this.numeroResolucion = numeroResolucion;
	}

	public void setFechaResolucion(Date fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public void setFechaVigenciaInicio(Date fechaVigenciaInicio) {
		this.fechaVigenciaInicio = fechaVigenciaInicio;
	}

	public void setFechaVigenciaFin(Date fechaVigenciaFin) {
		this.fechaVigenciaFin = fechaVigenciaFin;
	}

	public void setEmisorEmail(String emisorEmail) {
		this.emisorEmail = emisorEmail;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public void setHoraEmision(Date horaEmision) {
		this.horaEmision = horaEmision;
	}

	public void setTipoFactura(String tipoFactura) {
		this.tipoFactura = tipoFactura;
	}

	@Deprecated
	public void setTerminosPago(String terminosPago) {
		this.terminosPago = terminosPago;
	}

	@Deprecated
	public void setReferenciaFacturaId(String referenciaFacturaId) {
		this.referenciaFacturaId = referenciaFacturaId;
	}

	@Deprecated
	public void setReferenciaFacturaUUID(String referenciaFacturaUUID) {
		this.referenciaFacturaUUID = referenciaFacturaUUID;
	}

	@Deprecated
	public void setReferenciaFacturaFecha(Date referenciaFacturaFecha) {
		this.referenciaFacturaFecha = referenciaFacturaFecha;
	}

	@Deprecated
	public void setReferenciaFacturaCodigo(String referenciaFacturaCodigo) {
		this.referenciaFacturaCodigo = referenciaFacturaCodigo;
	}

	@Deprecated
	public void setNumeroPedido(String numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	@Deprecated
	public void setFechaPedido(Date fechaPedido) {
		this.fechaPedido = fechaPedido;
	}

	@Deprecated
	public void setNumeroAlbaran(String numeroAlbaran) {
		this.numeroAlbaran = numeroAlbaran;
	}

	@Deprecated
	public void setFechaAlbaran(Date fechaAlbaran) {
		this.fechaAlbaran = fechaAlbaran;
	}

	public void setEmisorTipoPersona(String emisorTipoPersona) {
		this.emisorTipoPersona = emisorTipoPersona;
	}

	public void setEmisorId(String emisorId) {
		this.emisorId = emisorId;
	}

	public void setEmisorTipoIdentificacion(BigDecimal emisorTipoIdentificacion) {
		this.emisorTipoIdentificacion = emisorTipoIdentificacion;
	}

	public void setEmisorNombre(String emisorNombre) {
		this.emisorNombre = emisorNombre;
	}

	public void setEmisorDireccion(String emisorDireccion) {
		this.emisorDireccion = emisorDireccion;
	}

	public void setEmisorCiudad(String emisorCiudad) {
		this.emisorCiudad = emisorCiudad;
	}

	public void setEmisorRegion(String emisorRegion) {
		this.emisorRegion = emisorRegion;
	}

	public void setEmisorDistrito(String emisorDistrito) {
		this.emisorDistrito = emisorDistrito;
	}

	public void setEmisorCodigoPostal(String emisorCodigoPostal) {
		this.emisorCodigoPostal = emisorCodigoPostal;
	}

	public void setEmisorPais(String emisorPais) {
		this.emisorPais = emisorPais;
	}

	public void setEmisorRazonSocial(String emisorRazonSocial) {
		this.emisorRazonSocial = emisorRazonSocial;
	}

	public void setEmisorRegimen(String emisorRegimen) {
		this.emisorRegimen = emisorRegimen;
	}

	public void setReceptorTipoPersona(String receptorTipoPersona) {
		this.receptorTipoPersona = receptorTipoPersona;
	}

	public void setReceptorId(String receptorId) {
		this.receptorId = receptorId;
	}

	public void setReceptorTipoIdentificacion(BigDecimal receptorTipoIdentificacion) {
		this.receptorTipoIdentificacion = receptorTipoIdentificacion;
	}

	public void setReceptorNombre(String receptorNombre) {
		this.receptorNombre = receptorNombre;
	}

	public void setReceptorRegion(String receptorRegion) {
		this.receptorRegion = receptorRegion;
	}

	public void setReceptorDistrito(String receptorDistrito) {
		this.receptorDistrito = receptorDistrito;
	}

	public void setReceptorCiudad(String receptorCiudad) {
		this.receptorCiudad = receptorCiudad;
	}

	public void setReceptorDireccion(String receptorDireccion) {
		this.receptorDireccion = receptorDireccion;
	}

	public void setReceptorCodigoPostal(String receptorCodigoPostal) {
		this.receptorCodigoPostal = receptorCodigoPostal;
	}

	public void setReceptorPais(String receptorPais) {
		this.receptorPais = receptorPais;
	}

	public void setReceptorRegimen(String receptorRegimen) {
		this.receptorRegimen = receptorRegimen;
	}

	public void setReceptorRazonSocial(String receptorRazonSocial) {
		this.receptorRazonSocial = receptorRazonSocial;
	}

	public void setReceptorNombrePersona(String receptorNombrePersona) {
		this.receptorNombrePersona = receptorNombrePersona;
	}

	public void setReceptorApellidoPersona(String receptorApellidoPersona) {
		this.receptorApellidoPersona = receptorApellidoPersona;
	}

	public void setLugarEntregaDireccion(String lugarEntregaDireccion) {
		this.lugarEntregaDireccion = lugarEntregaDireccion;
	}

	public void setLugarEntregaPais(String lugarEntregaPais) {
		this.lugarEntregaPais = lugarEntregaPais;
	}

	public void setLugarEntregaCiudad(String lugarEntregaCiudad) {
		this.lugarEntregaCiudad = lugarEntregaCiudad;
	}

	public void setLugarEntregaCodigoPostal(String lugarEntregaCodigoPostal) {
		this.lugarEntregaCodigoPostal = lugarEntregaCodigoPostal;
	}

	public void setLugarEntregaCondiciones(String lugarEntregaCondiciones) {
		this.lugarEntregaCondiciones = lugarEntregaCondiciones;
	}

	public void setLugarExpedicionDireccion(String lugarExpedicionDireccion) {
		this.lugarExpedicionDireccion = lugarExpedicionDireccion;
	}

	public void setLugarExpedicionPais(String lugarExpedicionPais) {
		this.lugarExpedicionPais = lugarExpedicionPais;
	}

	public void setLugarExpedicionCiudad(String lugarExpedicionCiudad) {
		this.lugarExpedicionCiudad = lugarExpedicionCiudad;
	}

	public void setLugarExpedicionCodigoPostal(String lugarExpedicionCodigoPostal) {
		this.lugarExpedicionCodigoPostal = lugarExpedicionCodigoPostal;
	}

	public void setTotalLineas(BigDecimal totalLineas) {
		this.totalLineas = totalLineas;
	}

	public void setTotalBaseImponible(BigDecimal totalBaseImponible) {
		this.totalBaseImponible = totalBaseImponible;
	}

	public void setTotalFactura(BigDecimal totalFactura) {
		this.totalFactura = totalFactura;
	}

	public void setNumeroCuentaBancaria(String numeroCuentaBancaria) {
		this.numeroCuentaBancaria = numeroCuentaBancaria;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public void setCorreoElectronicoReceptor(String correoElectronicoReceptor) {
		this.correoElectronicoReceptor = correoElectronicoReceptor;
	}

	public void setMonedaOrigen(String monedaOrigen) {
		this.monedaOrigen = monedaOrigen;
	}

	public void setMonedaDestino(String monedaDestino) {
		this.monedaDestino = monedaDestino;
	}

	public void setTasaCambio(BigDecimal tasaCambio) {
		this.tasaCambio = tasaCambio;
	}

	public void setFechaTasaCambio(String fechaTasaCambio) {
		this.fechaTasaCambio = fechaTasaCambio;
	}

	public void setTotalDescuentos(BigDecimal totalDescuentos) {
		this.totalDescuentos = totalDescuentos;
	}

	public void setTotalCargos(BigDecimal totalCargos) {
		this.totalCargos = totalCargos;
	}

	public void setValorAnticipo(BigDecimal valorAnticipo) {
		this.valorAnticipo = valorAnticipo;
	}

	public void setFechaAnticipo(String fechaAnticipo) {
		this.fechaAnticipo = fechaAnticipo;
	}

	public void setAvisoRecepcion(String avisoRecepcion) {
		this.avisoRecepcion = avisoRecepcion;
	}

	public void setLugarEntregaEAN(String lugarEntregaEAN) {
		this.lugarEntregaEAN = lugarEntregaEAN;
	}

	public void setEmisorRegistroMercantil(String emisorRegistroMercantil) {
		this.emisorRegistroMercantil = emisorRegistroMercantil;
	}

	public void setEmisorTelefono(String emisorTelefono) {
		this.emisorTelefono = emisorTelefono;
	}

	public void setEmisorObservaciones(String emisorObservaciones) {
		this.emisorObservaciones = emisorObservaciones;
	}

	public void setReceptorCodigoInterno(String receptorCodigoInterno) {
		this.receptorCodigoInterno = receptorCodigoInterno;
	}

	public void setCodigoLTA(String codigoLTA) {
		this.codigoLTA = codigoLTA;
	}

	public void setNumeroLineas(String numeroLineas) {
		this.numeroLineas = numeroLineas;
	}

	public void setDvEmisor(String dvEmisor) {
		this.dvEmisor = dvEmisor;
	}

	public void setDvReceptor(String dvReceptor) {
		this.dvReceptor = dvReceptor;
	}

	public void setFechaIniFacturacion(String fechaIniFacturacion) {
		this.fechaIniFacturacion = fechaIniFacturacion;
	}

	public void setHoraIniFacturacion(String horaIniFacturacion) {
		this.horaIniFacturacion = horaIniFacturacion;
	}

	public void setFechaFinFacturacion(String fechaFinFacturacion) {
		this.fechaFinFacturacion = fechaFinFacturacion;
	}

	public void setHoraFinFacturacion(String horaFinFacturacion) {
		this.horaFinFacturacion = horaFinFacturacion;
	}

	public void setIdAnticipo(String idAnticipo) {
		this.idAnticipo = idAnticipo;
	}

	public void setFechaRecibidoAnticipo(String fechaRecibidoAnticipo) {
		this.fechaRecibidoAnticipo = fechaRecibidoAnticipo;
	}

	public void setInstruccionesAnticipo(String instruccionesAnticipo) {
		this.instruccionesAnticipo = instruccionesAnticipo;
	}

	public void setTotalBrutoTrib(BigDecimal totalBrutoTrib) {
		this.totalBrutoTrib = totalBrutoTrib;
	}

	public void setLugarExpedicionRegion(String lugarExpedicionRegion) {
		this.lugarExpedicionRegion = lugarExpedicionRegion;
	}

	public void setLugarEntregaRegion(String lugarEntregaRegion) {
		this.lugarEntregaRegion = lugarEntregaRegion;
	}

	public void setTotalAnticipos(BigDecimal totalAnticipos) {
		this.totalAnticipos = totalAnticipos;
	}

	public void setCodigoMunicipioEmisor(String codigoMunicipioEmisor) {
		this.codigoMunicipioEmisor = codigoMunicipioEmisor;
	}

	public void setCodigoMunicipioLugarEntrega(String codigoMunicipioLugarEntrega) {
		this.codigoMunicipioLugarEntrega = codigoMunicipioLugarEntrega;
	}

	public void setCodigoMunicipioReceptor(String codigoMunicipioReceptor) {
		this.codigoMunicipioReceptor = codigoMunicipioReceptor;
	}

	public void setCodigoMunicipioLugarExpedicion(String codigoMunicipioLugarExpedicion) {
		this.codigoMunicipioLugarExpedicion = codigoMunicipioLugarExpedicion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public void setDescNatu(String descNatu) {
		this.descNatu = descNatu;
	}

	public void setCodigoMunicipioFiscalEmisor(String codigoMunicipioFiscalEmisor) {
		this.codigoMunicipioFiscalEmisor = codigoMunicipioFiscalEmisor;
	}

	public void setEmisorDistritoFiscal(String emisorDistritoFiscal) {
		this.emisorDistritoFiscal = emisorDistritoFiscal;
	}

	public void setEmisorCidudadFiscal(String emisorCidudadFiscal) {
		this.emisorCidudadFiscal = emisorCidudadFiscal;
	}

	public void setEmisorCodigoPostalFiscal(String emisorCodigoPostalFiscal) {
		this.emisorCodigoPostalFiscal = emisorCodigoPostalFiscal;
	}

	public void setEmisorRegionFiscal(String emisorRegionFiscal) {
		this.emisorRegionFiscal = emisorRegionFiscal;
	}

	public void setEmisorDireccionFiscal(String emisorDireccionFiscal) {
		this.emisorDireccionFiscal = emisorDireccionFiscal;
	}

	public void setEmisorPaisFiscal(String emisorPaisFiscal) {
		this.emisorPaisFiscal = emisorPaisFiscal;
	}

	public void setCodigoMunicipioFiscalReceptor(String codigoMunicipioFiscalReceptor) {
		this.codigoMunicipioFiscalReceptor = codigoMunicipioFiscalReceptor;
	}

	public void setReceptorDistritoFiscal(String receptorDistritoFiscal) {
		this.receptorDistritoFiscal = receptorDistritoFiscal;
	}

	public void setReceptorCiudadFiscal(String receptorCiudadFiscal) {
		this.receptorCiudadFiscal = receptorCiudadFiscal;
	}

	public void setReceptorCodigoPostalFiscal(String receptorCodigoPostalFiscal) {
		this.receptorCodigoPostalFiscal = receptorCodigoPostalFiscal;
	}

	public void setReceptorRegionFiscal(String receptorRegionFiscal) {
		this.receptorRegionFiscal = receptorRegionFiscal;
	}

	public void setReceptorDireccionFiscal(String receptorDireccionFiscal) {
		this.receptorDireccionFiscal = receptorDireccionFiscal;
	}

	public void setReceptorPaisFiscal(String receptorPaisFiscal) {
		this.receptorPaisFiscal = receptorPaisFiscal;
	}

	public void setFechaEntrega(Date fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}

	public void setHoraEntrega(Date horaEntrega) {
		this.horaEntrega = horaEntrega;
	}

	public void setTransportistaId(String transportistaId) {
		this.transportistaId = transportistaId;
	}

	public void setDvTransportista(String dvTransportista) {
		this.dvTransportista = dvTransportista;
	}

	public void setTransportistaTipoIdentificacion(BigDecimal transportistaTipoIdentificacion) {
		this.transportistaTipoIdentificacion = transportistaTipoIdentificacion;
	}

	public void setTransportistaNombre(String transportistaNombre) {
		this.transportistaNombre = transportistaNombre;
	}

	public void setCodigoMunicipioTransportista(String codigoMunicipioTransportista) {
		this.codigoMunicipioTransportista = codigoMunicipioTransportista;
	}

	public void setTransportistaDireccion(String transportistaDireccion) {
		this.transportistaDireccion = transportistaDireccion;
	}

	public void setTransportistaCiudad(String transportistaCiudad) {
		this.transportistaCiudad = transportistaCiudad;
	}

	public void setTransportistaRegion(String transportistaRegion) {
		this.transportistaRegion = transportistaRegion;
	}

	public void setTransportistaCodigoPostal(String transportistaCodigoPostal) {
		this.transportistaCodigoPostal = transportistaCodigoPostal;
	}

	public void setTransportistaPais(String transportistaPais) {
		this.transportistaPais = transportistaPais;
	}

	public void setTransportistaRazonSocial(String transportistaRazonSocial) {
		this.transportistaRazonSocial = transportistaRazonSocial;
	}

	public void setCodigoMunicipioFiscalTransportista(String codigoMunicipioFiscalTransportista) {
		this.codigoMunicipioFiscalTransportista = codigoMunicipioFiscalTransportista;
	}

	public void setTransportistaCiudadFiscal(String transportistaCiudadFiscal) {
		this.transportistaCiudadFiscal = transportistaCiudadFiscal;
	}

	public void setTransportistaCodigoPostalFiscal(String transportistaCodigoPostalFiscal) {
		this.transportistaCodigoPostalFiscal = transportistaCodigoPostalFiscal;
	}

	public void setTransportistaRegionFiscal(String transportistaRegionFiscal) {
		this.transportistaRegionFiscal = transportistaRegionFiscal;
	}

	public void setTransportistaDireccionFiscal(String transportistaDireccionFiscal) {
		this.transportistaDireccionFiscal = transportistaDireccionFiscal;
	}

	public void setTransportistaPaisFiscal(String transportistaPaisFiscal) {
		this.transportistaPaisFiscal = transportistaPaisFiscal;
	}

	public void setTransportistaRegistroMercantil(String transportistaRegistroMercantil) {
		this.transportistaRegistroMercantil = transportistaRegistroMercantil;
	}

	public void setTransportistaTelefono(String transportistaTelefono) {
		this.transportistaTelefono = transportistaTelefono;
	}

	public void setTransportistaEmail(String transportistaEmail) {
		this.transportistaEmail = transportistaEmail;
	}

	public void setTransportistaNombreContacto(String transportistaNombreContacto) {
		this.transportistaNombreContacto = transportistaNombreContacto;
	}

	public void setTransportistaObservaciones(String transportistaObservaciones) {
		this.transportistaObservaciones = transportistaObservaciones;
	}

	public void setMetodopagoTransporte(String metodopagoTransporte) {
		this.metodopagoTransporte = metodopagoTransporte;
	}

	public void setFechaVencimientoDoc(Date fechaVencimientoDoc) {
		this.fechaVencimientoDoc = fechaVencimientoDoc;
	}

	public void setEmisorCodigoActividadEconomica(String emisorCodigoActividadEconomica) {
		this.emisorCodigoActividadEconomica = emisorCodigoActividadEconomica;
	}

	public void setEmisorNombreContacto(String emisorNombreContacto) {
		this.emisorNombreContacto = emisorNombreContacto;
	}

	public void setReceptorObservaciones(String receptorObservaciones) {
		this.receptorObservaciones = receptorObservaciones;
	}

	public void setReceptorTelefono(String receptorTelefono) {
		this.receptorTelefono = receptorTelefono;
	}

	public void setReceptorNombreContacto(String receptorNombreContacto) {
		this.receptorNombreContacto = receptorNombreContacto;
	}

	public void setReceptorRegistroMercantil(String receptorRegistroMercantil) {
		this.receptorRegistroMercantil = receptorRegistroMercantil;
	}

	public void setTransportistaRegimen(String transportistaRegimen) {
		this.transportistaRegimen = transportistaRegimen;
	}

	public void setTipoImpuestoEmisor(String tipoImpuestoEmisor) {
		this.tipoImpuestoEmisor = tipoImpuestoEmisor;
	}

	public void setTipoImpuestoReceptor(String tipoImpuestoReceptor) {
		this.tipoImpuestoReceptor = tipoImpuestoReceptor;
	}

	public void setTipoExtClaro(String tipoExtClaro) {
		this.tipoExtClaro = tipoExtClaro;
	}

	public void setNumeroCumplimiento(String numeroCumplimiento) {
		this.numeroCumplimiento = numeroCumplimiento;
	}

	public void setRedondeoPagable(BigDecimal redondeoPagable) {
		this.redondeoPagable = redondeoPagable;
	}

	public void setMonedaOrigenAlternativa(String monedaOrigenAlternativa) {
		this.monedaOrigenAlternativa = monedaOrigenAlternativa;
	}

	public void setMonedaDestinoAlternativa(String monedaDestinoAlternativa) {
		this.monedaDestinoAlternativa = monedaDestinoAlternativa;
	}

	public void setTasaCambioAlternativa(BigDecimal tasaCambioAlternativa) {
		this.tasaCambioAlternativa = tasaCambioAlternativa;
	}

	public void setFechaTasaCambioAlternativa(String fechaTasaCambioAlternativa) {
		this.fechaTasaCambioAlternativa = fechaTasaCambioAlternativa;
	}

	@Override
	public String toString() {
		return LABEL_CAB + "|" + getTipodoc() + "|" + getId() + "|" + getPrefijoRangoFolios() + "|" + getInicioRangoFolios() + "|" + getFinRangoFolios() + "|" + getClaveTecnica() + "|"
		        + getNumeroResolucion() + "|" + getFechaResolucion() + "|" + getFechaVigenciaInicio() + "|" + getFechaVigenciaFin() + "|" + getFechaEmision() + "|" + getHoraEmision() + "|"
		        + getTipoFactura() + "|" + getMoneda() + "|" + getTerminosPago() + "|" + getReferenciaFacturaId() + "|" + getReferenciaFacturaUUID() + "|" + getReferenciaFacturaFecha() + "|"
		        + getReferenciaFacturaCodigo() + "|" + getNumeroPedido() + "|" + getFechaPedido() + "|" + getNumeroAlbaran() + "|" + getFechaAlbaran() + "|" + getEmisorTipoPersona() + "|"
		        + getEmisorId() + "|" + getEmisorTipoIdentificacion() + "|" + getEmisorNombre() + "|" + getEmisorDireccion() + "|" + getEmisorCiudad() + "|" + getEmisorRegion() + "|"
		        + getEmisorDistrito() + "|" + getEmisorCodigoPostal() + "|" + getEmisorPais() + "|" + getEmisorRazonSocial() + "|" + getEmisorRegimen() + "|" + getReceptorTipoPersona() + "|"
		        + getReceptorId() + "|" + getReceptorTipoIdentificacion() + "|" + getReceptorNombre() + "|" + getReceptorRegion() + "|" + getReceptorDistrito() + "|" + getReceptorCiudad() + "|"
		        + getReceptorDireccion() + "|" + getReceptorCodigoPostal() + "|" + getReceptorPais() + "|" + getReceptorRegimen() + "|" + getReceptorRazonSocial() + "|" + getReceptorNombrePersona()
		        + "|" + getReceptorApellidoPersona() + "|" + getLugarEntregaDireccion() + "|" + getLugarEntregaPais() + "|" + getLugarEntregaCiudad() + "|" + getLugarEntregaCodigoPostal() + "|"
		        + getLugarEntregaCondiciones() + "|" + getLugarExpedicionDireccion() + "|" + getLugarExpedicionPais() + "|" + getLugarExpedicionCiudad() + "|" + getLugarExpedicionCodigoPostal() + "|"
		        + getTotalLineas() + "|" + getTotalBaseImponible() + "|" + getTotalFactura() + "|" + getNumeroCuentaBancaria() + "|" + getFechaVencimiento() + "|" + getCorreoElectronicoReceptor()
		        + "|" + getMonedaOrigen() + "|" + getMonedaDestino() + "|" + getTasaCambio() + "|" + getFechaTasaCambio() + "|" + getTotalDescuentos() + "|" + getTotalCargos() + "|"
		        + getValorAnticipo() + "|" + getFechaAnticipo() + "|" + getAvisoRecepcion() + "|" + getLugarEntregaEAN() + "|" + getEmisorRegistroMercantil() + "|" + getEmisorTelefono() + "|"
		        + getEmisorObservaciones() + "|" + getReceptorCodigoInterno() + "|" + getCodigoLTA() + "|" + getAmbiente() + "|" + getNumeroLineas() + "|" + getDvEmisor() + "|" + getDvReceptor() + "|"
		        + getFechaIniFacturacion() + "|" + getHoraIniFacturacion() + "|" + getFechaFinFacturacion() + "|" + getHoraFinFacturacion() + "|" + getIdAnticipo() + "|" + getFechaRecibidoAnticipo()
		        + "|" + getInstruccionesAnticipo() + "|" + getTotalBrutoTrib() + "|" + getLugarExpedicionRegion() + "|" + getLugarEntregaRegion() + "|" + getTotalAnticipos() + "|"
		        + getCodigoMunicipioEmisor() + "|" + getCodigoMunicipioLugarEntrega() + "|" + getCodigoMunicipioReceptor() + "|" + getCodigoMunicipioLugarExpedicion() + "|" + getTipoOperacion() + "|"
		        + getDescNatu() + "|" + getCodigoMunicipioFiscalEmisor() + "|" + getEmisorDistritoFiscal() + "|" + getEmisorCidudadFiscal() + "|" + getEmisorCodigoPostalFiscal() + "|"
		        + getEmisorRegionFiscal() + "|" + getEmisorDireccionFiscal() + "|" + getEmisorPaisFiscal() + "|" + getCodigoMunicipioFiscalReceptor() + "|" + getReceptorDistritoFiscal() + "|"
		        + getReceptorCiudadFiscal() + "|" + getReceptorCodigoPostalFiscal() + "|" + getReceptorRegionFiscal() + "|" + getReceptorDireccionFiscal() + "|" + getReceptorPaisFiscal() + "|"
		        + getFechaEntrega() + "|" + getHoraEntrega() + "|" + getTransportistaId() + "|" + getDvTransportista() + "|" + getTransportistaTipoIdentificacion() + "|" + getTransportistaNombre()
		        + "|" + getCodigoMunicipioTransportista() + "|" + getTransportistaDireccion() + "|" + getTransportistaCiudad() + "|" + getTransportistaRegion() + "|" + getTransportistaCodigoPostal()
		        + "|" + getTransportistaPais() + "|" + getTransportistaRazonSocial() + "|" + getCodigoMunicipioFiscalTransportista() + "|" + getTransportistaCiudadFiscal() + "|"
		        + getTransportistaCodigoPostalFiscal() + "|" + getTransportistaRegionFiscal() + "|" + getTransportistaDireccionFiscal() + "|" + getTransportistaPaisFiscal() + "|"
		        + getTransportistaRegistroMercantil() + "|" + getTransportistaTelefono() + "|" + getTransportistaEmail() + "|" + getTransportistaNombreContacto() + "|"
		        + getTransportistaObservaciones() + "|" + getMetodopagoTransporte() + "|" + getFechaVencimientoDoc() + "|" + getEmisorCodigoActividadEconomica() + "|" + getEmisorEmail() + "|"
		        + getEmisorNombreContacto() + "|" + getReceptorObservaciones() + "|" + getReceptorTelefono() + "|" + getReceptorNombreContacto() + "|" + getReceptorRegistroMercantil() + "|"
		        + getTransportistaRegimen() + "|" + getTipoImpuestoEmisor() + "|" + getTipoImpuestoReceptor() + "|" + getTipoExtClaro() + "|" + getNumeroCumplimiento() + "|" + getRedondeoPagable()
		        + "|" + getMonedaOrigenAlternativa() + "|" + getMonedaDestinoAlternativa() + "|" + getTasaCambioAlternativa() + "|" + getFechaTasaCambioAlternativa() + "|";
	}

}
