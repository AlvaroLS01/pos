package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables;

import com.comerzzia.core.model.clases.parametros.valoresobjetos.ValorParametroObjeto;
import com.comerzzia.core.servicios.clases.parametros.valoresobjeto.ValoresParametrosObjetosService;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InyectableArticuloManager {

    public static final String CLASE_PARAMETRO_CODART = "D_ARTICULOS_TBL.CODART";

    public static final String VARIABLE_PROPIEDAD_INYECTABLE = "X_POS.PROPIEDAD_INYECTABLE";
    public static final String VALOR_DEFECTO_PROPIEDAD_INYECTABLE = "inyectable";
    public static final String VARIABLE_PEDIR_INYECTABLE = "X_POS.PEDIR_INYECTABLE";

    private static final Logger log = Logger.getLogger(InyectableArticuloManager.class);

    @Autowired
    private ValoresParametrosObjetosService valoresParametrosObjetosService;

    @Autowired
    private VariablesServices variablesServices;

    @Autowired
    private Sesion sesion;

    /**
     * Comprueba si el artículo es inyectable
     *
     * @param codArt Código del artículo
     * @return true si el artículo es inyectable, false en caso contrario
     */
    public boolean esArticuloInyectable(String codArt) {
        log.debug("esArticuloInyectable() - Comprobando si el artículo es inyectable: " + codArt);

        try {
            DatosSesionBean datosSesion = new DatosSesionBean();
            datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());

            if (tiendaPideInyectable()) {
                log.debug("esArticuloInyectable() - La tienda pide inyectables");
                String propiedadInyectable = getValorVariablePropiedad(VARIABLE_PROPIEDAD_INYECTABLE, VALOR_DEFECTO_PROPIEDAD_INYECTABLE);
                List<ValorParametroObjeto> listaParametros = valoresParametrosObjetosService.consultarValoresParametrosPorClaseYObjeto(CLASE_PARAMETRO_CODART, codArt, datosSesion);
                for (ValorParametroObjeto param : listaParametros) {
                    if ((propiedadInyectable.equalsIgnoreCase(param.getParametro()) && "S".equalsIgnoreCase(param.getValor()))) {
                        log.debug("esArticuloInyectable() - El artículo " + codArt + " es inyectable");
                        return true;
                    }
                }
            }
            log.debug("esArticuloInyectable() - El artículo " + codArt + " no es inyectable");
            return false;
        } catch (Exception e) {
            log.error("esArticuloInyectable() - Error al comprobar si el artículo es inyectable: " + e.getMessage());
            return false;
        }
    }

    /**
     * Comprueba si la tienda pide inyectables
     *
     * @return true si la tienda pide inyectables, false en caso contrario
     */
    public boolean tiendaPideInyectable() {
        log.debug("tiendaPideInyectable() - Comprobando si la tienda pide inyectables");
        boolean pedirInyectable = false;
        String esInyectable = variablesServices.getVariableAsString(VARIABLE_PEDIR_INYECTABLE);
        if (StringUtils.isNotBlank(esInyectable)) {
            log.debug("tiendaPideInyectable() - La tienda pide inyectables: " + esInyectable);
            pedirInyectable = esInyectable.equalsIgnoreCase("S");
        }
        log.debug("tiendaPideInyectable() - La tienda pide inyectables: " + pedirInyectable);
        return pedirInyectable;
    }

    /**
     * Busca el valor de la variable en la propiedad de la configuración de la tienda
     *
     * @param idVariable   identificador de la variable
     * @param valorDefecto valor por defecto a devolver si no se encuentra la variable
     * @return valor de la variable o valor por defecto
     */
    public String getValorVariablePropiedad(String idVariable, String valorDefecto) {
        try {
            String valor = variablesServices.getVariableAsString(idVariable);
            if (StringUtils.isNotBlank(valor)) {
                log.debug("getValorVariablePropiedad() - La variable " + idVariable + " tiene valor: " + valor);
                return valor;
            }
        } catch (Exception e) {
            log.error("getValorVariablePropiedad() - Error al obtener la variable " + idVariable + ": " + e.getMessage());
        }
        log.debug("getValorVariablePropiedad() - La variable " + idVariable + " no tiene valor o no está definida, se asignará el valor por defecto: " + valorDefecto);
        return valorDefecto;
    }

    /**
     * Busca las líneas inyectables no insertadas en el ticket
     *
     * @param lineas líneas del ticket
     * @return mapa con el código del artículo como clave y la lista de líneas inyectables no insertadas como valor
     */
    public Map<String, List<LineaTicket>> getLineasInyectablesNoInsertadas(List<LineaTicket> lineas) {
        log.debug("getLineasInyectablesNoInsertadas() - Buscando líneas inyectables no insertadas");
        HashMap<String, List<LineaTicket>> inyectablesNoInsertados = new HashMap<>();
        List<String> codigosUnicos = lineas.stream()
                .map(LineaTicket::getCodArticulo)
                .distinct()
                .collect(Collectors.toList());

        for (String codArtLinea : codigosUnicos) {
            log.debug("getLineasInyectablesNoInsertadas() - Comprobando el artículo " + codArtLinea);
            boolean esInyectable = esArticuloInyectable(codArtLinea);
            if (esInyectable) {
                log.debug("getLineasInyectablesNoInsertadas() - El artículo " + codArtLinea + " es inyectable");
                List<LineaTicket> lineasSinInyectable = lineas.stream()
                        .filter(el -> el.getArticulo().getCodArticulo().equalsIgnoreCase(codArtLinea)
                                && el instanceof IskaypetLineaTicket
                                && !validarDatosLineaInyectable((IskaypetLineaTicket) el))
                        .collect(Collectors.toList());

                if (lineasSinInyectable.isEmpty()) {
                    log.debug("getLineasInyectablesNoInsertadas() - No hay líneas inyectables no insertadas para el artículo " + codArtLinea);
                    continue;
                }
                log.debug("getLineasInyectablesNoInsertadas() - Se han encontrado " + lineasSinInyectable.size() + " líneas inyectables no insertadas para el artículo " + codArtLinea);
                inyectablesNoInsertados.put(codArtLinea, lineasSinInyectable);
            }
        }
        log.debug("getLineasInyectablesNoInsertadas() - Se han encontrado " + inyectablesNoInsertados.size() + " artículos inyectables no insertados");
        return inyectablesNoInsertados;
    }

    /**
     * Valida los datos de la línea inyectable
     *
     * @param lineaTicket línea de ticket
     * @return true si los datos son válidos, false en caso contrario
     */
    private boolean validarDatosLineaInyectable(IskaypetLineaTicket lineaTicket) {
        log.debug("validarDatosLineaInyectable() - Validando datos de la línea inyectable");

        if (lineaTicket == null || lineaTicket.getInyectable() == null) {
            log.error("validarDatosLineaInyectable() - La línea de ticket o el inyectable son nulos");
            return false;
        }

        if (lineaTicket.getInyectable().getCantidadConvertida() == null || lineaTicket.getInyectable().getCantidadSuministrada() == null ||
                StringUtils.isBlank(lineaTicket.getInyectable().getUnidadMedidaSuministrada())) {
            log.error("validarDatosLineaInyectable() - Los datos del inyectable son nulos o vacíos");
            return false;
        }

        log.debug("validarDatosLineaInyectable() - Los datos del inyectable son válidos");
        return true;
    }
}
