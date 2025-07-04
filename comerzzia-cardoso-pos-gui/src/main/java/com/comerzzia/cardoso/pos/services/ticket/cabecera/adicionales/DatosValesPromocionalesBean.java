package com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.comerzzia.pos.util.format.FormatUtil;

/**
 * GAP - PERSONALIZACIONES V3 - VALE PROMOCIONAL
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosValesPromocionalesBean{

	private static final Logger log = Logger.getLogger(DatosValesPromocionalesBean.class.getName());

	@XmlElement(name = "fecha_inicio")
	protected Date fechaInicio;
	@XmlElement(name = "fecha_fin")
	protected Date fechaFin;
	@XmlElement(name = "euros_compra")
	protected BigDecimal eurosCompra;
	@XmlElement(name = "euros_venta")
	protected BigDecimal eurosVenta;
	@XmlElement(name = "fecha_inicio_redencion")
	protected Date fechaInicioRedencion;
	@XmlElement(name = "fecha_fin_redencion")
	protected Date fechaFinRedencion;
	@XmlElement
	protected String texto;
	@XmlElement(name = "valor_vale")
	protected BigDecimal valorVale;

	@XmlTransient
	protected List<String> listaArticulos;
	
	/**
	 * En el propio contructor del objeto, cargamos los datos a patir del archivo.
	 */
	public DatosValesPromocionalesBean(){
		log.debug("DatosValesPromocionalesBean() : GAP - PERSONALIZACIONES V3 - VALE PROMOCIONAL");
		
		URL url = null;
		File file;
		String cmzHomePath = System.getenv("COMERZZIA_HOME"), valesprop = "valesPromocionales.properties";
		cmzHomePath = cmzHomePath == null ? "" : cmzHomePath;
		try{
			if(!cmzHomePath.endsWith(File.separator)){
				cmzHomePath += File.separator;
			}
			file = new File(cmzHomePath + valesprop);
			if(file.exists()){
				url = file.toURI().toURL();
			}
		}
		catch(MalformedURLException e){
			url = null;
		}
		if(url == null){
			url = Thread.currentThread().getContextClassLoader().getResource(valesprop);
		}

		try{
			log.info("DatosValesPromocionalesBean() - Cargar vales promocionales: Cargando fichero de configuracion [" + url.toString() + "]");
			file = new File(url.toURI());
		}
		catch(Exception e){
			log.debug("DatosValesPromocionalesBean() - Cargar vales promocionales: Error al cargar el fichero de configuracion no encontrado:" + e.getMessage());
			return;
		}

		if(!file.exists()){
			log.debug("DatosValesPromocionalesBean() - Cargar vales promocionales: Fichero de configuracion no encontrado");
			return;
		}

		/* Cargamos los articulos a los que les aplicamos el vale promocional */
		if(!leerArticulosValePromocional(cmzHomePath)){
			return;
		}

		Properties datosValePromocinal = new Properties();
		FileInputStream fichero;
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		try{
			fichero = new FileInputStream(file);
			try{
				datosValePromocinal.load(fichero);
				fechaInicio = (Date) formatoFecha.parse(datosValePromocinal.getProperty("FechaInicio"));
				fechaFin = (Date) formatoFecha.parse(datosValePromocinal.getProperty("FechaFin"));
				fechaInicioRedencion = (Date) formatoFecha.parse(datosValePromocinal.getProperty("FechaInicioRedencion"));
				fechaFinRedencion = (Date) formatoFecha.parse(datosValePromocinal.getProperty("FechaFinRedencion"));
				texto = datosValePromocinal.getProperty("Texto");
				eurosCompra = FormatUtil.getInstance().desformateaBigDecimal(datosValePromocinal.getProperty("EurosCompra"), 2);
				eurosVenta = FormatUtil.getInstance().desformateaBigDecimal(datosValePromocinal.getProperty("EurosVenta"), 2);
			}
			finally{
				try{
					if(fichero != null){
						fichero.close();
					}
				}
				catch(Exception e2){
				}
			}
		}
		catch(Exception ex){
			fechaInicio = null;
			fechaFin = null;
			fechaInicioRedencion = null;
			fechaFinRedencion = null;
			texto = null;
			eurosCompra = null;
			eurosVenta = null;
			valorVale = null;
		}
	}

	public Boolean leerArticulosValePromocional(String cmzHomePath){
		listaArticulos = new ArrayList<String>();
		FileReader fr = null;
		BufferedReader br = null;

		URL url = null;
		File file;
		String valesartprop = "valePromocionalesArticulos.txt";
		cmzHomePath = cmzHomePath == null ? "" : cmzHomePath;

		try{
			if(!cmzHomePath.endsWith(File.separator)){
				cmzHomePath += File.separator;
			}
			file = new File(cmzHomePath + valesartprop);
			if(file.exists()){
				url = file.toURI().toURL();
			}
		}
		catch(MalformedURLException e){
			url = null;
		}
		if(url == null){
			url = Thread.currentThread().getContextClassLoader().getResource(valesartprop);
		}

		try{
			log.info("leerArticulosValePromocional() - Cargar articulos vales promocionales: Cargando fichero de configuracion [" + url.toString() + "]");
			file = new File(url.toURI());
		}
		catch(Exception e){
			log.debug("leerArticulosValePromocional() - Cargar articulos vales promocionales: Error al cargar el fichero de configuracion no encontrado:" + e.getMessage());
			return false;
		}

		if(!file.exists()){
			log.debug("leerArticulosValePromocional() - Cargar articulos vales promocionales: Fichero de configuracion no encontrado");
			return false;
		}

		try{
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String linea;
			while((linea = br.readLine()) != null)
				listaArticulos.add(linea);
		}
		catch(Exception e){
			listaArticulos.clear();
		}
		finally{
			/* En el finally cerramos el fichero, para asegurarnos que se 
			 * cierra tanto si todo va bien como si salta una excepcion. */
			try{
				if(fr != null){
					fr.close();
				}
			}
			catch(Exception e2){
			}
		}

		return true;
	}
	
	public Date getFechaInicio(){
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio){
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin(){
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin){
		this.fechaFin = fechaFin;
	}

	public BigDecimal getEurosCompra(){
		return eurosCompra;
	}

	public void setEurosCompra(BigDecimal eurosCompra){
		this.eurosCompra = eurosCompra;
	}

	public BigDecimal getEurosVenta(){
		return eurosVenta;
	}

	public void setEurosVenta(BigDecimal eurosVenta){
		this.eurosVenta = eurosVenta;
	}

	public Date getFechaInicioRedencion(){
		return fechaInicioRedencion;
	}

	public void setFechaInicioRedencion(Date fechaInicioRedencion){
		this.fechaInicioRedencion = fechaInicioRedencion;
	}

	public Date getFechaFinRedencion(){
		return fechaFinRedencion;
	}

	public void setFechaFinRedencion(Date fechaFinRedencion){
		this.fechaFinRedencion = fechaFinRedencion;
	}

	public String getTexto(){
		return texto;
	}

	public void setTexto(String texto){
		this.texto = texto;
	}

	public BigDecimal getValorVale(){
		return valorVale;
	}

	public void setValorVale(BigDecimal valorVale){
		this.valorVale = valorVale;
	}

	public String getValorValeAsString(){
		return FormatUtil.getInstance().formateaImporte(valorVale);
	}

	public String getFechaInicioRedencionAsLocale(){
		return FormatUtil.getInstance().formateaFechaCorta(fechaInicioRedencion);
	}

	public String getFechaFinRedencionAsLocale(){
		return FormatUtil.getInstance().formateaFechaCorta(fechaFinRedencion);
	}

	public int getNumVales(){
		return valorVale.divide(eurosVenta, 0, RoundingMode.DOWN).intValue();
	}

	public String getFechaInicioRedencionAsString(){
		return FormatUtil.getInstance().formateaFechaCorta(fechaInicioRedencion);
	}

	public String getFechaFinRedencionAsString(){
		return FormatUtil.getInstance().formateaFechaCorta(fechaFinRedencion);
	}

	public List<String> getListaArticulos(){
		return listaArticulos;
	}

	public void setListaArticulos(List<String> listaArticulos){
		this.listaArticulos = listaArticulos;
	}
	
}
