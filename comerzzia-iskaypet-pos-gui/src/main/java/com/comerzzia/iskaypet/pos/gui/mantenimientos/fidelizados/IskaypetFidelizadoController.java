package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ContextoValidadorCodigoPostal;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostal;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostalException;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostalFactory;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.EnlaceFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.model.loyalty.SolicitudBean;
import com.comerzzia.api.model.loyalty.TarjetaBean;
import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.iskaypet.pos.core.gui.validation.IskaypetValidationUI;
import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.IskaypetFormularioFidelizadoBean;
import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.IskaypetPaneDatosGeneralesController;
import com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje.LenguajeNotFoundException;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.IskaypetFacturacionArticulosController;
import com.comerzzia.iskaypet.pos.services.lenguajes.LenguajesService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.view.View;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.FormularioBusquedaFidelizadoBean;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos.ColectivoAyudaGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas.TiendaGui;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosView;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class IskaypetFidelizadoController extends FidelizadoController {

	private static final String APIKEY = "WEBSERVICES.APIKEY";

	@Autowired
	private Sesion sesion;

	@Autowired
	private PaisService paisService;
	
	@Autowired
	private LenguajesService lenguajesService;

	@Autowired
	private VariablesServices variablesServices;

	protected IskaypetFormularioFidelizadoBean iskaypetFormFidelizado;

	private static final String INSERCION = "INSERCION";
	
	protected boolean borrarTelefonoFijo;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);
		iskaypetFormFidelizado = new IskaypetFormularioFidelizadoBean();
		iskaypetFormFidelizado.setFormField("codigo", paneDatosGeneralesController.getTfCodigo());
		iskaypetFormFidelizado.setFormField("numeroTarjeta", paneDatosGeneralesController.getTfNumeroTarjeta());
		iskaypetFormFidelizado.setFormField("nombre", paneDatosGeneralesController.getTfNombre());
		iskaypetFormFidelizado.setFormField("apellidos", paneDatosGeneralesController.getTfApellidos());
		iskaypetFormFidelizado.setFormField("tipoDocumento", paneDatosGeneralesController.getCbTipoDocumento());
		iskaypetFormFidelizado.setFormField("documento", paneDatosGeneralesController.getTfDocumento());
		iskaypetFormFidelizado.setFormField("sexo", paneDatosGeneralesController.getCbSexo());
		iskaypetFormFidelizado.setFormField("estadoCivil", paneDatosGeneralesController.getCbEstadoCivil());
		iskaypetFormFidelizado.setFormField("fechaNacimiento", paneDatosGeneralesController.getDpFechaNacimiento());
		iskaypetFormFidelizado.setFormField("email", paneDatosGeneralesController.getTfEmail());
		iskaypetFormFidelizado.setFormField("movil", paneDatosGeneralesController.getTfMovil());
		iskaypetFormFidelizado.setFormField("codPais", paneDatosGeneralesController.getTfCodPais());
		iskaypetFormFidelizado.setFormField("desPais", paneDatosGeneralesController.getTfDesPais());
		iskaypetFormFidelizado.setFormField("cp", paneDatosGeneralesController.getTfCodPostal());
		iskaypetFormFidelizado.setFormField("provincia", paneDatosGeneralesController.getTfProvincia());
		iskaypetFormFidelizado.setFormField("poblacion", paneDatosGeneralesController.getTfPoblacion());
		iskaypetFormFidelizado.setFormField("localidad", paneDatosGeneralesController.getTfLocalidad());
		iskaypetFormFidelizado.setFormField("domicilio", paneDatosGeneralesController.getTfDomicilio());
		iskaypetFormFidelizado.setFormField("codAlmFav", paneDatosGeneralesController.getTfCodTienda());
		iskaypetFormFidelizado.setFormField("desAlmFav", paneDatosGeneralesController.getTfDesTienda());
		iskaypetFormFidelizado.setFormField("codColectivo", paneDatosGeneralesController.getTfCodColectivo());
		iskaypetFormFidelizado.setFormField("desColectivo", paneDatosGeneralesController.getTfDesColectivo());
		iskaypetFormFidelizado.setFormField("observaciones", paneObservacionesController.getTaObservaciones());

		if (paneDatosGeneralesController instanceof IskaypetPaneDatosGeneralesController) {
			iskaypetFormFidelizado.setFormField("emailconfirm", ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfEmailConfirm());
			iskaypetFormFidelizado.setFormField("codLenguaje", ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfCodLenguaje());
			iskaypetFormFidelizado.setFormField("desLenguaje", ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfDesLenguaje());
			iskaypetFormFidelizado.setFormField("telefonoFijo", ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfTelefonoFijo());
		}

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		iskaypetFormFidelizado.limpiarFormulario();
		iskaypetFormFidelizado.clearErrorStyle();
		lbError.setText("");
		super.initializeForm();
		btImprimir.setVisible(false);
		btImprimir.setManaged(false);

		btImprimir.visibleProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				btImprimir.setVisible(false);
			}
		});

	}

	@Override
	public void accionCancelar() {
		super.accionCancelar();
		iskaypetFormFidelizado.clearErrorStyle();
	}

	@Override
	protected FidelizadoBean getDatosFidelizado(String modo) {
		FidelizadoBean fidelizado = new FidelizadoBean();
		if (paneDatosGeneralesController.getCbEstadoCivil().getValue() != null) {
			String codestcivil = paneDatosGeneralesController.getCbEstadoCivil().getValue().getCodEstadoCivil();
			String desestcivil = paneDatosGeneralesController.getCbEstadoCivil().getValue().getDesEstadoCivil();
			fidelizado.setCodEstCivil(codestcivil);
			fidelizado.setDesEstCivil(desestcivil);
		}
		if (paneDatosGeneralesController.getCbSexo().getValue() != null) {
			String sexo = paneDatosGeneralesController.getCbSexo().getValue().getCodigo();
			fidelizado.setSexo(sexo);
		}
		if (paneDatosGeneralesController.getCbTipoDocumento().getValue() != null) {
			String codtipoiden = paneDatosGeneralesController.getCbTipoDocumento().getValue().getCodigo();
			fidelizado.setCodTipoIden(codtipoiden);
		}
		Boolean notifEmail = paneDatosGeneralesController.getChNotifEmail().isSelected();
		Boolean notifMovil = paneDatosGeneralesController.getChNotifMovil().isSelected();
		Boolean paperLess = paneDatosGeneralesController.getChPaperLess().isSelected();
		Boolean isFidelizado = true;
		if (paneDatosGeneralesController instanceof IskaypetPaneDatosGeneralesController) {
			isFidelizado = ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getcBoxFidelizado().isSelected();
		}
		Date fechaNacimiento = paneDatosGeneralesController.getDpFechaNacimiento().getSelectedDate();
		String apellidos = paneDatosGeneralesController.getTfApellidos().getText();
		String codcolectivo = !"".equals(paneDatosGeneralesController.getTfCodColectivo().getText()) ? paneDatosGeneralesController.getTfCodColectivo().getText() : null;
		String descolectivo = !"".equals(paneDatosGeneralesController.getTfDesColectivo().getText()) ? paneDatosGeneralesController.getTfDesColectivo().getText() : null;
		String codfidelizado = !"".equals(paneDatosGeneralesController.getTfCodigo().getText()) ? paneDatosGeneralesController.getTfCodigo().getText() : null;
		String codpais = !"".equals(paneDatosGeneralesController.getTfCodPais().getText()) ? paneDatosGeneralesController.getTfCodPais().getText() : null;
		String codpostal = !"".equals(paneDatosGeneralesController.getTfCodPostal().getText()) ? paneDatosGeneralesController.getTfCodPostal().getText() : null;
		String codalmFav = !"".equals(paneDatosGeneralesController.getTfCodTienda().getText()) ? paneDatosGeneralesController.getTfCodTienda().getText() : null;
		String despais = !"".equals(paneDatosGeneralesController.getTfDesPais().getText()) ? paneDatosGeneralesController.getTfDesPais().getText() : null;
		String documento = !"".equals(paneDatosGeneralesController.getTfDocumento().getText()) ? paneDatosGeneralesController.getTfDocumento().getText() : null;
		String domicilio = !"".equals(paneDatosGeneralesController.getTfDomicilio().getText()) ? paneDatosGeneralesController.getTfDomicilio().getText() : null;
		String email = !"".equals(paneDatosGeneralesController.getTfEmail().getText()) ? paneDatosGeneralesController.getTfEmail().getText() : null;
		String movil = !"".equals(paneDatosGeneralesController.getTfMovil().getText()) ? paneDatosGeneralesController.getTfMovil().getText() : null;
		String nombre = !"".equals(paneDatosGeneralesController.getTfNombre().getText()) ? paneDatosGeneralesController.getTfNombre().getText() : null;
		String poblacion = !"".equals(paneDatosGeneralesController.getTfPoblacion().getText()) ? paneDatosGeneralesController.getTfPoblacion().getText() : null;
		String localidad = !"".equals(paneDatosGeneralesController.getTfLocalidad().getText()) ? paneDatosGeneralesController.getTfLocalidad().getText() : null;
		String provincia = !"".equals(paneDatosGeneralesController.getTfProvincia().getText()) ? paneDatosGeneralesController.getTfProvincia().getText() : null;
		String observaciones = !"".equals(paneObservacionesController.getTaObservaciones().getText()) ? paneObservacionesController.getTaObservaciones().getText() : null;
		String numeroTarjeta = !"".equals(paneDatosGeneralesController.getTfNumeroTarjeta().getText()) ? paneDatosGeneralesController.getTfNumeroTarjeta().getText() : null;

		fidelizado.setFechaNacimiento(fechaNacimiento);
		fidelizado.setApellidos(apellidos);
		fidelizado.setCodFidelizado(codfidelizado);
		fidelizado.setCodPais(codpais);
		fidelizado.setCp(codpostal);
		fidelizado.setDesPais(despais);
		fidelizado.setDocumento(documento);
		fidelizado.setDomicilio(domicilio);
		fidelizado.setNombre(nombre);
		fidelizado.setPoblacion(poblacion);
		fidelizado.setLocalidad(localidad);
		fidelizado.setProvincia(provincia);
		fidelizado.setObservaciones(observaciones);
		fidelizado.setPaperLess(paperLess);

		// ISK-311 GAP 141 Actualización servicio NAV (nuevos campos)
		String consentimientoRGPD = !"".equals(((IskaypetPaneDatosGeneralesController)paneDatosGeneralesController).getTfConsentimientoRGPD().getText()) ? ((IskaypetPaneDatosGeneralesController)paneDatosGeneralesController).getTfConsentimientoRGPD().getText() : null;
		if (StringUtils.isNotBlank(consentimientoRGPD)) {
			if (fidelizado.getLstSolicitudes() == null) {
				fidelizado.setLstSolicitudes(new ArrayList<SolicitudBean>());
			}
			SolicitudBean solicitud = new SolicitudBean();
			solicitud.setTipoSolicitud("RGPD");
			solicitud.setDesSolicitud(consentimientoRGPD);
			fidelizado.getLstSolicitudes().add(solicitud);
		}
		
		if (codalmFav != null) {
			EnlaceFidelizadoBean enlace = new EnlaceFidelizadoBean();
			enlace.setIdClase("D_TIENDAS_TBL.CODALM");
			enlace.setIdObjeto(codalmFav);

			fidelizado.setEnlace(enlace);
		}
		
		// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS
		String codLengua = StringUtils.isNotBlank(((IskaypetPaneDatosGeneralesController)paneDatosGeneralesController).getTfCodLenguaje().getText()) ? ((IskaypetPaneDatosGeneralesController)paneDatosGeneralesController).getTfCodLenguaje().getText() : null;
		fidelizado.setCodlengua(codLengua);
		
		String telefonoFijo = StringUtils.isNotBlank(((IskaypetPaneDatosGeneralesController)paneDatosGeneralesController).getTfTelefonoFijo().getText()) ? ((IskaypetPaneDatosGeneralesController)paneDatosGeneralesController).getTfTelefonoFijo().getText() : null;
		
		if (INSERCION.equals(modo)) {
			List<ColectivosFidelizadoBean> colectivos = loadColectivos(codcolectivo, descolectivo, isFidelizado);
			fidelizado.setColectivos(colectivos);

			List<TiposContactoFidelizadoBean> tiposContacto = new ArrayList<>();
			if (email != null) {
				TiposContactoFidelizadoBean contactoEmail = new TiposContactoFidelizadoBean();
				contactoEmail.setCodTipoCon("EMAIL");
				contactoEmail.setRecibeNotificaciones(notifEmail);
				contactoEmail.setValor(email);
				tiposContacto.add(contactoEmail);
			}
			if (movil != null) {
				TiposContactoFidelizadoBean contactoMovil = new TiposContactoFidelizadoBean();
				contactoMovil.setCodTipoCon("MOVIL");
				contactoMovil.setRecibeNotificaciones(notifMovil);
				contactoMovil.setValor(movil);

				tiposContacto.add(contactoMovil);
			}
			
			// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS
			if (StringUtils.isNotBlank(telefonoFijo)) {
				TiposContactoFidelizadoBean contactoTelefonoFijo = new TiposContactoFidelizadoBean();
				contactoTelefonoFijo.setCodTipoCon("TELEFONO1");
				contactoTelefonoFijo.setRecibeNotificaciones(false);
				contactoTelefonoFijo.setValor(telefonoFijo);

				tiposContacto.add(contactoTelefonoFijo);
			}

			fidelizado.setContactos(tiposContacto);
			
			fidelizado.setTarjetas(new ArrayList<TarjetaBean>());
			if (numeroTarjeta != null) {
				List<TarjetaBean> tarjetasFidelizado = new ArrayList<TarjetaBean>();
				TarjetaBean tarjeta = new TarjetaBean();
				tarjeta.setNumeroTarjeta(numeroTarjeta);
				tarjetasFidelizado.add(tarjeta);

				fidelizado.setTarjetas(tarjetasFidelizado);
			}
			
			// ISK-311 GAP 141 Actualización servicio NAV (nuevos campos)
			// Le asignamos como tienda favorita la tienda actual en la que se está dando de alta
			EnlaceFidelizadoBean enlace = new EnlaceFidelizadoBean();
			enlace.setIdClase("D_TIENDAS_TBL.CODALM");
			enlace.setIdObjeto(sesion.getAplicacion().getCodAlmacen());
			fidelizado.setEnlace(enlace);
			
		}
		else if ("EDICION".equals(modo)) {
			List<ColectivosFidelizadoBean> colectivos = this.fidelizado.getColectivos().size() > 0 ? this.fidelizado.getColectivos() : new ArrayList<>();
			if (codcolectivo != null) {
				ColectivosFidelizadoBean colectivo = new ColectivosFidelizadoBean();
				colectivo.setCodColectivo(codcolectivo);
				colectivo.setDesColectivo(descolectivo);
				colectivos.add(colectivo);
			}
			fidelizado.setColectivos(colectivos);
			fidelizado.setIdFidelizado(this.fidelizado.getIdFidelizado());
			fidelizado.setCodAlm(codalmFav);
			fidelizado.setContactos(this.fidelizado.getContactos());
			fidelizado.setFechaAlta(this.fidelizado.getFechaAlta());
			TiposContactoFidelizadoBean oldMovilContacto = fidelizado.getTipoContacto("MOVIL");
			if (oldMovilContacto != null && oldMovilContacto.getValor() != null) {
				getInfoContacto(notifMovil, movil, oldMovilContacto);
			}
			else {
				if (movil != null) {
					TiposContactoFidelizadoBean contactoMovil = new TiposContactoFidelizadoBean();
					contactoMovil.setCodTipoCon("MOVIL");
					contactoMovil.setRecibeNotificaciones(notifMovil);
					contactoMovil.setValor(movil);
					contactoMovil.setEstadoBean(Estado.NUEVO);
					fidelizado.getContactos().add(contactoMovil);
				}
			}
			TiposContactoFidelizadoBean oldEmailContacto = fidelizado.getTipoContacto("EMAIL");
			if (oldEmailContacto != null && oldEmailContacto.getValor() != null) {
				getInfoContacto(notifEmail, email, oldEmailContacto);
			}
			else {
				if (email != null) {
					TiposContactoFidelizadoBean contactoEmail = new TiposContactoFidelizadoBean();
					contactoEmail.setCodTipoCon("EMAIL");
					contactoEmail.setRecibeNotificaciones(notifEmail);
					contactoEmail.setValor(email);
					contactoEmail.setEstadoBean(Estado.NUEVO);

					fidelizado.getContactos().add(contactoEmail);
				}
			}
			
			// ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS
			TiposContactoFidelizadoBean oldTelefonoFijoContacto = fidelizado.getTipoContacto("TELEFONO1");
			if (oldTelefonoFijoContacto != null && oldTelefonoFijoContacto.getValor() != null) {
				getInfoContacto(Boolean.FALSE, telefonoFijo, oldTelefonoFijoContacto);
				
			} else if (telefonoFijo != null) {
					TiposContactoFidelizadoBean contactoTelefonoFijo = new TiposContactoFidelizadoBean();
					contactoTelefonoFijo.setCodTipoCon("TELEFONO1");
					contactoTelefonoFijo.setRecibeNotificaciones(false);
					contactoTelefonoFijo.setValor(telefonoFijo);
					contactoTelefonoFijo.setEstadoBean(Estado.NUEVO);

					fidelizado.getContactos().add(contactoTelefonoFijo);
			}
		}
		
		return fidelizado;
	}

	private void getInfoContacto(Boolean notifEmail, String email, TiposContactoFidelizadoBean oldEmailContacto) {
		String oldEmail = oldEmailContacto.getValor();
		boolean oldRecibeNotif = oldEmailContacto.getRecibeNotificaciones();
		if (email == null) {
			oldEmailContacto.setEstadoBean(Estado.BORRADO);
		}
		else if (!oldEmail.equals(email) || oldRecibeNotif != notifEmail) {
			oldEmailContacto.setValor(email);
			oldEmailContacto.setRecibeNotificaciones(notifEmail);
			oldEmailContacto.setEstadoBean(Estado.MODIFICADO);
		}
	}

	private List<ColectivosFidelizadoBean> loadColectivos(String codcolectivo, String descolectivo, boolean isFidelizado) {

		List<ColectivosFidelizadoBean> colectivos = new ArrayList<>();

		if (codcolectivo != null && isFidelizado) {
			ColectivosFidelizadoBean colectivo = new ColectivosFidelizadoBean();
			colectivo.setCodColectivo(codcolectivo);
			colectivo.setDesColectivo(descolectivo);

			colectivos.add(colectivo);
		}

		return colectivos;
	}

	@Override
	public boolean validarDatos() {

		// Limpiamos errores
		lbError.setText("");
		formFidelizado.clearErrorStyle();
		iskaypetFormFidelizado.clearErrorStyle();

		// Obtenemos valores de la pestaña de datos generales del fidelizado
		iskaypetFormFidelizado.setApellidos(paneDatosGeneralesController.getTfApellidos().getText());
		iskaypetFormFidelizado.setCodColectivo(paneDatosGeneralesController.getTfCodColectivo().getText());
		iskaypetFormFidelizado.setCodigo(paneDatosGeneralesController.getTfCodigo().getText());
		iskaypetFormFidelizado.setCp(paneDatosGeneralesController.getTfCodPostal().getText());
		iskaypetFormFidelizado.setDocumento(paneDatosGeneralesController.getTfDocumento().getText());
		iskaypetFormFidelizado.setDomicilio(paneDatosGeneralesController.getTfDomicilio().getText());
		iskaypetFormFidelizado.setEmail(paneDatosGeneralesController.getTfEmail().getText());
		iskaypetFormFidelizado.setFechaNacimiento(paneDatosGeneralesController.getDpFechaNacimiento().getSelectedDate());
		iskaypetFormFidelizado.setMovil(paneDatosGeneralesController.getTfMovil().getText());
		iskaypetFormFidelizado.setNombre(paneDatosGeneralesController.getTfNombre().getText());
		iskaypetFormFidelizado.setNumeroTarjeta(paneDatosGeneralesController.getTfNumeroTarjeta().getText());
		iskaypetFormFidelizado.setCodPais(paneDatosGeneralesController.getTfCodPais().getText());
		iskaypetFormFidelizado.setDesPais(paneDatosGeneralesController.getTfDesPais().getText());
		iskaypetFormFidelizado.setPoblacion(paneDatosGeneralesController.getTfPoblacion().getText());
		iskaypetFormFidelizado.setLocalidad(paneDatosGeneralesController.getTfLocalidad().getText());
		//iskaypetFormFidelizado.setProvincia(paneDatosGeneralesController.getTfProvincia().getText());
		iskaypetFormFidelizado.setObservaciones(paneObservacionesController.getTaObservaciones().getText());

		TipoIdentGui tipoIdent = paneDatosGeneralesController.getCbTipoDocumento().getSelectionModel().getSelectedItem();
		String codTipoIdent = (tipoIdent == null) ? "" : tipoIdent.getCodigo();
		iskaypetFormFidelizado.setTipoDocumento(codTipoIdent);

		SexoGui sexo = paneDatosGeneralesController.getCbSexo().getSelectionModel().getSelectedItem();
		String codSexo = (sexo == null) ? "" : sexo.getCodigo();
		iskaypetFormFidelizado.setSexo(codSexo);

		EstadoCivilGui estCivil = paneDatosGeneralesController.getCbEstadoCivil().getSelectionModel().getSelectedItem();
		String codEstCivil = (sexo == null) ? "" : estCivil.getCodEstadoCivil();
		iskaypetFormFidelizado.setEstadoCivil(codEstCivil);

		// Validar país
		try {
			paisService.consultarCodPais(paneDatosGeneralesController.getTfCodPais().getText());
		}
		catch (PaisNotFoundException var10) {
			PathImpl path = PathImpl.createPathFromString("codPais");
			iskaypetFormFidelizado.setFocus(path);
			iskaypetFormFidelizado.setErrorStyle(path, true);
			return false;
		}
		catch (PaisServiceException var11) {
			log.debug("validarDatos() - Ha habido un error al buscar el país con código " + paneDatosGeneralesController.getTfCodPais().getText() + ": " + var11.getMessage());
			return false;
		}

		// Validar codigo de colectivo
		String codcolectivo = paneDatosGeneralesController.getTfCodColectivo().getText();
		if (codcolectivo != null && !"".equals(codcolectivo)) {
			boolean colectivoValido = false;
			for (ColectivoAyudaGui colectivo : todosColectivos) {
				if (codcolectivo.equals(colectivo.getCodColectivo())) {
					colectivoValido = true;
					break;
				}
			}

			if (!colectivoValido) {
				PathImpl path = PathImpl.createPathFromString("codColectivo");
				iskaypetFormFidelizado.setFocus(path);
				iskaypetFormFidelizado.setErrorStyle(path, true);
				return false;
			}
		}

		// Validar codigo de tienda favorita
		String codtiendafavorita = paneDatosGeneralesController.getTfCodTienda().getText();
		if (codtiendafavorita != null && !"".equals(codtiendafavorita)) {
			boolean tiendaValida = false;

			for (TiendaGui tienda : todasTiendas) {
				if (codtiendafavorita.equals(tienda.getCodTienda())) {
					tiendaValida = true;
					break;
				}
			}

			if (!tiendaValida) {
				PathImpl path = PathImpl.createPathFromString("codAlmFav");
				iskaypetFormFidelizado.setFocus(path);
				iskaypetFormFidelizado.setErrorStyle(path, true);
				return false;
			}
		}
		
		// Validar Lengua y Teléfono Fijo (ISK-313 GAP 141-b Idioma en alta y modificación fidelizados POS)
		String telefonoFijo = "";
		if (paneDatosGeneralesController instanceof IskaypetPaneDatosGeneralesController) {
			telefonoFijo = ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfTelefonoFijo().getText();
			iskaypetFormFidelizado.setTelefonoFijo(telefonoFijo);
			iskaypetFormFidelizado.setCodLenguaje(((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfCodLenguaje().getText());
			iskaypetFormFidelizado.setDesLenguaje(((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfDesLenguaje().getText());
			try {
				lenguajesService.getLenguajeByCodLengua(((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfCodLenguaje().getText());
			}
			catch (LenguajeNotFoundException var10) {
				PathImpl path = PathImpl.createPathFromString("codLenguaje");
				iskaypetFormFidelizado.setFocus(path);
				iskaypetFormFidelizado.setErrorStyle(path, true);
				return false;
			}
			catch (Exception var11) {
				log.debug("validarDatos() - Ha habido un error al buscar el idioma con código " + ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfCodLenguaje().getText() + ": " + var11.getMessage());
				return false;
			}
		}

		// Validar valores de IskaypetFormularioFidelizadoBean
		boolean valido = true;
		Set<ConstraintViolation<IskaypetFormularioFidelizadoBean>> constraintViolations2 = IskaypetValidationUI.getInstance().getValidator().validate(iskaypetFormFidelizado, new Class[0]);
		List<String> errorMessages = new ArrayList<>();
		if (constraintViolations2.size() > 0) {
			boolean first = false;
			for (ConstraintViolation<IskaypetFormularioFidelizadoBean> violation : constraintViolations2) {
				if (!"telefonoFijo".equals(violation.getPropertyPath().toString()) || ("telefonoFijo".equals(violation.getPropertyPath().toString()) && StringUtils.isNotBlank(telefonoFijo))) {
					errorMessages.add(violation.getMessage());
					iskaypetFormFidelizado.setErrorStyle(violation.getPropertyPath(), true);
					if (!first) {
						iskaypetFormFidelizado.setFocus(violation.getPropertyPath());
					}
				}
			}
		}

		// Si es persona se verifica que tenga apellidos
		if (((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getcBoxPersona().isSelected()) {
			// Validar apellidos
			if (StringUtils.isBlank(iskaypetFormFidelizado.getApellidos())) {
				errorMessages.add("Debe rellenar el campo apellidos");
				PathImpl path = PathImpl.createPathFromString("apellidos");
				iskaypetFormFidelizado.setErrorStyle(path, true);
				iskaypetFormFidelizado.setFocus(path);
			}

		}
		else {
			// Validar razón social
			if (StringUtils.isBlank(iskaypetFormFidelizado.getApellidos())) {
				errorMessages.add("Debe rellenar el campo Razón Social");
				PathImpl path = PathImpl.createPathFromString("apellidos");
				iskaypetFormFidelizado.setErrorStyle(path, true);
				iskaypetFormFidelizado.setFocus(path);
			}
		}

		// Validar confirmación de email
		if (paneDatosGeneralesController instanceof IskaypetPaneDatosGeneralesController) {
			String emailConfirm = ((IskaypetPaneDatosGeneralesController) paneDatosGeneralesController).getTfEmailConfirm().getText();
			if (StringUtils.isBlank(emailConfirm)) {
				PathImpl path = PathImpl.createPathFromString("emailconfirm");
				iskaypetFormFidelizado.setErrorStyle(path, true);
				iskaypetFormFidelizado.setFocus(path);
				errorMessages.add(I18N.getTexto("Debe rellenar el campo de confirmación de email"));
			}
			else if (!emailConfirm.equals(iskaypetFormFidelizado.getEmail())) {
				PathImpl path = PathImpl.createPathFromString("emailconfirm");
				iskaypetFormFidelizado.setErrorStyle(path, true);
				iskaypetFormFidelizado.setFocus(path);
				errorMessages.add(I18N.getTexto("El campo de email y confirmación de email no coinciden"));
			}
		}

		// Validar código postal
		ValidadorCodigoPostal validadorCP = ValidadorCodigoPostalFactory.getValidadorCodigoPostal(iskaypetFormFidelizado.getCodPais());
		ContextoValidadorCodigoPostal context = new ContextoValidadorCodigoPostal();
		context.setValidadorCodigoPostal(validadorCP);
		try {
            context.ejecutaValidacionPostal(iskaypetFormFidelizado.getCp());
        } catch (ValidadorCodigoPostalException e) {
			String error;
			if(!Objects.equals(iskaypetFormFidelizado.getCodPais(), "ES") && !Objects.equals(iskaypetFormFidelizado.getCodPais(), "PT")) {
				error = I18N.getTexto("Código postal no válido: debe tener entre 4 y 8 caracteres, con letras, números y un guion (no al inicio ni al final).");
			} else {
				error = I18N.getTexto("El código postal del cliente no es válido. Por favor, asegúrese de que el código tenga el formato correcto {0} y solo contenga números.", e.getMessage());
			}
			errorMessages.add(error);
			PathImpl path = PathImpl.createPathFromString("cp");
			iskaypetFormFidelizado.setErrorStyle(path, true);
			iskaypetFormFidelizado.setFocus(path);
		}

		// Validar documento si no contiene errores de formulario
		if (!(paneDatosGeneralesController.getCbTipoDocumento().getStyleClass().contains("error-formulario") || paneDatosGeneralesController.getTfDocumento().getStyleClass()
				.contains("error-formulario"))) {
			String claseValidacion = paneDatosGeneralesController.getCbTipoDocumento().getSelectionModel().getSelectedItem().getClaseValidacion();
			String mensaje = null;
			try {
				if (claseValidacion != null) {
					IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
					if (!validadorDocumentoIdentificacion.validarDocumentoIdentificacion(iskaypetFormFidelizado.getDocumento())) {
						mensaje = I18N.getTexto("Documento no valido");
					}
				}

			}
			catch (Exception e) {
				log.error("Error validando el documento, se continua:");
				mensaje = I18N.getTexto("Error validando el documento de identificación: ") + e.getMessage();
			}
			finally {
				if (StringUtils.isNotBlank(mensaje)) {
					PathImpl path = PathImpl.createPathFromString("documento");
					iskaypetFormFidelizado.setErrorStyle(path, true);
					iskaypetFormFidelizado.setFocus(path);
					errorMessages.add(mensaje);
				}
			}
		}

		// Validaciones de valores unicos cuando insertarmos (evitar duplicidades)
		if (INSERCION.equals(getModo())) {
			validarValoresUnicos(errorMessages, iskaypetFormFidelizado.getCodigo());
		}

		// Muestra mensaje de error
		if (errorMessages.size() > 0) {
			valido = false;
			if (errorMessages.size() > 1) {
				String message = "";
				errorMessages = errorMessages.stream().sorted().collect(Collectors.toList());
				for(String error : errorMessages) {
					message += I18N.getTexto(error) + "\n";
				}
				lbError.setText(message);
			}
			else {
				for (String error : errorMessages) {
					lbError.setText(I18N.getTexto(error));
				}
			}
		}

		return valido;
	}

	protected void validarValoresUnicos(List<String> errorMessages, String codFidelizado) {
		List<String> valoresUnicos = new ArrayList<>();
		if (StringUtils.isNotBlank(iskaypetFormFidelizado.getDocumento())) {
			FormularioBusquedaFidelizadoBean formBusqueda = new FormularioBusquedaFidelizadoBean();
			formBusqueda.setDocumento(iskaypetFormFidelizado.getDocumento());
			if (existeFidelizado(formBusqueda, codFidelizado)) {
				PathImpl path = PathImpl.createPathFromString("documento");
				iskaypetFormFidelizado.setErrorStyle(path, true);
				iskaypetFormFidelizado.setFocus(path);
				valoresUnicos.add(I18N.getTexto("documento"));
			}
		}

		if (StringUtils.isNotBlank(iskaypetFormFidelizado.getEmail())) {
			FormularioBusquedaFidelizadoBean formBusqueda = new FormularioBusquedaFidelizadoBean();
			formBusqueda.setEmail(iskaypetFormFidelizado.getEmail());
			if (existeFidelizado(formBusqueda, codFidelizado)) {
				PathImpl path = PathImpl.createPathFromString("email");
				iskaypetFormFidelizado.setErrorStyle(path, true);
				iskaypetFormFidelizado.setFocus(path);
				valoresUnicos.add(I18N.getTexto("email"));
			}
		}

		if (StringUtils.isNotBlank(iskaypetFormFidelizado.getMovil())) {
			FormularioBusquedaFidelizadoBean formBusqueda = new FormularioBusquedaFidelizadoBean();
			formBusqueda.setTelefono(iskaypetFormFidelizado.getMovil());
			if (existeFidelizado(formBusqueda, codFidelizado)) {
				PathImpl path = PathImpl.createPathFromString("movil");
				iskaypetFormFidelizado.setErrorStyle(path, true);
				iskaypetFormFidelizado.setFocus(path);
				valoresUnicos.add(I18N.getTexto("teléfono"));
			}
		}

		if (valoresUnicos.size() > 0) {
			String message = I18N.getTexto("Ya existe un cliente con este ");
			message += String.join(", ", valoresUnicos);
			errorMessages.add(message);
		}
	}

	@Override
	public void crearFidelizado() {
		String apiKey = variablesService.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();
		FidelizadoRequestRest insertFidelizado = new FidelizadoRequestRest(apiKey, uidActividad, getDatosFidelizado(INSERCION), sesion.getAplicacion().getEmpresa().getCodEmpresa(),
				sesion.getAplicacion().getCodAlmacen());
		insertFidelizado.setTipoNotificacion("NUEVO_USUARIO_FIDELIZADO");
		CrearFidelizadoTask insertFidelizadoTask = SpringContext.getBean(CrearFidelizadoTask.class, insertFidelizado, new RestBackgroundTask.FailedCallback<FidelizadoBean>() {
			@Override
			public void succeeded(FidelizadoBean result) {
				fidelizado = result;
				tabResumen.setDisable(false);
				tabMovimientosTarjetas.setDisable(!verMovTarjetas);
				tabColectivos.setDisable(!verColectivos);
				tabUltimasVentas.setDisable(!verUltVentas);
				tabEtiquetas.setDisable(!verEtiquetas);
				paneResumenFidelizadoController.selected();
				tpDatosFidelizado.getSelectionModel().select(tabResumen);
				btAceptar.setVisible(false);
				btAceptar.setManaged(false);
				btCancelar.setVisible(false);
				btCancelar.setManaged(false);
				btEditar.setVisible(true);
				btEditar.setManaged(true);
				btImprimir.setVisible(false);
				btImprimir.setManaged(false);
				btCerrar.setVisible(true);
				btCerrar.setManaged(true);
				lblCampoObligatorios.setVisible(false);
				setModo("CONSULTA");

				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Cliente creado correctamente"), getStage());

				if (StringUtils.isNotBlank(result.getNumeroTarjeta())) {
					asignarFidelizadoAVenta();

				}
				else {
					asignarFidelizadoAVenta(result.getIdFidelizado());
				}
			}

			@Override
			public void failed(Throwable throwable) {
				getApplication().getMainView().close();
			}
		}, getStage());
		insertFidelizadoTask.start();
	}

	protected void asignarFidelizadoAVenta(Long idFidelizado) {
		ConsultarFidelizadoRequestRest consultarFidelizadoRequestRest = new ConsultarFidelizadoRequestRest(variablesService.getVariableAsString(APIKEY), sesion.getAplicacion().getUidActividad());
		consultarFidelizadoRequestRest.setIdFidelizado(idFidelizado.toString());
		SpringContext.getBean(ConsultarTarjetasFidelizadoTask.class, consultarFidelizadoRequestRest, new RestBackgroundTask.FailedCallback<List<TarjetaBean>>() {

			@Override
			public void succeeded(List<TarjetaBean> tarjetas) {

				if (!CollectionUtils.isEmpty(tarjetas)) {
					TarjetaBean tarjeta = tarjetas.stream().findFirst().orElse(null);
					if (tarjeta != null) {
						paneDatosGeneralesController.getTfNumeroTarjeta().setText(tarjeta.getNumeroTarjeta());
						asignarFidelizadoAVenta();
					}

				}
			}

			@Override
			public void failed(Throwable throwable) {
				getApplication().getMainView().close();
			}
		}, getStage()).start();
	}

	@Override
	protected void asignarFidelizadoAVenta() {
		for( View view : getApplication().getMainView().getSubViews()) {
			if (view instanceof FacturacionArticulosView && view.getController() instanceof FacturacionArticulosController) {
				String numTarjeta = this.paneDatosGeneralesController.getTfNumeroTarjeta().getText();
				if (StringUtils.isNotBlank(numTarjeta)) {
					FacturacionArticulosController facturacionArticulosController = (FacturacionArticulosController) view.getController();

					try {
						FidelizacionBean fidelizado = Dispositivos.getInstance().getFidelizacion().consultarTarjetaFidelizado(this.getStage(), numTarjeta, this.sesion.getAplicacion().getUidActividad());
						facturacionArticulosController.ticketManager.getTicket().getCabecera().setDatosFidelizado(fidelizado);
						if (facturacionArticulosController instanceof IskaypetFacturacionArticulosController) {
							((IskaypetFacturacionArticulosController) facturacionArticulosController).cargarFidelizado(fidelizado.getIdFidelizado());
						}
					}
					catch (Exception var6) {
						log.error("crearFidelizado() - Ha habido un error al consultar el fidelizado recién creado: " + var6.getMessage(), var6);
						facturacionArticulosController.ticketManager.getTicket().getCabecera().setDatosFidelizado(numTarjeta);
					}

					facturacionArticulosController.refrescarDatosPantalla();
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void editarFidelizado() {
		String apiKey = variablesService.getVariableAsString("WEBSERVICES.APIKEY");
		String uidActividad = sesion.getAplicacion().getUidActividad();
		fidelizado = getDatosFidelizado("EDICION");
		FidelizadoRequestRest insertFidelizado = new FidelizadoRequestRest(apiKey, uidActividad, fidelizado);
		if (fidelizado.getContactos() != null && !fidelizado.getContactos().isEmpty()) {
			Map<String, Integer> mapaEstadosTiposContacto = new HashMap();
			Iterator var5 = fidelizado.getContactos().iterator();

			while (var5.hasNext()) {
				TiposContactoFidelizadoBean tipoContacto = (TiposContactoFidelizadoBean) var5.next();
				if (tipoContacto.getEstadoBean() != 0) {
					mapaEstadosTiposContacto.put(tipoContacto.getCodTipoCon(), tipoContacto.getEstadoBean());
				}
			}

			insertFidelizado.setMapaEstadosTiposContacto(mapaEstadosTiposContacto);
		}

		if (fidelizado.getCodAlm() != null) {
			insertFidelizado.setCodAlmFav(fidelizado.getCodAlm());
		}
		
		borrarTelefonoFijo = false;
		if (fidelizado.getTipoContacto("TELEFONO1") != null && fidelizado.getTipoContacto("TELEFONO1").getEstadoBean() == Estado.BORRADO) {
			borrarTelefonoFijo = true;
		}

		SpringContext.getBean(EditarFidelizadoTask.class, new Object[] { insertFidelizado, new RestBackgroundTask.FailedCallback<FidelizadoBean>() {

			public void succeeded(FidelizadoBean result) {
				fidelizado = result;
				if (borrarTelefonoFijo) {
					fidelizado.getContactos().remove(fidelizado.getTipoContacto("TELEFONO1"));
				}
				actualizaFidelizado();
				setModo("CONSULTA");
				tabResumen.setDisable(false);
				tabMovimientosTarjetas.setDisable(!verMovTarjetas);
				tabColectivos.setDisable(!verColectivos);
				tabUltimasVentas.setDisable(!verUltVentas);
				tabEtiquetas.setDisable(!verEtiquetas);
				paneResumenFidelizadoController.setFidelizado(null);
				paneResumenFidelizadoController.selected();
				tpDatosFidelizado.getSelectionModel().select(tabResumen);
				btAceptar.setVisible(false);
				btAceptar.setManaged(false);
				btCancelar.setVisible(false);
				btCancelar.setManaged(false);
				btEditar.setVisible(true);
				btEditar.setManaged(true);
				btCerrar.setVisible(true);
				btCerrar.setManaged(true);
				lblCampoObligatorios.setVisible(false);
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Cliente editado correctamente"), getStage());

				if (StringUtils.isNotBlank(result.getNumeroTarjeta())) {
					asignarFidelizadoAVenta();

				}
				else {
					asignarFidelizadoAVenta(result.getIdFidelizado());
				}

			}

			public void failed(Throwable throwable) {
				getApplication().getMainView().close();
			}
		}, getStage() }).start();
	}

	private boolean existeFidelizado(FormularioBusquedaFidelizadoBean fidelizado, String codFidelizado) {

		ConsultarFidelizadoRequestRest req = new ConsultarFidelizadoRequestRest(variablesServices.getVariableAsString(APIKEY), sesion.getAplicacion().getUidActividad());

		if (StringUtils.isNotBlank(fidelizado.getDocumento())) {
			req.setDocumento(fidelizado.getDocumento());
		}

		if (StringUtils.isNotBlank(fidelizado.getTelefono())) {
			req.setTelefono(fidelizado.getTelefono());
		}

		if (StringUtils.isNotBlank(fidelizado.getEmail())) {
			req.setEmail(fidelizado.getEmail());
		}

		try {
			List<FidelizadoBean> fidelizados = FidelizadosRest.getFidelizadosDatos(req);
			if (fidelizados.size() > 0) {
				for (FidelizadoBean fide : fidelizados) {
					if (!fide.getCodFidelizado().equals(codFidelizado)) {
						return true;
					}
				}

			}
		}
		catch (RestException | RestHttpException e) {
			log.error("validacionUnicidad() - ha habido un problema con la petición REST: " + e.getMessage(), e);
		}
		catch (Exception e) {
			log.error("validacionUnicidad() - Ha ocurrido un error: " + e.getMessage(), e);
		}

		return false;
	}
	
	@Override
	public void actualizaFidelizado() {
		
		FidelizacionBean fb = new FidelizacionBean();
		
		fb.setIdFidelizado(fidelizado.getIdFidelizado());
		fb.setNombre(fidelizado.getNombre());
		fb.setApellido(fidelizado.getApellidos());
		fb.setDomicilio(fidelizado.getDomicilio());
		fb.setLocalidad(fidelizado.getLocalidad());
		fb.setProvincia(fidelizado.getProvincia());
		fb.setCodPais(fidelizado.getCodPais());
		fb.setNumTarjetaFidelizado(getNumeroTarjetaFidelizado());
		fb.setCodTipoIden(fidelizado.getCodTipoIden());
		fb.setDocumento(fidelizado.getDocumento());
		fb.setPaperLess(fidelizado.getPaperLess());
		fb.setCp(fidelizado.getCp());
		fb.setPoblacion(fidelizado.getPoblacion());

		for(View view : getApplication().getMainView().getSubViews()){
			if(view instanceof FacturacionArticulosView && view.getController() instanceof FacturacionArticulosController) {
				FacturacionArticulosController facturacionArticulosController = (FacturacionArticulosController) view.getController();
				facturacionArticulosController.ticketManager.getTicket().getCabecera().setDatosFidelizado(fb);
			}
		}
	}
}
