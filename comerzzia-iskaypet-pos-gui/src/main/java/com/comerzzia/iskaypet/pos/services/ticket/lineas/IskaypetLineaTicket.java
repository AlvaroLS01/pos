package com.comerzzia.iskaypet.pos.services.ticket.lineas;

import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaDto;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalDto;
import com.comerzzia.iskaypet.pos.persistence.articulos.anexos.DetalleAnexoArticulo;
import com.comerzzia.iskaypet.pos.persistence.articulos.lotes.LoteDTO;
import com.comerzzia.iskaypet.pos.persistence.auditorias.motivos.MotivoAuditoriaDTO;
import com.comerzzia.iskaypet.pos.persistence.promotionsticker.PromotionalStickerXML;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.detalles.DetallesTrazabilidadDto;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdent;
import com.comerzzia.iskaypet.pos.persistence.ticket.lineas.DtoPromocion;
import com.comerzzia.iskaypet.pos.persistence.ticket.lineas.TextoPromocion;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaLineaTicket;
import com.comerzzia.iskaypet.pos.services.promociones.IskaypetPromocionesService;
import com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad.TrazabilidadMascotasService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.canjeoPuntos.ArticlesPointsXMLBean;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.inyectables.Inyectable;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.IPromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Primary
@Component
@Scope("prototype")
public class IskaypetLineaTicket extends LineaTicket {

	public static final String CATEGORIA_MEDICAMENTOS = "X_POS.CATEGORIA_MEDICAMENTOS";
	public static final String IMPRIMIR_MEDICAMENTOS = "X_POS.IMPRIMIR_MEDICAMENTOS";

	@Autowired
	@XmlTransient
	private VariablesServices variablesServices;

	@Autowired
	@XmlTransient
	private IskaypetPromocionesService promocionesService;

	@Autowired
	@XmlTransient
	protected TrazabilidadMascotasService mascotasService;
	
	// GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS
	@XmlElement(name = "contratoAnimal")
	private ContratoAnimalDto contratoAnimal;
	private MotivoAuditoriaDto motivoAuditoria;

	@XmlElement(name = "motivo_devolucion")
	private MotivoAuditoriaDTO motivoDevolucion;

	@XmlElement(name = "motivo_precio_descuento")
	private MotivoAuditoriaDTO motivoPrecioDescuento;

	@XmlElementWrapper(name = "textosPromociones")
	@XmlElement(name = "textoPromocion")
	protected List<TextoPromocion> textosPromociones;

	@XmlElement(name = "uidAuditoria")
	private String uidAuditoria;

	// GAP 12 - ISK-8 GESTIÓN DE LOTES
	@XmlElement(name = "lote")
	private LoteDTO lote;

	// GAP46 - CANJEO ARTÍCULOS POR PUNTOS
	@XmlElement(name = "canjeo_puntos")
	protected ArticlesPointsXMLBean articlePoints;

	// GAP62 - PEGATINAS PROMOCIONALES
	@XmlElement(name = "pegatina_promocion")
	protected PromotionalStickerXML pegatinaPromocional;

	// GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
	@XmlElementWrapper(name = "auditorias")
	@XmlElement(name = "auditoria")
	protected List<AuditoriaLineaTicket> auditorias;
	//GAP 169 DEVOLUCIÓN DE PROMOCIONES
	@XmlElementWrapper(name = "dto_promociones")
	@XmlElement(name = "dto_promocion")
	protected List<DtoPromocion> dtoPromociones ;
	
	//GAP 172 TRAZABILIDAD ANIMALES
	@XmlElementWrapper(name = "extensiones")
	@XmlElement(name = "extension")
	private List<DetallesTrazabilidadDto> detallesTrazabilidad;
	@XmlElement
	private boolean requiereContrato;
	@XmlElement
	private boolean requiereIdentificacion;
	@XmlTransient
	private ItemsPetsIdent itemsPetIdent;
	@XmlElement
	private Boolean imprimir;

	@XmlElement(name = "inyectable")
	private Inyectable inyectable;

	@XmlElement(name = "plan_salud")
	private Boolean planSalud;

	public IskaypetLineaTicket() {
		this.imprimir = true;
		this.planSalud = false;
	}

	public boolean tieneMotivoAuditoria() {
		return motivoDevolucion != null || motivoPrecioDescuento != null;
	}

	@Override
	public void addPromocion(PromocionLineaTicket promocion) {
		super.addPromocion(promocion);

		// Se le setea a la linea el texto de promoción
		if (textosPromociones == null) {
			textosPromociones = new ArrayList<>();
		}
		else {
			for (TextoPromocion textoPromocion : textosPromociones) {
				if (promocion.getIdPromocion().equals(textoPromocion.getIdPromocion())) {
					return;
				}
			}
		}

		TextoPromocion textoPromocion = new TextoPromocion();
		textoPromocion.setIdPromocion(promocion.getIdPromocion());
		try {
			/*
			 * Se consulta el texto de promoción de la linea en el siguiente orden: - Texto del detalle de la promoción - Texto del
			 * xml de los datos de la promoción - Descripción de la promoción
			 */
			textoPromocion.setTexto(promocionesService.consultarTextoDetallePromocion(promocion.getIdPromocion(), this.getCodArticulo()));
			if (StringUtils.isBlank(textoPromocion.getTexto())) {
				textoPromocion.setTexto(promocionesService.consultarDescripcionOTextoCabeceraPromocion(promocion.getIdPromocion()));
			}
		}
		catch (Exception ignored) {
		}
		textosPromociones.add(textoPromocion);
	}

	public String getTextoLineaPromocion(Long idPromocion) {
		if (textosPromociones != null) {
			for (TextoPromocion textoPromocion : textosPromociones) {
				if (idPromocion != null && idPromocion.equals(textoPromocion.getIdPromocion())) {
					return textoPromocion.getTexto();
				}
			}
		}
		return "";
	}

	public String getTextoLineaPromocionEscape(Long idPromocion) {
		return StringEscapeUtils.escapeXml(getTextoLineaPromocion(idPromocion));
	}

	public List<TextoPromocion> getTextoLineaPromociones() {
		return textosPromociones;
	}

	@Override
	public void recalcularImporteFinal() {
		Long idTratamientoImpuestos = getCabecera().getCliente().getIdTratImpuestos();

		importeTotalPromociones = BigDecimal.ZERO;
		importeTotalPromocionesMenosIngreso = BigDecimal.ZERO;
		BigDecimal importeTotalPromocionesSinRedondear = BigDecimal.ZERO;
		BigDecimal importeTotalConDtoSinRedondear = BigDecimal.ZERO;
		boolean cantidadTieneDecimales = !BigDecimalUtil.isIgualACero(cantidad.remainder(BigDecimal.ONE));
		BigDecimal importeLineaConImpuestos = BigDecimal.ZERO;

		if (tieneCambioPrecioManual()) {
			importeTotalConDtoSinRedondear = getPrecioTotalSinDto().multiply(cantidad);
		}

		if (tieneDescuentoManual()) {
			importeTotalConDtoSinRedondear = BigDecimalUtil.menosPorcentajeR4(getPrecioTotalSinDto(), descuentoManual).multiply(cantidad);
			importeTotalConDto = BigDecimalUtil.redondear(importeTotalConDtoSinRedondear);
			importeLineaConImpuestos = importeTotalConDto;
		}
		else if (promociones != null) {
			for (IPromocionLineaTicket promocionLinea : promociones) {
				BigDecimal descuento = promocionLinea.getImporteTotalDtoMenosMargen();
				if (cantidadTieneDecimales) {
					// En lineas de peso (cantidad con decimales) usamos descuento sin redondear para no perder
					// precisiÃ³n en los siguientes cÃ¡lculos
					descuento = promocionLinea.getImporteTotalDtoMenosMargenSinRedondear();
				}
				importeTotalPromociones = importeTotalPromociones.add(descuento);
				importeTotalPromocionesSinRedondear = importeTotalPromocionesSinRedondear.add(promocionLinea.getImporteTotalDtoMenosMargenSinRedondear() != null ? promocionLinea.getImporteTotalDtoMenosMargenSinRedondear() : BigDecimal.ZERO);
				importeTotalPromocionesMenosIngreso = importeTotalPromocionesMenosIngreso.add(promocionLinea.getImporteTotalDtoMenosIngreso());
			}
			importeTotalConDto = BigDecimalUtil.redondear(getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromociones));
			importeTotalConDtoSinRedondear = getImporteTotalSinDtoSinRedondear().subtract(importeTotalPromocionesSinRedondear);

			// Para calcular el precio con impuestos y el importe sin impuestos se mantiene toda la precisiÃ³n de la
			// operaciÃ³n de cantidad*preciototal-descuento
			importeLineaConImpuestos = getCantidad().multiply(precioTotalSinDto).subtract(importeTotalPromociones);
		}

		Sesion sesion = SpringContext.getBean(Sesion.class);
		SesionImpuestos sesionImpuestos = sesion.getImpuestos();
		importeConDto = sesionImpuestos.getPrecioSinImpuestos(getCodImpuesto(), BigDecimalUtil.redondear(importeLineaConImpuestos), idTratamientoImpuestos);

		precioConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeConDto.divide(cantidad, 4, RoundingMode.HALF_UP);
		precioTotalConDto = BigDecimalUtil.isIgualACero(cantidad) ? BigDecimal.ZERO : importeLineaConImpuestos.divide(cantidad, 2, RoundingMode.HALF_UP);
		descuento = BigDecimal.ZERO;
		if (tieneDescuentoManual()) {
			descuento = descuentoManual;
		}
		else if (!promociones.isEmpty()) {
			// Calculamos el porcentaje de descuento desde el importe final sin redondear para que coincida con el
			// porcentaje exacto configurado en la promociÃ³n (por ejemplo, si configuro un 10% dto, que no salga un 9,99% tras los
			// cÃƒÂ¡lculos)
			descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDtoSinRedondear);
			// Confirmamos que el % descuento calculado, al aplicarlo sobre el precio unitario y multiplicarlo por la
			// cantidad es igual al importe final previamente calculado. En caso contrario, volveremos a calcular el % de descuento
			// a partir del importe redondeado
			if (!BigDecimalUtil.isIgual(BigDecimalUtil.redondear(BigDecimalUtil.menosPorcentajeR(getPrecioTotalSinDto(), BigDecimalUtil.redondear(descuento)).multiply(cantidad)),
			        (BigDecimalUtil.redondear(importeTotalConDto)))) {
				descuento = BigDecimalUtil.getTantoPorCientoMenos(getImporteTotalSinDtoSinRedondear(), importeTotalConDto);
			}
		}
	}

	@Override
	public void cambiarTarifaCalculos(String codtar) {
		// TODO Auto-generated method stub
		if (precioTotalTarifaOrigen.equals(precioSinDto)) {
			super.cambiarTarifaCalculos(codtar);
		}
		else {
			recalcularImporteFinal();
		}
	}

	/* ######################################################################################################################### */
	/* ###################### GAP 172 TRAZABILIDAD ANIMALES #################################################################### */
	/* ######################################################################################################################### */
	public boolean isMascota() {
		return isMascota(variablesServices, mascotasService);
	}

	public boolean isMascota(VariablesServices variablesServices, TrazabilidadMascotasService mascotasService) {

		// Verificamos si la variable de configuración está definida para la trazabilidad de mascotas
		String codTipoIdentificado = variablesServices.getVariableAsString(TrazabilidadMascotasService.X_POS_IDENTIFICADOR_ANIMAL);
		if (StringUtils.isBlank(codTipoIdentificado)) {
			return false;
		}

		// Verificamos si el articulo tiene detalles de anexos en X_ARTICULOS_TBL
		DetalleAnexoArticulo detallesAnexos = mascotasService.consultarDetallesAnexo(articulo);
		if (detallesAnexos == null) {
			return false;
		}

		// Verificamos si el tipo de material del artículo no está vacío, en cuyo caso no se considera mascota
		String tipoMaterial = detallesAnexos.getTipoMaterial();
		if (StringUtils.isBlank(tipoMaterial)) {
			return false;
		}

		String[] listaIdentificadores = codTipoIdentificado.split(";");
		for (String identificador : listaIdentificadores) {
			if (tipoMaterial.equalsIgnoreCase(identificador)) {
				return true;
			}
		}
		return false;
	}


	public VariablesServices getVariablesServices() {
		return variablesServices;
	}

	public void setVariablesServices(VariablesServices variablesServices) {
		this.variablesServices = variablesServices;
	}

	public IskaypetPromocionesService getPromocionesService() {
		return promocionesService;
	}

	public void setPromocionesService(IskaypetPromocionesService promocionesService) {
		this.promocionesService = promocionesService;
	}

	public ContratoAnimalDto getContratoAnimal() {
		return contratoAnimal;
	}

	public void setContratoAnimal(ContratoAnimalDto contratoAnimal) {
		this.contratoAnimal = contratoAnimal;
	}

	public MotivoAuditoriaDto getMotivoAuditoria() {
		return motivoAuditoria;
	}

	public void setMotivoAuditoria(MotivoAuditoriaDto motivoAuditoria) {
		this.motivoAuditoria = motivoAuditoria;
	}

	public MotivoAuditoriaDTO getMotivoDevolucion() {
		return motivoDevolucion;
	}

	public void setMotivoDevolucion(MotivoAuditoriaDTO motivoDevolucion) {
		this.motivoDevolucion = motivoDevolucion;
	}

	public MotivoAuditoriaDTO getMotivoPrecioDescuento() {
		return motivoPrecioDescuento;
	}

	public void setMotivoPrecioDescuento(MotivoAuditoriaDTO motivoPrecioDescuento) {
		this.motivoPrecioDescuento = motivoPrecioDescuento;
	}

	public List<TextoPromocion> getTextosPromociones() {
		return textosPromociones;
	}

	public void setTextosPromociones(List<TextoPromocion> textosPromociones) {
		this.textosPromociones = textosPromociones;
	}

	public String getUidAuditoria() {
		return uidAuditoria;
	}

	public void setUidAuditoria(String uidAuditoria) {
		this.uidAuditoria = uidAuditoria;
	}

	public LoteDTO getLote() {
		return lote;
	}

	public void setLote(LoteDTO lote) {
		this.lote = lote;
	}

	public ArticlesPointsXMLBean getArticlePoints() {
		return articlePoints;
	}

	public void setArticlePoints(ArticlesPointsXMLBean articlePoints) {
		this.articlePoints = articlePoints;
	}

	public PromotionalStickerXML getPegatinaPromocional() {
		return pegatinaPromocional;
	}

	public void setPegatinaPromocional(PromotionalStickerXML pegatinaPromocional) {
		this.pegatinaPromocional = pegatinaPromocional;
	}

	public List<AuditoriaLineaTicket> getAuditorias() {
		return auditorias;
	}

	public void setAuditorias(List<AuditoriaLineaTicket> auditorias) {
		this.auditorias = auditorias;
	}

	public List<DtoPromocion> getDtoPromociones() {
		return dtoPromociones;
	}

	public void setDtoPromociones(List<DtoPromocion> dtoPromociones) {
		this.dtoPromociones = dtoPromociones;
	}

	public List<DetallesTrazabilidadDto> getDetallesTrazabilidad() {
		return detallesTrazabilidad;
	}

	public void setDetallesTrazabilidad(List<DetallesTrazabilidadDto> detallesTrazabilidad) {
		this.detallesTrazabilidad = detallesTrazabilidad;
	}
	
	public boolean isRequiereContrato() {
		return requiereContrato;
	}

	public void setRequiereContrato(boolean requiereContrato) {
		this.requiereContrato = requiereContrato;
	}

	public boolean isRequiereIdentificacion() {
		return requiereIdentificacion;
	}

	public void setRequiereIdentificacion(boolean requiereIdentificacion) {
		this.requiereIdentificacion = requiereIdentificacion;
	}

	public ItemsPetsIdent getItemsPetIdent() {
		return itemsPetIdent;
	}

	public void setItemsPetIdent(ItemsPetsIdent itemsPetIdent) {
		this.itemsPetIdent = itemsPetIdent;
	}

	public boolean isRequiereMascotaFacturaCompleta() {
		return isRequiereContrato() || isRequiereIdentificacion();
	}

	public boolean isImprimir() {
		return imprimir == null || imprimir;
	}

	public void setImprimir(boolean imprimir) {
		this.imprimir = imprimir;
	}

	public Inyectable getInyectable() {
		return inyectable;
	}

	public void setInyectable(Inyectable inyectable) {
		this.inyectable = inyectable;
	}

	public boolean isPlanSalud() {
		return Boolean.TRUE.equals(planSalud);
	}

	public void setPlanSalud(Boolean planSalud) {
		this.planSalud = planSalud;
	}
}
