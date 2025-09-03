package com.comerzzia.iskaypet.pos.devices;

import com.comerzzia.api.loyalty.client.CouponsApi;
import com.comerzzia.api.loyalty.client.model.CouponDetail;
import com.comerzzia.api.loyalty.client.model.CouponLink;
import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoColectivoRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoContactosRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoEtiquetasRest;
import com.comerzzia.core.model.config.variables.ConfigVariableBean;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.config.configVariables.ServicioConfigVariablesImpl;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.IskaypetFidelizadoRest;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.response.IskaypetResponseGetFidelizadoRest;
import com.comerzzia.iskaypet.pos.persistence.colectivos.LocalColectivos;
import com.comerzzia.iskaypet.pos.persistence.colectivos.LocalColectivosKey;
import com.comerzzia.iskaypet.pos.persistence.colectivos.LocalColectivosMapper;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.IskaypetCustomerCouponDTO;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosExample;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosKey;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosMapper;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizados;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizadosExample;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.fidelizados.LocalFidelizadosMapper;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoCallback;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.ConsultaTarjetaFidelizadoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.IFidelizacion;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.fidelizacion.Fidelizacion;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.stage.Stage;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.FidelizacionService.convertApiToPos;
import static com.comerzzia.iskaypet.pos.services.cuponespuntos.CuponesPuntosService.COLECTIVOS_FIDELIZADOS;

@SuppressWarnings("deprecation")
public class IskaypetFidelizacion extends Fidelizacion implements IFidelizacion {

	private static final Logger log = Logger.getLogger(Fidelizacion.class.getName());

	public static String VARIABLE_INSTANCIAS_CARENCIA = "X_FIDELIZACION.INSTANCIA_CARENCIA_PUNTOS";

	public static final String VARIABLE_COLECTIVO_GLOVO = "GLOVO";
	public static final String VARIABLE_COLECTIVO_UBER = "UBER";

	protected static final String PARAMETRO_SI = "S";
	public static final String PARAMETRO_SALDO_NO_DISPONIBLE = "saldoNoDisponible";
	public static final String PARAMETRO_APLICA_CARENCIA = "aplicaCarencia";
	public static final String PARAMETRO_APLICA_FIDELIZACION = "aplicaFidelizacion";
	public static final String PARAMETRO_COD_LENGUAJE = "codLenguaje";
	public static final String PARAMETRO_EMAIL = "e-mail";
	public static final String PARAMETRO_TELEFONO = "telefono";
	public static final String PARAMETRO_MOVIL = "movil";

	// GAP147 - USO DE CUPONES DE INTEGRACION NAV EN POS
	public static final String CLASS_ID_COMPRA_MINIMA = "COMPRA_MINIMA";
	public static final String CLASS_ID_NAVISION_COUPON = "NAVISION_COUPON";
	public static final String CLASS_ID_PROMO_ORIGEN = "D_PROMOCIONES_CAB_TBL.ID_PROMOCION_ORIG";

	public static final String TIPO_CONTACTO_COD_EMAIL = "EMAIL";
	public static final String TIPO_CONTACTO_COD_TELEFONO = "TELEFONO1";
	public static final String TIPO_CONTACTO_COD_MOVIL = "MOVIL";
	public static final String TIPO_CONTACTO_EMAIL = "E-mail";
	public static final String TIPO_CONTACTO_TELEFONO = "Teléfono principal";
	public static final String TIPO_CONTACTO_MOVIL = "Móvil";

	@Override
	public FidelizacionBean consultarTarjetaFidelizado(Stage stage, String numTarjRegalo, String uidActividad) throws ConsultaTarjetaFidelizadoException {
		log.debug("consultarTarjetaFidelizado() - Consultando tarjeta de fidelizado con número " + numTarjRegalo);
		FidelizacionBean fidelizacionBean = consultarTarjetaFidelizado(stage, numTarjRegalo, uidActividad, null, null, null);
		log.debug("consultarTarjetaFidelizado() - Tarjeta de fidelizado consultada con éxito");
		return fidelizacionBean;
	}

	public static boolean isFidelizado(List<String> colectivos) {
		if (!colectivos.isEmpty()) {
			VariablesServices variablesServices = SpringContext.getBean(VariablesServices.class);
			String codcolectivos = variablesServices.getVariableAsString(COLECTIVOS_FIDELIZADOS);
			List<String> colectivosList = Arrays.asList(codcolectivos.split(";"));
			for (String colectivo : colectivos) {
				if (StringUtils.isNotBlank(codcolectivos)) {
					if (colectivosList.contains(colectivo)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Boolean aplicaCarencia() {
		try {

			ConfigVariableBean config = ServicioConfigVariablesImpl.get().consultar(VARIABLE_INSTANCIAS_CARENCIA);
			if (config != null && StringUtils.isNotBlank(config.getValorDefecto())) {
				String[] split = config.getValorDefecto().split(";");
				Sesion sesion = SpringContext.getBean(Sesion.class);
				if (sesion != null) {
					for (int i = 0; i < split.length; ++i) {
						if (sesion.getAplicacion().getUidInstancia().equals(split[i])) {
							return true;
						}
					}
				}

			}
		}
		catch (Exception e) {
			log.error("Ha ocurrido un error consultando la variable: " + VARIABLE_INSTANCIAS_CARENCIA);
		}
		return false;
	}

	// GAP147 - USO DE CUPONES DE INTEGRACION NAV EN POS
	@Override
	public void getCustomerCoupons(FidelizacionBean fidelizado) throws Exception {
		log.debug("getCustomerCoupons() - Querying coupons for customer " + fidelizado.getIdFidelizado());

		Sesion sesion = SpringContext.getBean(Sesion.class);
		ComerzziaApiManager apiManager = SpringContext.getBean(ComerzziaApiManager.class);

		DatosSesionBean datosSesion = new DatosSesionBean();
		datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
		datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
		datosSesion.setLocale(new Locale(sesion.getAplicacion().getStoreLanguageCode()));
		CouponsApi api = apiManager.getClient(datosSesion, "CouponsApi");

		Map<String, Object> params = new HashMap<>();
		params.put("active", true);
		params.put("used", false);
		params.put("valid", true);
		params.put("validInFuture", false);
		params.put("lockByTerminalId", sesion.getAplicacion().getCodAlmacen() + "-" + sesion.getAplicacion().getCodCaja());
		params.put("customerId", fidelizado.getIdFidelizado());
		params.put("includeAnonymousCoupons", false);

		List<CouponDetail> customerCoupons = api.getCustomerCoupons(fidelizado.getIdFidelizado(), params);

		List<CustomerCouponDTO> availableCoupons = new ArrayList<>();
		for (CouponDetail coupon : customerCoupons) {
			Promocion promotion = sesion.getSesionPromociones().getPromocionActiva(coupon.getPromotionId());
			if (promotion != null && promotion.isActiva()) {
				IskaypetCustomerCouponDTO customerCouponDTO = new IskaypetCustomerCouponDTO(coupon.getCouponCode(), false);
				BeanUtils.copyProperties(customerCouponDTO, coupon);
				availableCoupons.add(customerCouponDTO);
				for (CouponLink customerCouponDTO2 : coupon.getLinks()) {
					if (CLASS_ID_NAVISION_COUPON.equalsIgnoreCase(customerCouponDTO2.getClassId())) {
						customerCouponDTO.setCouponCodeNavision(customerCouponDTO2.getObjectId());
					}
					if (CLASS_ID_COMPRA_MINIMA.equalsIgnoreCase(customerCouponDTO2.getClassId())) {
						customerCouponDTO.setCompraMinima(customerCouponDTO2.getObjectId());
					}
                    if(CLASS_ID_PROMO_ORIGEN.equalsIgnoreCase(customerCouponDTO2.getClassId())) {
                        customerCouponDTO.setIdPromoOrigen(Long.parseLong(customerCouponDTO2.getObjectId()));
                    }
				}
			}
			else {
				log.warn("getCustomerCoupons() - No se ha podido añadir el cupón con código " + coupon.getCouponCode() + " al no estar activa la promoción " + coupon.getPromotion().getPromotionId()
				        + " - " + coupon.getPromotion().getDescription());
			}

		}

		fidelizado.setAvailableCoupons(availableCoupons);
		fidelizado.setActiveCoupons(new ArrayList<>());
	}

	// Tarea personalizada que sobreescribe 'failed()' para omitir la ventana emergente anterior
	public class IskaypetFidelizacionTask extends FidelizacionTask {

		private final Stage localStage;
		private final DispositivoCallback<FidelizacionBean> localCallback;
		private final String localNumTarjeta;

		public IskaypetFidelizacionTask(String numTarjeta, DispositivoCallback<FidelizacionBean> callback, Stage stage) {
			super(numTarjeta, callback, stage);
			this.localCallback = callback;
			this.localStage = stage;
			this.localNumTarjeta = numTarjeta;
		}

        @Override
		protected void failed() {
			Throwable e = getException();
			if (e != null) {
				log.error("IskaypetFidelizacionTask - Offline or server error: " + e.getMessage(), e);
				// si es un error de conectividad => no mostramos la ventana emergente anterior
				// en su lugar, llamamos a callback.onFailure(e) para permitir que el código principal se encargue de la situación "offline"
				localCallback.onFailure(e);
				return;
			}
			FidelizacionBean fidelizacionBean = new FidelizacionBean();
			fidelizacionBean.setNumTarjetaFidelizado(localNumTarjeta);
			localCallback.onSuccess(fidelizacionBean);
		}
	}

	public FidelizacionBean consultarTarjetaFidelizado(Stage stage, String numTarjRegalo, String uidActividad, LocalFidelizadosMapper localFidelizadosMapper, LocalFidelizadosColectivosMapper localFidelizadosColectivosMapper, LocalColectivosMapper localColectivosMapper) throws ConsultaTarjetaFidelizadoException {
		try {
			ConsultarFidelizadoRequestRest paramConsulta = new ConsultarFidelizadoRequestRest(this.wsApiKey, uidActividad);
			paramConsulta.setNumeroTarjeta(numTarjRegalo);
			IskaypetResponseGetFidelizadoRest result = IskaypetFidelizadoRest.getFidelizado(paramConsulta);

			FidelizacionBean tarjetaFidelizacion = new FidelizacionBean();
			tarjetaFidelizacion.setIdFidelizado(result.getIdFidelizado());
			tarjetaFidelizacion.setNumTarjetaFidelizado(result.getNumeroTarjeta());
			tarjetaFidelizacion.setBaja(PARAMETRO_SI.equals(result.getBaja()));
			tarjetaFidelizacion.setActiva(PARAMETRO_SI.equals(result.getActiva()));
			tarjetaFidelizacion.setSaldo(BigDecimal.valueOf(result.getSaldo()));
			tarjetaFidelizacion.setSaldoProvisional(BigDecimal.valueOf(result.getSaldoProvisional()));
			tarjetaFidelizacion.setNombre(result.getNombre());
			tarjetaFidelizacion.setApellido(result.getApellidos());
			tarjetaFidelizacion.setCodTipoIden(result.getCodTipoIden());
			tarjetaFidelizacion.setCp(result.getCp());
			tarjetaFidelizacion.setDocumento(result.getDocumento());
			tarjetaFidelizacion.setDomicilio(result.getDomicilio());
			tarjetaFidelizacion.setLocalidad(result.getLocalidad());
			tarjetaFidelizacion.setPoblacion(result.getPoblacion());
			tarjetaFidelizacion.setProvincia(result.getProvincia());
			tarjetaFidelizacion.setCodPais(result.getCodPais());
			tarjetaFidelizacion.setDesPais(result.getDesPais());
			tarjetaFidelizacion.setPaperLess(result.getPaperLess());

			tarjetaFidelizacion.putAdicional(PARAMETRO_SALDO_NO_DISPONIBLE, (result.getSaldoNoDisponible() != null ? BigDecimal.valueOf(result.getSaldoNoDisponible()) : BigDecimal.ZERO));
			tarjetaFidelizacion.putAdicional(PARAMETRO_APLICA_CARENCIA, aplicaCarencia());

			List<String> colectivos = result.getColectivos() != null && !result.getColectivos().isEmpty()
					? result.getColectivos().stream().map(ResponseGetFidelizadoColectivoRest::getCodigo).collect(Collectors.toList())
					: Collections.emptyList();
			tarjetaFidelizacion.setCodColectivos(colectivos);


			// Si es glovo no se muestra la fidelización
			Boolean contieneGlovo = tarjetaFidelizacion.getCodColectivos().contains(VARIABLE_COLECTIVO_GLOVO);
			Boolean contieneUber = tarjetaFidelizacion.getCodColectivos().contains(VARIABLE_COLECTIVO_UBER);

			if (contieneGlovo) {
				tarjetaFidelizacion.putAdicional(VARIABLE_COLECTIVO_GLOVO, Boolean.TRUE);
			}
			if (contieneUber) {
				tarjetaFidelizacion.putAdicional(VARIABLE_COLECTIVO_UBER, Boolean.TRUE);
			}

			// Si tiene colectivos, se aplica mostrar la fidelización
			tarjetaFidelizacion.putAdicional(PARAMETRO_APLICA_FIDELIZACION, isFidelizado(tarjetaFidelizacion.getCodColectivos()));

			List<String> etiquetas = result.getEtiquetas() != null && !result.getEtiquetas().isEmpty()
					? result.getEtiquetas().stream().map(ResponseGetFidelizadoEtiquetasRest::getUidEtiqueta).collect(Collectors.toList())
					: Collections.emptyList();
			tarjetaFidelizacion.setUidEtiquetas(etiquetas);

			// Adicionamos los datos de contacto
			if (result.getTiposContactos() != null && !result.getTiposContactos().isEmpty()) {
				for (ResponseGetFidelizadoContactosRest contacto : result.getTiposContactos()) {
					log.debug("consultarTarjetaFidelizado() - Contacto: " + contacto.getDescripcion() + " - " + contacto.getValor());
					switch (contacto.getDescripcion()) {
						case TIPO_CONTACTO_MOVIL:
							tarjetaFidelizacion.putAdicional(PARAMETRO_MOVIL, contacto.getValor());
							break;
						case TIPO_CONTACTO_TELEFONO:
							tarjetaFidelizacion.putAdicional(PARAMETRO_TELEFONO, contacto.getValor());
							break;
						case TIPO_CONTACTO_EMAIL:
							tarjetaFidelizacion.putAdicional(PARAMETRO_EMAIL, contacto.getValor());
							break;
						default:
							break;
					}
				}
			}

			SesionPromociones promotionSession = SpringContext.getBean(SesionPromociones.class);
			if (promotionSession.isLoadedLoyaltyModule()) {
				try {
					this.getCustomerCoupons(tarjetaFidelizacion);
				}
				catch (Exception var12) {
					String msg = "El fidelizado se ha consultado correctamente, pero ha ocurrido un error cargando sus cupones disponibles. ";
					log.error(msg, var12);
					if (stage != null) {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(msg + "Consulte con el administrador."), stage);
					}
				}
			}

			return tarjetaFidelizacion;
		} catch (RestException e) {
			// Offline: si falla la llamada REST, buscamos el fidelizado localmente
			log.warn("Llamada REST ha fallado en consultarTarjetaFidelizado(), buscando fidelizado en DB local con numTarjeta: " + numTarjRegalo);
			try {
				FidelizadoBean localFidelizado = buscarFidelizadoOfflinePorNumeroTarjeta(numTarjRegalo, localFidelizadosMapper, localFidelizadosColectivosMapper, localColectivosMapper);
				if (localFidelizado == null) {
					throw new ConsultaTarjetaFidelizadoException("No se ha encontrado fidelizado offline para tarjeta: " + numTarjRegalo);
				}
				// Convertir FidelizadoBean a FidelizacionBean
				return convertApiToPos(localFidelizado);
			} catch (Exception ex) {
				throw new ConsultaTarjetaFidelizadoException("Error al realizar la búsqueda offline para tarjeta: " + numTarjRegalo, ex);
			}
		} catch (Exception e) {
			throw new ConsultaTarjetaFidelizadoException(e);
		}

	}

	private FidelizadoBean buscarFidelizadoOfflinePorNumeroTarjeta(String numeroTarjeta,
																   LocalFidelizadosMapper localFidelizadosMapper,
																   LocalFidelizadosColectivosMapper localFidelizadosColectivosMapper,
																   LocalColectivosMapper localColectivosMapper) {

		log.debug("buscarFidelizadoOfflinePorNumeroTarjeta() - Buscando en local DB con NUMERO_TARJETA: " + numeroTarjeta);

		if (org.apache.commons.lang3.StringUtils.isBlank(numeroTarjeta)) {
			log.warn("buscarFidelizadoOfflinePorNumeroTarjeta() - numeroTarjeta es null o está vacío.");
			return null;
		}

		LocalFidelizadosExample example = new LocalFidelizadosExample();
		LocalFidelizadosExample.Criteria criteria = example.createCriteria();

		criteria.andNumeroTarjetaEqualTo(numeroTarjeta);

		List<LocalFidelizados> results = localFidelizadosMapper.selectByExample(example);

		if (results.isEmpty()) {
			log.debug("buscarFidelizadoOfflinePorNumeroTarjeta() - No se ha encontrado ningún registro con NUMERO_TARJETA: " + numeroTarjeta);
			return null;
		}

		if (results.size() > 1) {
			log.warn("buscarFidelizadoOfflinePorNumeroTarjeta() - Se han encontrado múltiples registros con NUMERO_TARJETA: " + numeroTarjeta);
		}

		LocalFidelizados localFidelizado = results.get(0);
		FidelizadoBean fidelizadoBean = convertirLocalFidelizadosAFidelizadoBean(localFidelizado);

		List<ColectivosFidelizadoBean> colectivos = CargarColectivosDesdeLocal(
				localFidelizado.getUidInstancia(),
				localFidelizado.getIdFidelizado(),
				localColectivosMapper,
				localFidelizadosColectivosMapper
		);
		fidelizadoBean.setColectivos(colectivos);

		return fidelizadoBean;
	}

	private FidelizadoBean convertirLocalFidelizadosAFidelizadoBean(LocalFidelizados local) {
		FidelizadoBean bean = new FidelizadoBean();

		bean.setIdFidelizado(local.getIdFidelizado());
		bean.setNumeroTarjeta(local.getNumeroTarjeta());
		bean.setNombre(local.getNombre());
		bean.setApellidos(local.getApellidos());
		bean.setDomicilio(local.getDomicilio());
		bean.setLocalidad(local.getLocalidad());
		bean.setProvincia(local.getProvincia());
		bean.setCp(local.getCp());
		bean.setCodPais(local.getCodpais());
		bean.setCodTipoIden(local.getCodtipoiden());
		bean.setDocumento(local.getDocumento());
		bean.setFechaAlta(local.getFechaAlta());
		bean.setFechaModificacion(local.getFechaModificacion());
		bean.setCodlengua(local.getCodlengua());
		bean.setCodFidelizado(local.getCodfidelizado());

		// agregar telefono y email
		if (org.apache.commons.lang3.StringUtils.isNotBlank(local.getTelefono())) {
			TiposContactoFidelizadoBean telefono = new TiposContactoFidelizadoBean();
			telefono.setCodTipoCon("TELEFONO1");
			telefono.setValor(local.getTelefono());
			bean.getContactos().add(telefono);
		}

		if (org.apache.commons.lang3.StringUtils.isNotBlank(local.getEmail())) {
			TiposContactoFidelizadoBean email = new TiposContactoFidelizadoBean();
			email.setCodTipoCon("EMAIL");
			email.setValor(local.getEmail());
			bean.getContactos().add(email);
		}

		return bean;
	}

	private List<ColectivosFidelizadoBean> CargarColectivosDesdeLocal(String uidInstancia,
																	  Long idFidelizado,
																	  LocalColectivosMapper localColectivosMapper,
																	  LocalFidelizadosColectivosMapper localFidelizadosColectivosMapper) {

		LocalFidelizadosColectivosExample example = new LocalFidelizadosColectivosExample();
		LocalFidelizadosColectivosExample.Criteria cr = example.createCriteria();
		cr.andUidInstanciaEqualTo(uidInstancia);
		cr.andIdFidelizadoEqualTo(idFidelizado);

		List<LocalFidelizadosColectivosKey> bridgingRows = localFidelizadosColectivosMapper.selectByExample(example);

		List<ColectivosFidelizadoBean> colectivos = new ArrayList<>();
		for (LocalFidelizadosColectivosKey row : bridgingRows) {
			LocalColectivosKey key = new LocalColectivosKey();
			key.setUidInstancia(row.getUidInstancia());
			key.setCodColectivo(row.getCodColectivo());

			LocalColectivos localColectivo = localColectivosMapper.selectByPrimaryKey(key);

			ColectivosFidelizadoBean colectivoBean = new ColectivosFidelizadoBean();
			colectivoBean.setCodColectivo(row.getCodColectivo());

			if (localColectivo != null) {
				colectivoBean.setDesColectivo(localColectivo.getDesColectivo());
				colectivoBean.setCodtipcolectivo(localColectivo.getCodtipcolectivo());
				colectivoBean.setDestipcolectivo(localColectivo.getDestipcolectivo());
			}

			colectivos.add(colectivoBean);
		}

		return colectivos;
	}
}
