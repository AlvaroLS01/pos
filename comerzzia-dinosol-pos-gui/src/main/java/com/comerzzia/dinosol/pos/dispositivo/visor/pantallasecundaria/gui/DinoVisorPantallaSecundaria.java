package com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.model.notificaciones.Notificacion;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.cupones.PantallaSecundariaInfoCuponesController;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.cupones.PantallaSecundariaInfoCuponesView;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.encuestas.PantallaSecundariaEncuestasController;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.encuestas.PantallaSecundariaEncuestasView;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.enviodomicilio.PantallaSecundariaEnvioDomicilioController;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.enviodomicilio.PantallaSecundariaEnvioDomicilioView;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.promociones.opciones.PantallaSecundariaOpcionesPromocionController;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.promociones.opciones.PantallaSecundariaOpcionesPromocionView;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.sorteos.PantallaSecundariaSorteosController;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.sorteos.PantallaSecundariaSorteosView;
import com.comerzzia.dinosol.pos.persistence.encuestas.Encuesta;
import com.comerzzia.dinosol.pos.persistence.encuestas.preguntas.PreguntaEncuesta;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasFechasDisponiblesBean;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionPromocionesDto;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.dispositivo.visor.VisorPantallaSecundaria;
import com.comerzzia.pos.services.notificaciones.Notificaciones;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.stage.Screen;

@Primary
@Component
public class DinoVisorPantallaSecundaria extends VisorPantallaSecundaria {

	protected static final Logger log = Logger.getLogger(DinoVisorPantallaSecundaria.class.getName());

	public static int MODO_ENVIO_DOMICILIO = 5;

	public static int MODO_INFORMACION_CUPONES = 7;

	public static int MODO_ENCUESTA = 12;

	public static int MODO_OPCIONES_PROMOCION = 15;
	
	public static int MODO_PARTICIPACIONES_ANIVERSARIO = 20;

	private PantallaSecundariaEnvioDomicilioView envioDomicilioView;
	
	private PantallaSecundariaInfoCuponesView infoCuponesView;
	
	private PantallaSecundariaEncuestasView encuestasView;
	
	private PantallaSecundariaOpcionesPromocionView opcionesPromocionView;
	
	private PantallaSecundariaSorteosView sorteosView;

	protected static List<RutasFechasDisponiblesBean> disponible;

	private static List<String> cuponesAplicados;

	private static List<String> cuponesNoAplicados;
	
	private static Encuesta encuesta;
	
	private static PreguntaEncuesta preguntaEncuesta;

	private static Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpcionesPromociones;

	public void modoEnvioDomicilio(List<RutasFechasDisponiblesBean> disponibilidad) {
		try {
			if (envioDomicilioView == null) {
				if (Screen.getScreens().size() > 1) {
					if (getEstado() == ENCENDIDO) {
						try {
							setDisponibilidad(disponibilidad);
							setModo(MODO_ENVIO_DOMICILIO);
							envioDomicilioView = SpringContext.getBean(PantallaSecundariaEnvioDomicilioView.class);
							envioDomicilioView.loadAndInitialize();
							scene.setRoot(envioDomicilioView.getViewNode());
							//stage.show();
							((PantallaSecundariaEnvioDomicilioController) envioDomicilioView.getController()).refrescarDatosPantalla();
						}
						catch (InitializeGuiException e) {
							log.error("modoPago() Error cargando vista:" + e.getMessage(), e);
						}
					}
					else {
						conecta();
					}
				}
				else {
					if (getEstado() == ENCENDIDO) {
						Notificaciones.get().addNotification(new Notificacion(I18N.getTexto("Compruebe que el segundo monitor está encendido."), Notificacion.Tipo.WARN, new Date()));
					}
					setEstado(APAGADO);
				}
			}
			else {
				scene.setRoot(envioDomicilioView.getViewNode());
				setDisponibilidad(disponibilidad);
				setModo(MODO_ENVIO_DOMICILIO);
				((PantallaSecundariaEnvioDomicilioController) envioDomicilioView.getController()).refrescarDatosPantalla();
			}
		}
		catch (Exception e) {
			log.error("modoEnvioDomicilio() - Ha habido un error al mostrar el modo envio domicilio en la pantalla secundaria: " + e.getMessage(), e);
		}
	}

	public void modoInfoCupones(List<String> cuponesAplicados, List<String> cuponesNoAplicados) {
		try {
			if (infoCuponesView == null) {
				if (Screen.getScreens().size() > 1) {
					if (getEstado() == ENCENDIDO) {
						try {
							setCuponesAplicados(cuponesAplicados);
							setCuponesNoAplicados(cuponesNoAplicados);
							setModo(MODO_INFORMACION_CUPONES);
							infoCuponesView = SpringContext.getBean(PantallaSecundariaInfoCuponesView.class);
							infoCuponesView.loadAndInitialize();
							scene.setRoot(infoCuponesView.getViewNode());
							//stage.show();
							((PantallaSecundariaInfoCuponesController) infoCuponesView.getController()).refrescarDatosPantalla();
						}
						catch (InitializeGuiException e) {
							log.error("modoInfoCupones() Error cargando vista:" + e.getMessage(), e);
						}
					}
					else {
						scene.setRoot(infoCuponesView.getViewNode());
						setCuponesAplicados(cuponesAplicados);
						setCuponesNoAplicados(cuponesNoAplicados);
						setModo(MODO_ENVIO_DOMICILIO);
						((PantallaSecundariaInfoCuponesController) infoCuponesView.getController()).refrescarDatosPantalla();
					}
				}
				else {
					if (getEstado() == ENCENDIDO) {
						Notificaciones.get().addNotification(new Notificacion(I18N.getTexto("Compruebe que el segundo monitor está encendido."), Notificacion.Tipo.WARN, new Date()));
					}
					setEstado(APAGADO);
				}
			}
			else {
				scene.setRoot(infoCuponesView.getViewNode());
				setCuponesAplicados(cuponesAplicados);
				setCuponesNoAplicados(cuponesNoAplicados);
				setModo(MODO_INFORMACION_CUPONES);
				((PantallaSecundariaInfoCuponesController) infoCuponesView.getController()).refrescarDatosPantalla();
			}
		}
		catch (Exception e) {
			log.error("modoPago() - Ha habido un error al mostrar la pantalla de información de cupones en la pantalla secundaria: " + e.getMessage(), e);
		}
	}
	
	public void modoEncuesta(Encuesta encuesta, PreguntaEncuesta preguntaEncuesta) {
		try {
			if (encuestasView == null) {
				if (Screen.getScreens().size() > 1) {
					if (getEstado() == ENCENDIDO) {
						try {
							setEncuesta(encuesta);
							setPreguntaEncuesta(preguntaEncuesta);
							
							setModo(MODO_ENCUESTA);
							encuestasView = SpringContext.getBean(PantallaSecundariaEncuestasView.class);
							encuestasView.loadAndInitialize();
							scene.setRoot(encuestasView.getViewNode());
							((PantallaSecundariaEncuestasController) encuestasView.getController()).refrescarDatosPantalla();
						}
						catch (InitializeGuiException e) {
							log.error("modoEncuesta() Error cargando vista:" + e.getMessage(), e);
						}
					}
					else {
						setEncuesta(encuesta);
						setPreguntaEncuesta(preguntaEncuesta);
						
						scene.setRoot(encuestasView.getViewNode());
						setModo(MODO_ENCUESTA);
						((PantallaSecundariaEncuestasController) encuestasView.getController()).refrescarDatosPantalla();
					}
				}
				else {
					if (getEstado() == ENCENDIDO) {
						Notificaciones.get().addNotification(new Notificacion(I18N.getTexto("Compruebe que el segundo monitor está encendido."), Notificacion.Tipo.WARN, new Date()));
					}
					setEstado(APAGADO);
				}
			}
			else {
				scene.setRoot(encuestasView.getViewNode());
				setEncuesta(encuesta);
				setPreguntaEncuesta(preguntaEncuesta);
				setModo(MODO_ENCUESTA);
				((PantallaSecundariaEncuestasController) encuestasView.getController()).refrescarDatosPantalla();
			}
		}
		catch(Exception e) {
			log.error("modoEncuesta() - Ha habido un error al mostrar la pantalla de encuesta para el cliente: " + e.getMessage(), e);
		}
	}

	public void modoOpcionesPromocion(Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones) {
		try {
			if (encuestasView == null) {
				if (Screen.getScreens().size() > 1) {
					if (getEstado() == ENCENDIDO) {
						try {
							setResultadoOpcionesPromociones(resultadoOpciones);
							
							setModo(MODO_OPCIONES_PROMOCION);
							opcionesPromocionView = SpringContext.getBean(PantallaSecundariaOpcionesPromocionView.class);
							opcionesPromocionView.loadAndInitialize();
							scene.setRoot(opcionesPromocionView.getViewNode());
							((PantallaSecundariaOpcionesPromocionController) opcionesPromocionView.getController()).refrescarDatosPantalla();
						}
						catch (InitializeGuiException e) {
							log.error("modoOpcionesPromocion() Error cargando vista:" + e.getMessage(), e);
						}
					}
					else {
						setResultadoOpcionesPromociones(resultadoOpciones);
						
						scene.setRoot(opcionesPromocionView.getViewNode());
						setModo(MODO_OPCIONES_PROMOCION);
						((PantallaSecundariaOpcionesPromocionController) opcionesPromocionView.getController()).refrescarDatosPantalla();
					}
				}
				else {
					if (getEstado() == ENCENDIDO) {
						Notificaciones.get().addNotification(new Notificacion(I18N.getTexto("Compruebe que el segundo monitor está encendido."), Notificacion.Tipo.WARN, new Date()));
					}
					setEstado(APAGADO);
				}
			}
			else {
				scene.setRoot(opcionesPromocionView.getViewNode());
				setResultadoOpcionesPromociones(resultadoOpciones);
				setModo(MODO_OPCIONES_PROMOCION);
				((PantallaSecundariaOpcionesPromocionController) opcionesPromocionView.getController()).refrescarDatosPantalla();
			}
		}
		catch(Exception e) {
			log.error("modoOpcionesPromocion() - Ha habido un error al mostrar la pantalla de encuesta para el cliente: " + e.getMessage(), e);
		}
	}
	
	public void modoParticipacionesAniversario(DinoTicketVentaAbono ticket) {
		try {
			if (sorteosView == null) {
				if (Screen.getScreens().size() > 1) {
					if (getEstado() == ENCENDIDO) {
						try {
							setModo(MODO_PARTICIPACIONES_ANIVERSARIO);
							sorteosView = SpringContext.getBean(PantallaSecundariaSorteosView.class);
							sorteosView.loadAndInitialize();
							scene.setRoot(sorteosView.getViewNode());
							((PantallaSecundariaSorteosController) sorteosView.getController()).pintarMensaje(ticket);
						}
						catch (InitializeGuiException e) {
							log.error("modoOpcionesPromocion() Error cargando vista:" + e.getMessage(), e);
						}
					}
					else {

						scene.setRoot(sorteosView.getViewNode());
						setModo(MODO_PARTICIPACIONES_ANIVERSARIO);
						((PantallaSecundariaSorteosController) sorteosView.getController()).pintarMensaje(ticket);
					}
				}
				else {
					if (getEstado() == ENCENDIDO) {
						Notificaciones.get().addNotification(new Notificacion(I18N.getTexto("Compruebe que el segundo monitor está encendido."), Notificacion.Tipo.WARN, new Date()));
					}
					setEstado(APAGADO);
				}
			}
			else {
				scene.setRoot(sorteosView.getViewNode());
				setModo(MODO_PARTICIPACIONES_ANIVERSARIO);
				((PantallaSecundariaSorteosController) sorteosView.getController()).pintarMensaje(ticket);
			}
		}
		catch (Exception e) {
			log.error("modoParticipacionesAniversario() - Ha habido un error al mostrar la pantalla de aniversario para el cliente: " + e.getMessage(), e);
		}
	}

	public static List<RutasFechasDisponiblesBean> getDisponibilidad() {
		return disponible;
	}

	public void setDisponibilidad(List<RutasFechasDisponiblesBean> disponible) {
		DinoVisorPantallaSecundaria.disponible = disponible;
	}

	public static List<String> getCuponesAplicados() {
		return cuponesAplicados;
	}

	public static void setCuponesAplicados(List<String> cuponesAplicados) {
		DinoVisorPantallaSecundaria.cuponesAplicados = cuponesAplicados;
	}

	public static List<String> getCuponesNoAplicados() {
		return cuponesNoAplicados;
	}

	public static void setCuponesNoAplicados(List<String> cuponesNoAplicados) {
		DinoVisorPantallaSecundaria.cuponesNoAplicados = cuponesNoAplicados;
	}

	public static Encuesta getEncuesta() {
		return encuesta;
	}

	public static void setEncuesta(Encuesta encuesta) {
		DinoVisorPantallaSecundaria.encuesta = encuesta;
	}

	public static PreguntaEncuesta getPreguntaEncuesta() {
		return preguntaEncuesta;
	}

	public static void setPreguntaEncuesta(PreguntaEncuesta preguntaEncuesta) {
		DinoVisorPantallaSecundaria.preguntaEncuesta = preguntaEncuesta;
	}
	
	public void setRespuestaMarcada(String respuestaMarcada) {
		try {
			((PantallaSecundariaEncuestasController) encuestasView.getController()).marcarOpcion(respuestaMarcada);
		}
		catch (Exception e) {
			log.error("setRespuestaMarcada() - No se ha podido seleccionar la respuesta: " + e.getMessage(), e);
		}
	}
	
	public static Map<OpcionPromocionesDto, DocumentoPromocionable> getResultadoOpcionesPromociones() {
		return resultadoOpcionesPromociones;
	}

	public static void setResultadoOpcionesPromociones(Map<OpcionPromocionesDto, DocumentoPromocionable> resultadoOpciones) {
		DinoVisorPantallaSecundaria.resultadoOpcionesPromociones = resultadoOpciones;
	}

}
