package com.comerzzia.iskaypet.pos.core.gui;

import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.iskaypet.pos.services.variables.IskaypetVariablesServices;
import com.comerzzia.pos.core.dispositivos.ConfigDispositivosLoadException;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.ApplicationListener;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.POSPreloader;
import com.comerzzia.pos.core.gui.POSURLHandler;
import com.comerzzia.pos.core.gui.componentes.simboloCargando.VentanaCargando;
import com.comerzzia.pos.core.gui.login.LoginView;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuariosView;
import com.comerzzia.pos.core.gui.main.MainView;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.view.View;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.config.configContadores.ContadoresConfigException;
import com.comerzzia.pos.services.core.config.configContadores.ContadoresConfigNotFoundException;
import com.comerzzia.pos.services.core.config.configContadores.ServicioConfigContadores;
import com.comerzzia.pos.services.core.config.configContadores.parametros.ConfigContadoresParametrosException;
import com.comerzzia.pos.services.core.config.configContadores.rangos.ConfigContadoresRangosConstraintViolationException;
import com.comerzzia.pos.services.core.config.configContadores.rangos.ConfigContadoresRangosException;
import com.comerzzia.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.notificaciones.Notificaciones;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.sun.javafx.application.LauncherImpl;
import com.sun.javafx.runtime.VersionInfo;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.*;

@Primary
@Component
public class IskaypetPOSApplication extends POSApplication {

    private static final Logger log = Logger.getLogger(IskaypetPOSApplication.class.getName());
    private Sesion sesion;
    private static final String TIEMPO_INACTIVIDAD = "X_POS.TIEMPO_SEGUNDOS_INACTIVIDAD";

    public static void main(String[] args) {
        boolean alreadyRunning = true;

        try {
            JUnique.acquireLock("comerzzia/jpos");
            alreadyRunning = false;
        } catch (AlreadyLockedException var5) {
        }

        if (alreadyRunning) {
            log.error("comerzzia POS already running. Aborting.");
            System.exit(100);
        }

        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    log.error("Error no controlado en thread: " + t + " Exception: " + e, e);
                }
            });
            log.info("comerzzia POS with JavaFX version: " + VersionInfo.getRuntimeVersion());
            Class<? extends Application> applicationClass = IskaypetPOSApplication.class;
            if (AppConfig.applicationClassName != null) {
                applicationClass = (Class<? extends Application>) Class.forName(AppConfig.applicationClassName);
            }

            if (AppConfig.mostrarPreloader) {
                Class<? extends Preloader> preloaderClass = POSPreloader.class;
                if (AppConfig.preloaderClassName != null) {
                    preloaderClass = (Class<? extends Preloader>) Class.forName(AppConfig.preloaderClassName);
                }

                LauncherImpl.launchApplication(applicationClass, preloaderClass, args);
            } else {
                LauncherImpl.launchApplication(applicationClass, args);
            }
        } catch (Exception e) {
            log.fatal("load() - Error al iniciar la aplicación : " + e.getMessage(), e);
        }

    }

    @Override
    public void init() {
        instance = this;

        try {
            long time = System.currentTimeMillis();
            rootContext = new ClassPathXmlApplicationContext("classpath*:comerzzia-*context.xml");
            log.debug(String.format("main() - Spring context inicializado en %d milisegundos", System.currentTimeMillis() - time));
            applicationListeners = searchApplicationListeners();

            for(ApplicationListener appListener : applicationListeners) {
                try {
                    appListener.onBeforeApplicationInit();
                } catch (Exception e1) {
                    log.error("init() - Error al iniciar applicationListener", e1);
                }
            }

            locale = new Locale(AppConfig.idioma, AppConfig.pais);
            FormatUtil formatUtil = FormatUtil.getInstance();
            formatUtil.init(locale);
            log.debug("init() - Iniciando formateadores y Sesion");
            CellFactoryBuilder.getInstance();
            loadFonts();
            sesion = SpringContext.getBean(Sesion.class);
            sesion.initAplicacion();
            Notificaciones.get().init();
            notificarCaducidadDocumentos();
            Dispositivos dispositivos = Dispositivos.getInstance();

            try {
                dispositivos.leerDispositivosDisponibles();
            } catch (ConfigDispositivosLoadException e) {
                log.error(e.getMessage(), e);
                String error = I18N.getTexto("Ha habido errores al cargar la configuración de dispositivos disponibles. Por favor, revise el archivo devices.xml");
                Notificaciones.get().addNotification(new Notificacion(error, Notificacion.Tipo.WARN, new Date()));
            }

            try {
                dispositivos.cargarConfiguracionDispositivos(sesion.getAplicacion().getTiendaCaja().getConfiguracion());
            } catch (ConfigDispositivosLoadException var12) {
                if (sesion.getAplicacion().getTiendaCaja().getConfiguracion() == null) {
                    var12.getErrores().add(0, I18N.getTexto("Aún no se han configurado los dispositivos"));
                }

                for(String error : var12.getErrores()) {
                    Notificaciones.get().addNotification(new Notificacion(error, Notificacion.Tipo.WARN, new Date()));
                }
            }

            VariablesServices variablesServices = SpringContext.getBean(VariablesServices.class);
            Dispositivos.getInstance().getFidelizacion().setApikey(variablesServices.getVariableAsString("WEBSERVICES.APIKEY"));
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    if (!protocol.equalsIgnoreCase("cmzpos") && !protocol.equalsIgnoreCase("czzpos")) {
                        return null;
                    } else {
                        log.debug(String.format("createURLStreamHandler() - url con protocolo '%s' detectado", protocol));
                        return new POSURLHandler();
                    }
                }
            });
            visor = Dispositivos.getInstance().getVisor();

            for(ApplicationListener appListener : applicationListeners) {
                try {
                    appListener.onAfterApplicationInit();
                } catch (Exception e1) {
                    log.error("init() - Error al ejecutar applicationListener", e1);
                }
            }
        } catch (Exception e) {
            log.error("init() " + e.getMessage(), e);
            initException = e;
        }

    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            for(ApplicationListener appListener : applicationListeners) {
                try {
                    appListener.onBeforeApplicationStart();
                } catch (Exception e1) {
                    log.error("start() - Error al ejecutar applicationListener", e1);
                }
            }

            log.debug("start() - Iniciando Aplicación");
            stage = primaryStage;
            setBaseTheme();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent arg0) {
                    log.trace("Se intentó cerrar la aplicación desde Windows.");
                    mainView.close();
                    arg0.consume();
                }
            });
            VentanaCargando.crearVentanaCargando(stage);
            if (checkInitExceptions()) {
                return;
            }

            if (checkUidTpvInUse()) {
                return;
            }

            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Comerzzia POS");
            stage.getIcons().add(createImage("logos/logo_comerzzia_barra.png"));
            log.debug("start() - estableciendo tamaño de la página");
            setStageSize();
            mainView = (MainView) View.loadView(SpringContext.getBean(MainView.class));
            mainView.loadFXML();
            primaryStage.show();
            if (AppConfig.loginBotonera) {
                try {
                    showFullScreenView(SpringContext.getBean(SeleccionUsuariosView.class));
                } catch (NoSuchBeanDefinitionException var7) {
                    log.debug("No se ha encontrado pantalla de selección de usuarios.");
                }
            } else {
                try {
                    showFullScreenView(SpringContext.getBean(LoginView.class));
                } catch (NoSuchBeanDefinitionException var6) {
                    log.debug("No se ha encontrado pantalla de login.");
                }
            }

            int segundosInactividad = IskaypetVariablesServices.get()
                    .getVariableAsInteger(TIEMPO_INACTIVIDAD) != null ?
                    IskaypetVariablesServices.get().getVariableAsInteger(TIEMPO_INACTIVIDAD) : 0;

            log.info("Segundos de inactividad: " + segundosInactividad);

            if (segundosInactividad > 0) {
                int time = segundosInactividad * 1000;
                timer = new Timer(time, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (sesion.getSesionUsuario() != null) {
                            if (isLoginWindowShowing()) {
                                return;
                            }

                            if (isModalDialogShowing()) {
                                log.debug("Application en ventana de dialogo. No se puede bloquear");
                                return;
                            }

                            Platform.runLater(new Runnable() {
                                public void run() {
                                    log.debug(segundosInactividad + " segundo(s) de inactividad. Se cierra la sesión.");

                                    try {
                                        timer.stop();
                                        HashMap<String, Object> parametros = new HashMap();
                                        if (AppConfig.loginBotonera) {
                                            SeleccionUsuariosView view = SpringContext.getBean(SeleccionUsuariosView.class);
                                            parametros.put("MODO_PANTALLA_BLOQUEO", "S");
                                            mainView.showModal(view, parametros);
                                        } else {
                                            LoginView loginView = SpringContext.getBean(LoginView.class);
                                            parametros.put("MODO_BLOQUEO", "S");
                                            mainView.showModal(loginView, parametros);
                                        }
                                    } finally {
                                        timer.start();
                                    }

                                }
                            });
                        }

                    }
                });
                stage.addEventFilter(Event.ANY, new EventHandler<Event>() {
                    public void handle(Event arg0) {
                        if (timer.isRunning()) {
                            timer.restart();
                        }
                    }
                });
                timer.start();
            }

            addEnterKeyListener();
            Platform.runLater(new Runnable() {
                public void run() {
                    Dispositivos.getInstance().conectarDispositivos();
                }
            });

            for(ApplicationListener appListener : applicationListeners) {
                try {
                    appListener.onAfterApplicationStart();
                } catch (Exception e1) {
                    log.error("main() - Error al comenzar applicationListener", e1);
                }
            }
        } catch (Throwable ex) {
            log.error("start() " + ex.getMessage(), ex);
            initException = ex;
            checkInitExceptions();
        }

    }

    protected void notificarCaducidadDocumentos() throws ConfigContadoresRangosConstraintViolationException, ConfigContadoresRangosException, ContadoresConfigException {
        ServicioContadores servicioContadores = SpringContext.getBean(ServicioContadores.class);
        ServicioConfigContadores servicioConfigContadores = SpringContext.getBean(ServicioConfigContadores.class);
        ServicioConfigContadoresRangos servicioConfigContadoresRangos = SpringContext.getBean(ServicioConfigContadoresRangos.class);
        Collection<TipoDocumentoBean> tiposDocumentos = sesion.getAplicacion().getDocumentos().getDocumentos().values();
        Map<String, String> parametrosContador = new HashMap();
        Map<String, String> condicionesVigencias = new HashMap();
        parametrosContador.put("CODEMP", sesion.getAplicacion().getEmpresa().getCodEmpresa());
        parametrosContador.put("CODALM", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
        parametrosContador.put("CODSERIE", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
        parametrosContador.put("CODCAJA", sesion.getAplicacion().getCodCaja());
        parametrosContador.put("PERIODO", (new Fecha()).getAño().toString());
        condicionesVigencias.put("codCaja", sesion.getAplicacion().getCodCaja());
        condicionesVigencias.put("codAlm", sesion.getAplicacion().getCodAlmacen());
        condicionesVigencias.put("codEmp", sesion.getAplicacion().getEmpresa().getCodEmpresa());
        Date hoy = new Date();

        try {
            for(TipoDocumentoBean tipoDocumento : tiposDocumentos) {
                parametrosContador.put("CODTIPODOCUMENTO", tipoDocumento.getCodtipodocumento());
                ConfigContadorBean configContador = servicioConfigContadores.consultar(tipoDocumento.getIdContador());
                ConfigContadorRangoExample example = new ConfigContadorRangoExample();
                example.or().andIdContadorEqualTo(configContador.getIdContador());
                example.setOrderByClause("RANGO_INICIO, RANGO_FIN, RANGO_FECHA_INICIO, RANGO_FECHA_FIN");
                List<ConfigContadorRangoBean> rangos = servicioConfigContadoresRangos.consultar(example);
                configContador.setRangos(rangos);
                configContador.setRangosCargados(true);
                if (!configContador.getRangos().isEmpty()) {
                    ContadorBean contador = servicioContadores.consultarContadorActivo(configContador, parametrosContador, condicionesVigencias, sesion.getAplicacion().getUidActividad(), false);
                    ConfigContadorRangoBean rango = contador.getConfigContadorRango();
                    if (rango != null) {
                        Date proximoAviso = rango.getProximoAvisoFecha();
                        if (proximoAviso != null && Fechas.equalsDate(proximoAviso, hoy)) {
                            String msg = I18N.getTexto("La fecha autorizada para la vigencia de {0} caduca el día {1}. Por favor, solicite una nueva autorización a la entidad competente", new Object[]{tipoDocumento.getDestipodocumento(), rango.getRangoFechaFin()});
                            Notificacion notif = new Notificacion(msg, Notificacion.Tipo.WARN);
                            Notificaciones.get().addNotification(notif);
                            if (rango.getRangoFechaUltimoAviso() == null || !Fechas.equalsDate(hoy, rango.getRangoFechaUltimoAviso())) {
                                rango.setRangoFechaUltimoAviso(new Date());
                                rango.setEstadoBean(1);
                                servicioConfigContadoresRangos.salvar(rango);
                            }
                        }
                    }
                }
            }
        } catch (ConfigContadoresParametrosException | ContadorServiceException | ContadoresConfigNotFoundException e) {
            log.error("init() - Error obteniendo los contadores", e);
        }

    }
}
