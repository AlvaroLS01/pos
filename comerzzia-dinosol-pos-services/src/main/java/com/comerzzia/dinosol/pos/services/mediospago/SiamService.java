package com.comerzzia.dinosol.pos.services.mediospago;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.TefSiamManager;

@SuppressWarnings({"static-access", "unused"})
@Service
public class SiamService {
	
	private static final Logger log = Logger.getLogger(SiamService.class);
	
    enum estadoConexion {
        CONEXION,
        ABIERTA
    }
    private String tran_msg;
    private String terminal;
    private String tipoTran;
    private String tipoInterfaz;
    private String serverTEF;
    private String portTEF;
    private String longitudMensajesTCP;
    private Charset cs = StandardCharsets.UTF_8;
    private ReadWriteHandler readWriteHandler;
    private DatosConexion datosConexion;
    private Estados estados;
    private String mensajeTransaccion;

    // timers
    private String timerPeticion;
    private String timerCursada;
    private String timerConexion;
    private String timerEnvio;
    
    private static String Fi_config= "socketClient.properties";
    private static String Fi_Last_Tran="LASTRAN.DAT";
    private static String directorioLOG="LOG";

    // respuesta
    private String respuesta = "";
    private String respuestaOriginal = "";
    
    private String cliente;
    
    @Autowired
    protected ParseadorTransacciones transaccion;

    static class DatosConexion {
        AsynchronousSocketChannel conexion;
        estadoConexion EstadoConexion;
        ByteBuffer buffer;
        byte[] bloqueRecepcionTCP;
        String ultimoMensaje;
        Thread mainThread;
        boolean isRead;
    }

    // construye el mensaje a enviar indicado en el fichero de propiedades
    
    public SiamService (){
    	
    }
    
    public SiamService (String Term,
                       String Tran,
                       String Interfaz,
                       String server,
                       String port,
                       String mensajeTran,
                       String timerPet,
                       String timerCurs,
                       String timerConex,
                       String timerEnv,
                       String longMSGTCP)
	{
		terminal = Term;
		tipoTran = Tran;
		tipoInterfaz = Interfaz;
		serverTEF = server;
		portTEF = port;
		mensajeTransaccion = mensajeTran;
		timerPeticion = timerPet;
		timerCursada = timerCurs;
		timerConexion = timerConex;
		timerEnvio = timerEnv;
		longitudMensajesTCP = longMSGTCP;
		tran_msg = mensajeTran;

		readWriteHandler = new ReadWriteHandler();

		log.debug("server TEF: " + serverTEF + ":" + portTEF);
		log.debug("enviando transacción: " + Tran + " para terminal: " + Term);

		switch (tipoTran) {
			case "PP":
				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz;
				break;
			case "IN":
				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz;
				break;
			case "TC":
				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz + mensajeTransaccion;
				break;
			case "VT":
				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz + mensajeTransaccion;
				break;
			case "AN":
				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz + mensajeTransaccion;
				break;
//			case "AV":
//				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz + mensajeTransaccion;
//				break;
//			case "AD":
//				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz + mensajeTransaccion;
//				break;
			case "DV":
				tran_msg = terminal + estados.PETICION.getCodEstado() + tipoTran + tipoInterfaz + mensajeTransaccion;
				break;
		}

		log.debug("mensaje de peticion:" + tran_msg);
	}   
    
	public void init(String host, int puerto, int tout1, int tout2, String idTerminal, String cliente) throws IOException {
		this.serverTEF = host;
		this.portTEF = String.valueOf(puerto);
		this.timerConexion = String.valueOf(tout1);
		this.timerPeticion = String.valueOf(tout2);
		this.timerCursada = String.valueOf(tout2);
		this.timerEnvio = String.valueOf(tout2);
		this.terminal = idTerminal;
		this.cliente = cliente;
		this.longitudMensajesTCP = String.valueOf(TefSiamManager.LONGITUD_MSG_TCP);
		this.readWriteHandler = new ReadWriteHandler();
		
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion("siam_mensajes_error.properties");
		InputStream in = url.openStream();
		Properties prop = new Properties();
		prop.load(in);
	}
    
	public String getTran_msg() {
		return tran_msg;
	}

	public void setTran_msg(String tran_msg) {
		this.tran_msg = tran_msg;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getTipoTran() {
		return tipoTran;
	}

	public void setTipoTran(String tipoTran) {
		this.tipoTran = tipoTran;
	}

	public String getTipoInterfaz() {
		return tipoInterfaz;
	}

	public void setTipoInterfaz(String tipoInterfaz) {
		this.tipoInterfaz = tipoInterfaz;
	}

	public String getServerTEF() {
		return serverTEF;
	}

	public void setServerTEF(String serverTEF) {
		this.serverTEF = serverTEF;
	}

	public String getPortTEF() {
		return portTEF;
	}

	public void setPortTEF(String portTEF) {
		this.portTEF = portTEF;
	}

	public String getLongitudMensajesTCP() {
		return longitudMensajesTCP;
	}

	public void setLongitudMensajesTCP(String longitudMensajesTCP) {
		this.longitudMensajesTCP = longitudMensajesTCP;
	}

	public ReadWriteHandler getReadWriteHandler() {
		return readWriteHandler;
	}

	public void setReadWriteHandler(ReadWriteHandler readWriteHandler) {
		this.readWriteHandler = readWriteHandler;
	}

	public DatosConexion getDatosConexion() {
		return datosConexion;
	}

	public void setDatosConexion(DatosConexion datosConexion) {
		this.datosConexion = datosConexion;
	}

	public Estados getEstados() {
		return estados;
	}

	public void setEstados(Estados estados) {
		this.estados = estados;
	}

	public String getMensajeTransaccion() {
		return mensajeTransaccion;
	}

	public void setMensajeTransaccion(String mensajeTransaccion) {
		this.mensajeTransaccion = mensajeTransaccion;
	}

	public String getTimerPeticion() {
		return timerPeticion;
	}

	public void setTimerPeticion(String timerPeticion) {
		this.timerPeticion = timerPeticion;
	}

	public String getTimerCursada() {
		return timerCursada;
	}

	public void setTimerCursada(String timerCursada) {
		this.timerCursada = timerCursada;
	}

	public String getTimerConexion() {
		return timerConexion;
	}

	public void setTimerConexion(String timerConexion) {
		this.timerConexion = timerConexion;
	}

	public String getTimerEnvio() {
		return timerEnvio;
	}

	public void setTimerEnvio(String timerEnvio) {
		this.timerEnvio = timerEnvio;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	public String getRespuestaOriginal() {
		return respuestaOriginal;
	}

	public void setRespuestaOriginal(String respuestaOriginal) {
		this.respuestaOriginal = respuestaOriginal;
	}

	public ParseadorTransacciones getTransaccion() {
		return transaccion;
	}

	public void setTransaccion(ParseadorTransacciones transaccion) {
		this.transaccion = transaccion;
	}

	public void realizarConexion() throws Exception {
		log.debug("conectando...");
		try (AsynchronousSocketChannel conexion = AsynchronousSocketChannel.open()) {
			// lanza conexion
			SocketAddress serverAddr = new InetSocketAddress(serverTEF, Integer.parseInt(portTEF));
			Future<Void> result = conexion.connect(serverAddr);

			// espera el resultado, con timer
			result.get(Long.parseLong(timerConexion), TimeUnit.SECONDS);

			log.debug("realizarConexion() - conexión establecida");

			// asocia objeto datosConexion
			datosConexion = new DatosConexion();
			datosConexion.conexion = conexion;
			datosConexion.buffer = ByteBuffer.allocate(Integer.parseInt(longitudMensajesTCP));
			datosConexion.bloqueRecepcionTCP = new byte[0];
			datosConexion.isRead = false;
			datosConexion.mainThread = Thread.currentThread();

			// inicio dialogo inicial tras conexion
			datosConexion.EstadoConexion = estadoConexion.CONEXION;
			estados = Estados.ESPERA;
			
			log.debug("realizarConexion()- mandando peticion a SIAM");
			enviarPeticion(terminal);

			// espera finalización del proceso
			datosConexion.mainThread.join();

		}
		catch (InterruptedException ex) {
			log.error("finalizando transaccion: " + ex.getMessage());
		}
		catch (ExecutionException exe) {
			log.error("finalizando transaccion: " + exe.getMessage());
			throw new ExecutionException("ERROR LA RED ES INACCESIBLE", exe);
		}
		catch (Exception e) {
			log.error("ha fallado la conexion o envio de peticion por " + e.getMessage());
			respuesta = "KO0EX" + "ha fallado la conexion o envio de peticion por " + e.getMessage();
			throw new Exception(e);
		}
	}

    // añade 2 bytes con la longitud del mensaje
    byte[] addLongitud(String msg) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte)((msg.length() >> 8) & 0xFF);
        bytes[1] = (byte)(msg.length() & 0xFF);
        /* otra forma de hacerlo:
        byte[] bytes = new byte[2];
        byte[] bytesN = ByteBuffer.allocate(4).putInt(0,msg.length()).array();
        bytes[0]=bytesN[2];
        bytes[1]=bytesN[3];
        */
        return bytes;
    }

	void enviarPeticion(String msg) {
		log.debug("enviarPeticion()- preparando buffer para enviar: " + msg);

		// añade longitud
		byte[] data = addLongitud(msg);
		datosConexion.buffer.clear();
		datosConexion.buffer.put(data);

		// añade el mensaje
		data = msg.getBytes(cs);
		datosConexion.buffer.put(data);
		datosConexion.buffer.flip();

		// escribimos
		datosConexion.isRead = false;

		// envía por el socket
		datosConexion.conexion.write(datosConexion.buffer, Long.parseLong(timerConexion), TimeUnit.SECONDS, datosConexion, readWriteHandler);
	}

    /*
     proceso de la transaccion
     este controlador no es válido para múltiples sockets simultáneos.

     !!!!!!!SOLO TRATA UN SOCKET DE UN CLIENTE!!!!!!!!

     ***** INDISPENSABLE ******
     Hay que considerar la gestión de recepción de datos todas las posibilidades de
     agrupamiento que sabemos por experiencia que ocurren en lineas TCP
     sobretodo si la conexión es a un server remoto

     ***** OJO IMPORTANTE ******
     En ABIERTA, CURSADA, case "3", estamos simulando la respuesta por una variable
     en el fichero de propiedades "aceptarRespuesta" para aceptar o rechazar la respuesta obtenida

     En el caso real habría que asegurar que se entrega la respuesta antes de aceptarla

     */
    class ReadWriteHandler implements CompletionHandler<Integer, DatosConexion> {
        @Override
        public void completed(Integer result, DatosConexion datosConexion) {
            try {
                int mensajesCompletosLeidos;
                ArrayList<String> mensajesParaProcesar = new ArrayList<>();
                String mstr = new String(datosConexion.buffer.array(), cs);

                if (datosConexion.isRead) {
                    log.debug("ReadWriteHandler - completed() - recibido:" + mstr.substring(0, result) + " bytes:" + result);
                    log.debug("ReadWriteHandler - completed() - longitud en mensaje:" + (datosConexion.buffer.array()[0] * 256 + datosConexion.buffer.array()[1] + 256) + "/ result:" + result);

                    // Gestión de Bloques parciales

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(datosConexion.bloqueRecepcionTCP);
                    outputStream.write(datosConexion.buffer.array(),0,result);
                    datosConexion.bloqueRecepcionTCP = outputStream.toByteArray();
                   
                    if ((datosConexion.bloqueRecepcionTCP[0]*256 + datosConexion.bloqueRecepcionTCP[1] + 256) == datosConexion.bloqueRecepcionTCP.length-2) {
                        mstr = new String(datosConexion.bloqueRecepcionTCP, cs);
                        datosConexion.bloqueRecepcionTCP= new byte[0];
                        
                        log.debug("ReadWriteHandler - completed() - tratando bloque completo");
                        mensajesParaProcesar.add(mstr);
                       
                    } 
                    else if ((datosConexion.bloqueRecepcionTCP[0]*256 + datosConexion.bloqueRecepcionTCP[1] + 256) > datosConexion.bloqueRecepcionTCP.length-2) {
                        log.debug("ReadWriteHandler - completed() - recibido bloque parcial:"+ datosConexion.bloqueRecepcionTCP.length +" sobre "+(datosConexion.bloqueRecepcionTCP[0]*256 + datosConexion.bloqueRecepcionTCP[1] + 256) +" leemos mas..." );
                        
                        datosConexion.buffer.flip();
                        datosConexion.conexion.read(datosConexion.buffer, Long.parseLong(timerPeticion), TimeUnit.SECONDS, datosConexion, this);
                        
                        return;
                    } 
                    else {
                        //En el bloque hay mas de un mensaje
                        while (datosConexion.bloqueRecepcionTCP.length != 0 && (datosConexion.bloqueRecepcionTCP.length >= (2 + ((datosConexion.bloqueRecepcionTCP[0] * 256) + datosConexion.bloqueRecepcionTCP[1] + 256)))) {
                            int lBloque = (datosConexion.bloqueRecepcionTCP[0] * 256 + datosConexion.bloqueRecepcionTCP[1] + 256) + 2;
                            byte[] target = new byte[lBloque];
                            System.arraycopy(datosConexion.bloqueRecepcionTCP, 0, target, 0, lBloque);
                            mstr = new String(target, cs);
                            target = new byte[(datosConexion.bloqueRecepcionTCP.length - lBloque)];
                            System.arraycopy(datosConexion.bloqueRecepcionTCP, lBloque, target, 0, (datosConexion.bloqueRecepcionTCP.length - lBloque));
                            datosConexion.bloqueRecepcionTCP = target;
                            
                            mensajesParaProcesar.add(mstr);
                            
                            log.debug("ReadWriteHandler - completed() - queda parte de un bloque posterior por procesar:" + datosConexion.bloqueRecepcionTCP.length );
                        }
                    }
                    log.debug("ReadWriteHandler - completed() - mensajes para procesar:"+mensajesParaProcesar.size() );
                    //el ultimo mensaje debe lanzar lectura del próximo aún por venir si lo requiere,
                    //los previos NONONO porque ya tenemos el siguiente!!!
                    //lanzar envío no afecta porque el modelo es pregunta-respuesta o pregunta-varias respuestas y no se va a dar el caso
                    mensajesCompletosLeidos = mensajesParaProcesar.size() - 1;
                    for (String str : mensajesParaProcesar) {
                        log.debug("ReadWriteHandler - completed() - tratando bloque:" + str.trim());
                        switch (datosConexion.EstadoConexion) {
                        	
                            case CONEXION:
                                // leer la situación actual del terminal en el server TEF
                                // pag 10 doc. Interfaz_SIAM_TEF
                                switch (str.charAt(4)) {
                                	
                                    case '0':
                                        // enviar petición
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '0'");
                                        datosConexion.EstadoConexion = estadoConexion.ABIERTA;
                                        enviarPeticion(StringUtils.isNotBlank(tran_msg.trim()) ? tran_msg.trim() : str.trim());
                                        break;
                                        
                                    case '1':
                                        // INDICAR ESTADO 6 timeout2
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '1'");
                                        estados = estados.TIMEOUT2;
                                        str = terminal + estados.getCodEstado() + str.substring(5);
                                        enviarPeticion(str.trim());
                                        break;
                                        
                                    case '2':
                                        // INDICAR ESTADO 6 timeout2
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '2'");
                                        estados = estados.TIMEOUT1;
                                        str = terminal + estados.getCodEstado() + str.substring(5);
                                        enviarPeticion(str.trim());
                                        break;
                                        
                                    case '4':
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '4'");
                                    case '5':
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '5'");
                                    case '6':
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '6'");
                                    case '7':
                                        // indicar error y cortar conexion
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '7'");
                                        log.debug("ReadWriteHandler - completed() - finalizando transaccion con ERROR DE SERVIDOR. REINTENTE" );
                                        respuesta = "KO0EX" + "finalizando transaccion con ERROR DE SERVIDOR. REINTENTE";
                                        datosConexion.mainThread.interrupt();
                                        break;
                                    case '3':
                                    	log.debug("ReadWriteHandler - completed() -  case CONEXION / '3'");
                                        switch (str.charAt(5)) {
                                        	
                                            case 'K':
                                                // INDICAR ESTADO ERRORTERMINAL
                                            	log.debug("ReadWriteHandler - completed() -  case CONEXION / '3' / 'K'");
                                                estados = estados.ERRORTERMINAL;
                                                str = terminal + estados.getCodEstado() + str.substring(5);
                                                enviarPeticion(str.trim());
                                                break;
                                                
                                            case 'O':
                                                // comprobar si se había recogido previamente
                                            	log.debug("ReadWriteHandler - completed() -  case CONEXION / '3' / 'O'");
                                                File fi = new File(directorioLOG + "/" + Fi_Last_Tran);
                                                if (!fi.exists()) {
                                                    // INDICAR ESTADO RECOGIDA
                                                    estados = estados.RECOGIDA;
                                                    str = terminal + estados.getCodEstado() + str.substring(5);
                                                    enviarPeticion(str.trim());
                                                } else {
                                                    // leemos el ultimo emnsaje enviado de respuesta
                                                    // que hemos grabado machacando al enviar un ERRORTERMINAL o RECOGIDA
                                                    try (BufferedReader br = new BufferedReader(new FileReader(directorioLOG + "/" + Fi_Last_Tran))) {
                                                        String linea = br.readLine();
                                                        log.debug("ReadWriteHandler - completed() - leido    >" + linea.trim() + "<" );
                                                        log.debug("ReadWriteHandler - completed() - recibido >" + str.trim().substring(2) + "<" );
                                                        // si son iguales es la misma transaccion
                                                        // y repetimos la respuesta enviada
                                                        // si son diferentes enviamos anulacion
                                                        if (linea.trim().substring(1).equals(str.trim().substring(5))) {
                                                            if (linea.trim().charAt(0) == '5') {
                                                                estados = estados.RECOGIDA;
                                                            } else {
                                                                estados = estados.ERRORTERMINAL;
                                                            }
                                                            enviarPeticion(linea.trim());
                                                        } else {
                                                            // INDICAR ESTADO ERRORTERMINAL
                                                            estados = estados.ERRORTERMINAL;
                                                            str = terminal + estados.getCodEstado() + str.substring(5);
                                                            enviarPeticion(str.trim());
                                                        }
                                                    } catch (IOException ex) {
                                                        log.debug("ReadWriteHandler - completed() - excepcion tratando " + directorioLOG + "/" + Fi_Last_Tran + " " + ex.getMessage() );
                                                        estados = estados.ERRORTERMINAL;
                                                        str = terminal + estados.getCodEstado() + str.substring(5);
                                                        enviarPeticion(str.trim());
                                                    }
                                                }
                                                break;
                                        }
                                        break;
                                }
                                break;
                                
                            case ABIERTA:
                                switch (estados) {
                                    case PETICION:
                                    	log.debug("ReadWriteHandler - completed() -  case ABIERTA / PETICION ");
                                        // puede llegar una cursada o respuesta
                                        switch (str.charAt(4)) {
                                        	
                                            case '2':
                                                // nada solo mostrar mensaje y leer
                                            	log.debug("ReadWriteHandler - completed() -  case ABIERTA / PETICION /'2'");
                                                estados = estados.CURSADA;
                                                if (mensajesCompletosLeidos == 0) {
                                                    datosConexion.isRead = true;
                                                    datosConexion.buffer.clear();
                                                    datosConexion.ultimoMensaje = str.trim().substring(2);
                                                    datosConexion.conexion.read(datosConexion.buffer, Long.parseLong(timerCursada), TimeUnit.SECONDS, datosConexion, this);
                                                } else {
                                                    mensajesCompletosLeidos -= 1;
                                                }
                                                break;
                                                
                                            case '3':
                                                // respuesta inmediata recogerla y confirmarla
                                            	log.debug("ReadWriteHandler - completed() -  case ABIERTA / PETICION /'3'");
                                                estados = estados.RECOGIDA;
                                                str = terminal + estados.getCodEstado() + str.substring(5);
                                                enviarPeticion(str.trim());
                                                break;
                                                
                                            default:
                                            	log.debug("ReadWriteHandler - completed() -  case ABIERTA / PETICION / 'default'");
                                                log.debug("ReadWriteHandler - completed() - estado erroneo:" + str.trim().substring(4, 4) );
                                                respuesta = "KO0EX" + "estado erroneo:" + str.trim().substring(4, 4);
                                                datosConexion.mainThread.interrupt();
                                                break;
                                        }
                                        break;
                                        
                                    case CURSADA:
                                    	log.debug("ReadWriteHandler - completed() -  case CURSADA");
                                        switch (str.charAt(4)) {
                                        	
                                            case '2':
                                            	log.debug("ReadWriteHandler - completed() -  case CURSADA / '2'");
                                                // solo mostrar por pantalla o visor para indicar la evolucion y seguir leyendo
                                                if (mensajesCompletosLeidos == 0) {
                                                    datosConexion.isRead = true;
                                                    datosConexion.buffer.clear();
                                                    datosConexion.ultimoMensaje = str.trim().substring(2);
                                                    datosConexion.conexion.read(datosConexion.buffer, Long.parseLong(timerCursada), TimeUnit.SECONDS, datosConexion, this);
                                                } else {
                                                    mensajesCompletosLeidos -= 1;
                                                }
                                                break;
                                                
                                            case '3':
                                                // respuesta
                                                // hay que ver si se puede entregar al terminal y
                                                // decidir si darlo por tratada o solicitar anulacion
                                            	log.debug("ReadWriteHandler - completed() -  case CURSADA / '3'");
                                            	estados = estados.RECOGIDA;

                                                str = terminal + estados.getCodEstado() + str.substring(5);
                                                enviarPeticion(str.trim());
                                                break;
                                        }
                                        
									default:
										break;
                                }
                                break;
                        }
                    }
                } else {
                    // envío
                    log.debug("ReadWriteHandler - completed() - enviado:" + mstr.substring(0, result) + " bytes:" + result );
                    // hay que tratar según la fase de la conexión
                    switch (datosConexion.EstadoConexion) {
                    	
                        case CONEXION:
                        	log.debug("ReadWriteHandler - completed() -  case CONEXION");
                            // leer la situación actual del terminal en el server TEF
                            switch (estados) {
                            	
                                case RECOGIDA:
                                	log.debug("ReadWriteHandler - completed() -  case CONEXION / RECOGIDA");
                                    log.debug("ReadWriteHandler - completed() - FINALIZANDO TRANSACCION PREVIA RECOGIDA (5)" );

                                    respuesta = "KO0EX" + "FINALIZANDO TRANSACCION PREVIA RECOGIDA (5)";
                                    datosConexion.mainThread.interrupt();
                                    break;
                                    
                                case ERRORTERMINAL:
                                	log.debug("ReadWriteHandler - completed() -  case CONEXION / ERRORTERMINAL");
                                    log.debug("ReadWriteHandler - completed() - FINALIZANDO TRANSACCION PREVIA ERROR TERMINAL (7)" );

                                    respuesta = "KO0EX" + "FINALIZANDO TRANSACCION PREVIA ERROR TERMINAL (7)";
                                    datosConexion.mainThread.interrupt();
                                    break;
                                    
                                case TIMEOUT1:
                                	log.debug("ReadWriteHandler - completed() -  case CONEXION / T1");
                                	log.debug("ReadWriteHandler - completed() -  Timeout del Pinpad");
                                	throw new Exception("TIMEOUT");
//                                	break;
                                	
                                case TIMEOUT2:
                                	log.debug("ReadWriteHandler - completed() -  case CONEXION / T2");
                                    log.debug("ReadWriteHandler - completed() - FINALIZANDO TRANSACCION CON ERROR DE TIMEOUT EN PREVIA. REINTENTE" );
                                   
                                    respuesta = "KO0EX" + "FINALIZANDO TRANSACCION CON ERROR DE TIMEOUT EN PREVIA. REINTENTE";
                                    datosConexion.mainThread.interrupt();
                                    break;
                                    
                                case ESPERA:
                                	log.debug("ReadWriteHandler - completed() -  case CONEXION / ESPERA");
                                    // en el inicio tras envio del terminal en fase de conexión
                                    datosConexion.isRead = true;
                                    datosConexion.buffer.clear();
                                    datosConexion.conexion.read(datosConexion.buffer, 10, TimeUnit.SECONDS, datosConexion, this);
                                    break;
                                    
								default:
									break;
                            }
                            break;
                            
                        case ABIERTA:
                        	log.debug("ReadWriteHandler - completed() -  case ABIERTA");
                            switch (estados) {
                            	
                                case ESPERA:
                                	log.debug("ReadWriteHandler - completed() -  case ABIERTA / ESPERA");
                                case PETICION:
                                	log.debug("ReadWriteHandler - completed() -  case ABIERTA / PETICION");
                                    estados = estados.PETICION;
                                    datosConexion.isRead = true;
                                    datosConexion.buffer.clear();
                                    datosConexion.ultimoMensaje = mstr.trim().substring(2);
                                    datosConexion.conexion.read(datosConexion.buffer, Long.parseLong(timerPeticion), TimeUnit.SECONDS, datosConexion, this);
                                    break;
                                case ERRORTERMINAL:
                                	log.debug("ReadWriteHandler - completed() -  case ABIERTA / ERRORTERMINAL");
                                	break;
                                	
                                case RECOGIDA:
                                	log.debug("ReadWriteHandler - completed() -  case ABIERTA / RECOGIDA");
                                	if(StringUtils.isNotBlank(respuesta) && !respuesta.contains("KO")) {
                                    	respuestaOriginal = respuesta;                                    	
                                    }
//                                	if(tipoTran.equals(TefSiamManager.CANCEL_PAY) || tipoTran.equals(TefSiamManager.CANCEL_RETURN)) {
//                                		respuesta = mstr.trim().substring(2);
//                                	}
                                	if(tipoTran.equals(TefSiamManager.CANCEL)) {
                                		respuesta = mstr.trim().substring(2);
                                	}
                                	else if(mstr.trim().contains("KO")){
                                		respuesta = mstr.trim().substring(2);                                		
                                	}
                                	else {
                                		respuesta = mstr.trim().substring(3);                                		
                                	}
                                	
                                    datosConexion.mainThread.interrupt();
                                    break;
                                case CURSADA:
                                	log.debug("ReadWriteHandler - completed() -  case ABIERTA / CURSADA");
                                    break;
                                    
                                case TIMEOUT1:
                                	log.debug("ReadWriteHandler - completed() -  case ABIERTA / T1");
                                	log.debug("ReadWriteHandler - completed() -  Timeout del Pinpad");
                                	throw new Exception("TIMEOUT");
                                	
                                case TIMEOUT2:
                                	log.debug("ReadWriteHandler - completed() -  case ABIERTA / T2");
                                    respuesta = "KO0VT";
                                    datosConexion.mainThread.interrupt();
                                    break;
                                    
								default:
									break;
                            }
                            break;
                    }
                }
            }
            catch (Exception eex){
                log.debug("ReadWriteHandler - completed() - excepcion en completed: "+eex.getMessage());
                datosConexion.mainThread.interrupt();
            }
        }

        @Override
        public void failed(Throwable e, DatosConexion datosConexion) {
            log.debug("ReadWriteHandler - failed() - excepcion enviando:"+e.getMessage());
            try {
                String str;
                // ver timeouts
                if (e instanceof InterruptedByTimeoutException){
                    switch (estados) {
                        case PETICION:
                        	log.debug("ReadWriteHandler - failed() -  case PETICION");
                            estados = estados.TIMEOUT2;
                            str = terminal + estados.getCodEstado() + datosConexion.ultimoMensaje.substring(3);
                            enviarPeticion(str.trim());
                            break;
                            
                        case CURSADA:
                        	log.debug("ReadWriteHandler - failed() -  case CURSADA");
                            estados = estados.TIMEOUT1;
                            str = terminal + estados.getCodEstado() + datosConexion.ultimoMensaje.substring(3);
                            enviarPeticion(str.trim());
                            break;
                            
                        default:
                        	log.debug("ReadWriteHandler - failed() -  case DEFAULT");
                            respuesta="KO0VT";
                            datosConexion.mainThread.interrupt();
                            break;
                    }
                } else {
                    respuesta = "KO0EX" + e.getMessage();
                    datosConexion.mainThread.interrupt();
                }

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    public SiamService crearServicioTestPinpad(String mensajeTransaccion) {
		SiamService t = new SiamService(terminal, 
				TefSiamManager.TEST_TERMINAL, 
				TefSiamManager.INTEFAZ_EMV, 
				serverTEF, 
				String.valueOf(portTEF), 
				mensajeTransaccion.substring(6) , 
				String.valueOf(timerPeticion), 
				String.valueOf(timerCursada), 
				String.valueOf(timerConexion), 
				String.valueOf(timerEnvio),
				String.valueOf(TefSiamManager.LONGITUD_MSG_TCP)
				);
		
		return t;	
	}
	
	public SiamService crearServicioInicializacion(String mensajeTransaccion) {
		SiamService t = new SiamService(terminal, 
				TefSiamManager.INIT, 
				TefSiamManager.INTEFAZ_INIT, 
				serverTEF, 
				String.valueOf(portTEF), 
				mensajeTransaccion.substring(6) , 
				String.valueOf(timerPeticion), 
				String.valueOf(timerCursada), 
				String.valueOf(timerConexion), 
				String.valueOf(timerEnvio),
				String.valueOf(TefSiamManager.LONGITUD_MSG_TCP)
				);
		
		return t;	
	}

	public SiamService crearServicioVenta(String mensajeTransaccion) {
		SiamService t = new SiamService(terminal, 
				TefSiamManager.VENTA, 
				TefSiamManager.INTEFAZ_EMV, 
				serverTEF, 
				String.valueOf(portTEF), 
				mensajeTransaccion.substring(6) , 
				String.valueOf(timerPeticion), 
				String.valueOf(timerCursada), 
				String.valueOf(timerConexion), 
				String.valueOf(timerEnvio),
				String.valueOf(TefSiamManager.LONGITUD_MSG_TCP)
				);
//		tran_msg = mensajeTransaccion;
		
		return t;	
	}
	
	public SiamService crearServicioAnulacionPago(String mensajeTransaccion) {
		SiamService t = new SiamService(terminal, 
				TefSiamManager.CANCEL, 
				mensajeTransaccion.substring(5,6), 
				serverTEF, 
				String.valueOf(portTEF), 
				mensajeTransaccion.substring(6) , 
				String.valueOf(timerPeticion), 
				String.valueOf(timerCursada), 
				String.valueOf(timerConexion), 
				String.valueOf(timerEnvio),
				String.valueOf(TefSiamManager.LONGITUD_MSG_TCP)
				);
//		tran_msg = mensajeTransaccion;
		return t;	
	}
	
//	public SiamService crearServicioAnulacionPagoVenta(String mensajeTransaccion) {
//		SiamService t = new SiamService(terminal, 
//				TefSiamManager.CANCEL_PAY, 
//				mensajeTransaccion.substring(5,6), 
//				serverTEF, 
//				String.valueOf(portTEF), 
//				mensajeTransaccion.substring(6) , 
//				String.valueOf(timerPeticion), 
//				String.valueOf(timerCursada), 
//				String.valueOf(timerConexion), 
//				String.valueOf(timerEnvio),
//				String.valueOf(TefSiamManager.LONGITUD_MSG_TCP)
//				);
////		tran_msg = mensajeTransaccion;
//		return t;	
//	}

	public SiamService crearServicioDevolucion(String mensajeTransaccion) {
		SiamService t = new SiamService(terminal, 
				TefSiamManager.DEVOLUCION, 
				TefSiamManager.INTEFAZ_EMV, 
				serverTEF, 
				String.valueOf(portTEF), 
				mensajeTransaccion.substring(6) , 
				String.valueOf(timerPeticion), 
				String.valueOf(timerCursada), 
				String.valueOf(timerConexion), 
				String.valueOf(timerEnvio),
				String.valueOf(TefSiamManager.LONGITUD_MSG_TCP)
				);
//		tran_msg = mensajeTransaccion;
		return t;	
	}
	
//	public SiamService crearServicioAnulacionDevolucion(String mensajeTransaccion) {
//		SiamService t = new SiamService(terminal, 
//				TefSiamManager.CANCEL_RETURN, 
//				mensajeTransaccion.substring(5,6), 
//				serverTEF, 
//				String.valueOf(portTEF), 
//				mensajeTransaccion.substring(6) , 
//				String.valueOf(timerPeticion), 
//				String.valueOf(timerCursada), 
//				String.valueOf(timerConexion), 
//				String.valueOf(timerEnvio),
//				String.valueOf(TefSiamManager.LONGITUD_MSG_TCP)
//				);
////		tran_msg = mensajeTransaccion;
//		return t;	
//	}
	
	//Método para inicializar el pinpad de forma manual
	private boolean inicializar() throws Exception {
		try {
			log.debug("pay() - Trama de envio: " + terminal);
			String mensajeTransaccion = transaccion.crearPeticion(null, TefSiamManager.INIT, null, null, 0);
			log.debug("mensaje transaccion " + mensajeTransaccion);
			
			crearServicioInicializacion(mensajeTransaccion);
			realizarConexion();
			
			return true;
		}
		catch (Exception e) {
			log.error("inicializar() - error inicializando el TEF");
			return false;
		}

	}
	
	//Método para comprobar el estado en el que se encuentra el pinpad
	private boolean testPinpad() {
		try {
			log.debug("pay() - Trama de envio: " + terminal);
			String mensajeTransaccion = transaccion.crearPeticion(null, TefSiamManager.TEST_TERMINAL, null, null, 0);
			log.debug("mensaje transaccion " + mensajeTransaccion);

			crearServicioTestPinpad(mensajeTransaccion);
			realizarConexion();

			return esRespuestaOK(respuesta);
		}
		catch (Exception e) {
			log.error("testPinpad() - error inicializando el TEF");
			return false;
		}

	}
	
	public String modificarMensajeParaReenvioPeticion(String mensajeTransaccion) {
		String parte1 = mensajeTransaccion.substring(0, 2);
		String parte2 = mensajeTransaccion.substring(2 + 1);
		char estado = '5';

		// Concatenar las dos partes con el nuevo carácter
		String nuevaCadena = parte1 + estado + parte2;
		return nuevaCadena;
	}


	public boolean esRespuestaOK(String mensaje) {
		return mensaje.contains("OK");
	}
	
	public boolean esRespuestaKO(String mensaje) {
		return mensaje.contains("KO");
	}
	
	public boolean esRespuestaKOVT(String mensaje) {
		return mensaje.contains("KO") && mensaje.contains("VT");
	}
}
