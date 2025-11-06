package com.comerzzia.dinosol.pos.services.ticket.cabecera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comarch.clm.partner.dto.IssuanceResponse;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.pos.devices.fidelizacion.DinoFidelizacion;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasTicketBean;
import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;
import com.comerzzia.dinosol.pos.services.encuestas.EncuestaTicket;
import com.comerzzia.dinosol.pos.services.payments.methods.prefijos.PrefijosTarjetasService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.bp.BPManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.descuentosespeciales.DescuentosEspecialesManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.DescuentosEmpleadoManager;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.opciones.OpcionPromocionesSeleccionadaDto;
import com.comerzzia.dinosol.pos.services.ticket.pagos.sipay.InfoSipayTransaction;
import com.comerzzia.dinosol.pos.services.ventas.reparto.dto.ServicioRepartoDto;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;

@Component
@Primary
@Scope("prototype")
@XmlSeeAlso({ IssuanceResponse.class, HashMap.class })
public class DinoCabeceraTicket extends CabeceraTicket {

	@XmlTransient
	private Logger log = Logger.getLogger(DinoCabeceraTicket.class);

	@XmlElementWrapper
	@XmlElement(name = "tarjetaIdentificacion")
	private List<TarjetaIdentificacionDto> tarjetasIdentificacion;

	@XmlElement(name = "datosSad")
	private RutasTicketBean datosSad;

	@XmlTransient
	private List<String> numeroTarjetas;

	@XmlTransient
	private boolean recuperadoTicketOrigenCentral;

	private String codigoParking;

	private String horaSalidaParking;

	private boolean codigoParkingFormatoQr;

	@XmlTransient
	private boolean generarQrParking;

	@XmlTransient
	@Autowired
	private PrefijosTarjetasService prefijosTarjetasService;

	@XmlTransient
	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	@XmlTransient
	private String nifBusquedaEnvioDomicilio;

	@XmlTransient
	private String nombreBusquedaEnvioDomicilio;

	@XmlTransient
	private DomicilioResponse domicilioResponseBusquedaEnvioDomicilio;

	@XmlElement
	private List<CustomerCouponDTO> cuponesLeidos;

	private Integer numRascasEntregados;

	@XmlElementWrapper
	@XmlElement(name = "encuesta")
	private List<EncuestaTicket> encuestasHechas;
	
	@XmlElement(name = "opcion_promo_seleccionada")
	private OpcionPromocionesSeleccionadaDto opcionPromocionesSeleccionada;
	
	@XmlTransient
	private ServicioRepartoDto servicioRepartoDto;
	
	private String servicioReparto;
	
	@XmlElement(name = "infoTransactionSipay")
	protected InfoSipayTransaction transactionsSipay;

	public List<TarjetaIdentificacionDto> getTarjetasIdentificacion() {
		if (this.tarjetasIdentificacion == null) {
			this.tarjetasIdentificacion = new ArrayList<TarjetaIdentificacionDto>();
		}
		return tarjetasIdentificacion;
	}

	public RutasTicketBean getDatosSad() {
		return datosSad;
	}

	public void setDatosSad(RutasTicketBean datosSad) {
		this.datosSad = datosSad;
	}

	public void setTarjetasIdentificacion(List<TarjetaIdentificacionDto> tarjetasIdentificacion) {
		this.tarjetasIdentificacion = tarjetasIdentificacion;

		this.numeroTarjetas = new ArrayList<String>();
		if (tarjetasIdentificacion != null) {
			for (TarjetaIdentificacionDto tarjeta : tarjetasIdentificacion) {
				this.numeroTarjetas.add(tarjeta.getNumeroTarjeta());
			}
		}
	}

	public void addTarjetaIdentificacion(TarjetaIdentificacionDto tarjetaIdentificacion) {
		if (this.tarjetasIdentificacion == null) {
			this.tarjetasIdentificacion = new ArrayList<TarjetaIdentificacionDto>();
			this.numeroTarjetas = new ArrayList<String>();
		}

		if (StringUtils.isNotBlank(tarjetaIdentificacion.getTipoTarjeta()) && tarjetaIdentificacion.getTipoTarjeta().equals("BP")) {
			String numeroTarjetaBp = transformarCodigoTarjetaBp(tarjetaIdentificacion.getNumeroTarjeta());
			tarjetaIdentificacion.setNumeroTarjeta(numeroTarjetaBp);
		}

		if (containsTarjeta(tarjetaIdentificacion.getNumeroTarjeta())) {
			return;
		}

		TarjetaIdentificacionDto tarjetaIdentificacionTipo = buscarTarjeta("BP");
		if (tarjetaIdentificacionTipo != null) {
			removeTarjetaIdentificacion(tarjetaIdentificacion);
		}

		this.tarjetasIdentificacion.add(tarjetaIdentificacion);
		
		if(this.numeroTarjetas == null) {
			this.numeroTarjetas = new ArrayList<String>();
		}
		this.numeroTarjetas.add(tarjetaIdentificacion.getNumeroTarjeta());
	}

	private String transformarCodigoTarjetaBp(String numeroTarjeta) {
		try {
			if (numeroTarjeta.startsWith("705690")) {
				numeroTarjeta = "002" + numeroTarjeta.substring(6, numeroTarjeta.length() - 1);
				numeroTarjeta = numeroTarjeta + new EAN13CheckDigit().calculate(numeroTarjeta);
			}
			else if (numeroTarjeta.startsWith("242")) {
				numeroTarjeta = "002" + numeroTarjeta.substring(3, numeroTarjeta.length() - 1);
				numeroTarjeta = numeroTarjeta + new EAN13CheckDigit().calculate(numeroTarjeta);
			}
		}
		catch (Exception e) {
			log.error("transformarCodigoTarjeta() - Ha habido un error al generar el número de tarjeta correcto de BP: " + e.getMessage(), e);
		}

		return numeroTarjeta;
	}

	@SuppressWarnings("unlikely-arg-type")
	public boolean containsTarjeta(String tarjetaIdentificacion) {
		if (this.tarjetasIdentificacion == null) {
			this.tarjetasIdentificacion = new ArrayList<TarjetaIdentificacionDto>();
			this.numeroTarjetas = new ArrayList<String>();
		}
		return tarjetasIdentificacion.contains(tarjetaIdentificacion);
	}

	public TarjetaIdentificacionDto buscarTarjeta(String tipo) {
		if (tarjetasIdentificacion != null) {
			for (TarjetaIdentificacionDto tarjeta : tarjetasIdentificacion) {
				if (StringUtils.isNotBlank(tarjeta.getTipoTarjeta()) && tarjeta.getTipoTarjeta().equals(tipo)) {
					return tarjeta;
				}
			}
		}
		return null;
	}

	public void removeTarjetaIdentificacion(TarjetaIdentificacionDto tarjetaIdentificacion) {
		Iterator<TarjetaIdentificacionDto> it = tarjetasIdentificacion.iterator();
		while (it.hasNext()) {
			TarjetaIdentificacionDto tarjeta = it.next();
			if (tarjeta.getTipoTarjeta().equals(tarjetaIdentificacion.getTipoTarjeta())) {
				numeroTarjetas.remove(tarjeta.getNumeroTarjeta());
				it.remove();
			}
		}
	}

	public void removeTarjetaIdentificacion(String numeroTarjeta) {
		Iterator<TarjetaIdentificacionDto> it = tarjetasIdentificacion.iterator();
		while (it.hasNext()) {
			TarjetaIdentificacionDto tarjeta = it.next();
			if (tarjeta.getNumeroTarjeta().equals(numeroTarjeta)) {
				numeroTarjetas.remove(tarjeta.getNumeroTarjeta());
				it.remove();
			}
		}
	}

	public boolean isRecuperadoTicketOrigenCentral() {
		return recuperadoTicketOrigenCentral;
	}

	public void setRecuperadoTicketOrigenCentral(boolean recuperadoTicketOrigenCentral) {
		this.recuperadoTicketOrigenCentral = recuperadoTicketOrigenCentral;
	}

	public String getCodigoParking() {
		return codigoParking;
	}

	public void setCodigoParking(String codigoParking) {
		this.codigoParking = codigoParking;
	}

	public String getHoraSalidaParking() {
		return horaSalidaParking;
	}

	public void setHoraSalidaParking(String horaSalidaParking) {
		this.horaSalidaParking = horaSalidaParking;
	}

	public boolean isGenerarQrParking() {
		return generarQrParking;
	}

	public void setGenerarQrParking(boolean generarQrParking) {
		this.generarQrParking = generarQrParking;
	}

	public boolean isCodigoParkingFormatoQr() {
		return codigoParkingFormatoQr;
	}

	public void setCodigoParkingFormatoQr(boolean codigoParkingFormatoQr) {
		this.codigoParkingFormatoQr = codigoParkingFormatoQr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDatosFidelizado(FidelizacionBean tarjeta) {
		super.setDatosFidelizado(tarjeta);

		this.tarjetasIdentificacion = new ArrayList<TarjetaIdentificacionDto>();
		this.numeroTarjetas = new ArrayList<String>();

		if (tarjeta != null && tarjeta.getAdicionales() != null && tarjeta.getAdicionales().containsKey(DinoFidelizacion.FIDELIZADO_TARJETAS)) {
			List<String> tarjetas = (List<String>) tarjeta.getAdicionales().get(DinoFidelizacion.FIDELIZADO_TARJETAS);
			if (tarjetas != null && !tarjetas.isEmpty()) {
				for (String numTarjeta : tarjetas) {
					try {
						String medioPagoPrefijo = prefijosTarjetasService.getMedioPagoPrefijo(numTarjeta);

						if (StringUtils.isNotBlank(medioPagoPrefijo)) {
							TarjetaIdentificacionDto tarjetaIdentificacionDto = new TarjetaIdentificacionDto();
							tarjetaIdentificacionDto.setNumeroTarjeta(numTarjeta);
							for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
								String controlClass = configuration.getControlClass();
								if (StringUtils.isNotBlank(controlClass)) {
									PaymentMethodManager manager = configuration.getManager();
									String paymentCode = configuration.getPaymentCode();

									if (StringUtils.isNotBlank(controlClass) && manager instanceof BPManager && paymentCode.equals(medioPagoPrefijo)) {
										tarjetaIdentificacionDto.setTipoTarjeta("BP");
									}
									else {
										boolean esDescuentoEmpleado = manager instanceof DescuentosEspecialesManager || manager instanceof DescuentosEmpleadoManager;
										if (StringUtils.isNotBlank(controlClass) && esDescuentoEmpleado && paymentCode.equals(medioPagoPrefijo)) {
											tarjetaIdentificacionDto.setTipoTarjeta("EMP");
										}
									}
								}
							}

							if (StringUtils.isNotBlank(tarjetaIdentificacionDto.getTipoTarjeta())) {
								addTarjetaIdentificacion(tarjetaIdentificacionDto);
							}
						}
					}
					catch (Exception e) {
						log.error("setDatosFidelizado() - Ha habido un error al añadir la tarjeta " + numTarjeta + ": " + e.getMessage(), e);
					}
				}
			}
		}
	}

	public String getNifBusquedaEnvioDomicilio() {
		return nifBusquedaEnvioDomicilio;
	}

	public void setNifBusquedaEnvioDomicilio(String nifBusquedaEnvioDomicilio) {
		this.nifBusquedaEnvioDomicilio = nifBusquedaEnvioDomicilio;
	}

	public String getNombreBusquedaEnvioDomicilio() {
		return nombreBusquedaEnvioDomicilio;
	}

	public void setNombreBusquedaEnvioDomicilio(String nombreBusquedaEnvioDomicilio) {
		this.nombreBusquedaEnvioDomicilio = nombreBusquedaEnvioDomicilio;
	}

	public DomicilioResponse getDomicilioResponseBusquedaEnvioDomicilio() {
		return domicilioResponseBusquedaEnvioDomicilio;
	}

	public void setDomicilioResponseBusquedaEnvioDomicilio(DomicilioResponse domicilioResponseBusquedaEnvioDomicilio) {
		this.domicilioResponseBusquedaEnvioDomicilio = domicilioResponseBusquedaEnvioDomicilio;
	}

	public Integer getNumRascasEntregados() {
		return numRascasEntregados;
	}

	public void setNumRascasEntregados(Integer numRascasEntregados) {
		this.numRascasEntregados = numRascasEntregados;
	}

	public List<CustomerCouponDTO> getCuponesLeidos() {
		return cuponesLeidos;
	}

	public void setCuponesLeidos(List<CustomerCouponDTO> cuponesLeidos) {
		this.cuponesLeidos = cuponesLeidos;
	}

	public List<EncuestaTicket> getEncuestasHechas() {
		return encuestasHechas;
	}

	public void setEncuestasHechas(List<EncuestaTicket> encuestasHechas) {
		this.encuestasHechas = encuestasHechas;
	}

	public OpcionPromocionesSeleccionadaDto getOpcionPromocionesSeleccionada() {
		return opcionPromocionesSeleccionada;
	}

	public void setOpcionPromocionesSeleccionada(OpcionPromocionesSeleccionadaDto opcionPromocionesSeleccionada) {
		this.opcionPromocionesSeleccionada = opcionPromocionesSeleccionada;
	}

	public ServicioRepartoDto getServicioRepartoDto() {
		return servicioRepartoDto;
	}

	public void setServicioRepartoDto(ServicioRepartoDto servicioRepartoDto) {
		this.servicioRepartoDto = servicioRepartoDto;
		if(servicioRepartoDto != null) {
			this.servicioReparto = servicioRepartoDto.getNombre();
			
			FidelizacionBean fidelizacionBean = new FidelizacionBean();
    		fidelizacionBean.setNumTarjetaFidelizado(servicioRepartoDto.getTarjetaFidelizado());
    		setDatosFidelizado(fidelizacionBean);
		}
	}

	public String getServicioReparto() {
		return servicioReparto;
	}

	
	public InfoSipayTransaction getTransactionsSipay() {
		return transactionsSipay;
	}

	
	public void setTransactionsSipay(InfoSipayTransaction transactionsSipay) {
		this.transactionsSipay = transactionsSipay;
	}

	
}
