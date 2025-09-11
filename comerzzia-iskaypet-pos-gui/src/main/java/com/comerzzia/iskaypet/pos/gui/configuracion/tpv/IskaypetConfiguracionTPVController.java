package com.comerzzia.iskaypet.pos.gui.configuracion.tpv;

import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraCajaView;
import com.comerzzia.iskaypet.pos.services.impresion.IskaypetServicioImpresion;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.configuracion.tpv.ConfiguracionTPVController;
import com.comerzzia.pos.persistence.almacenes.AlmacenBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController.*;

@Primary
@Component
public class IskaypetConfiguracionTPVController extends ConfiguracionTPVController {

    protected static final String PLANTILLAS_IMPRESION_PRUEBAS = "impresion_prueba";
    protected static final String PARAMETRO_UID_ACTIVIDAD = "uidActividad";

    @FXML
    protected Button btImpresionPrueba;

    @FXML
    protected Button btAbrirCajonPrueba;

    @Autowired
    protected VariablesServices variablesServices;

    @Autowired
    protected Sesion sesion;

    @Override
    public void initializeForm() {
        super.initializeForm();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @FXML
    public void accionBtGenerales() {
        super.accionBtGenerales();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @FXML
    public void accionBtImpresora() {
        super.accionBtImpresora();
        this.btImpresionPrueba.setVisible(true);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @FXML
    public void accionBtImpresora2() {
        super.accionBtImpresora2();
        this.btImpresionPrueba.setVisible(true);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @FXML
    public void accionBtFidelizacion() {
        super.accionBtFidelizacion();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @FXML
    public void accionBtVisor() {
        super.accionBtVisor();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @FXML
    public void accionBtCajon() {
        super.accionBtCajon();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(true);
    }

    @Override
    public void accionBtBalanza() {
        super.accionBtBalanza();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @Override
    public void accionBtRecargaMovil() {
        super.accionBtRecargaMovil();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(false);
    }

    @Override
    public void accionBtEscaner() {
        super.accionBtEscaner();
        this.btImpresionPrueba.setVisible(false);
        this.btAbrirCajonPrueba.setVisible(false);
    }


    @FXML
    public void imprimirPrueba() throws DeviceException {

        Map<String, Object> parametros = new HashMap<>();

        parametros.put(PARAMETRO_UID_ACTIVIDAD, sesion.getAplicacion().getUidActividad());


        EmpresaBean empresa = sesion.getAplicacion().getEmpresa();
        parametros.put(EMPRESA, empresa);

        AlmacenBean tienda = sesion.getAplicacion().getTienda().getAlmacenBean();
        parametros.put("tienda", tienda);

        parametros.put(IMPRIMIR_LOGO, requierImprimirLogo(variablesServices));

        IPrinter printer = null;
        if (opcionSeleccionada.equals(OPCION_IMPRESORA1)) {
            printer = Dispositivos.getInstance().getImpresora1();
        } else if (opcionSeleccionada.equals(OPCION_IMPRESORA2)) {
            printer = Dispositivos.getInstance().getImpresora2();
        }

        String mensaje = I18N.getTexto("Para continuar con la impresión de prueba, por favor asegúrate de haber" +
                " guardado previamente la configuración y de haber reiniciado el POS. ¿Estás seguro de que deseas proceder" +
                " con la impresión?");

        if (VentanaDialogoComponent.crearVentanaConfirmacion( mensaje, getStage())) {
            // Llamamos al servicio de impresión
            IskaypetServicioImpresion.imprimir(printer, PLANTILLAS_IMPRESION_PRUEBAS, parametros);
        }

    }

    @FXML
    public void abrirCajonPrueba() throws InitializeGuiException {
        try {
            String mensaje = I18N.getTexto("Para continuar con la apertura del cajón de prueba, por favor asegúrate de haber" +
                    " guardado previamente la configuración y de haber reiniciado el POS. ¿Estás seguro de que deseas proceder" +
                    " con la apertura del cajón?");
            if (VentanaDialogoComponent.crearVentanaConfirmacion( mensaje, getStage())) {
                HashMap<String, Object> datos = new HashMap<>();
                datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, false);
                AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
                super.abrirCajon();
            }
        } catch (InitializeGuiException initializeGuiException) {
            if (initializeGuiException.isMostrarError()) {
                VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
            }
        }
    }



}
