package com.comerzzia.bimbaylola.pos.services.core.variables;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.core.variables.VariablesServices;

@Component
@Primary
public class ByLVariablesServices extends VariablesServices {

	/* Stocks */
	public static final String SUBCRIPTION_KEY = "STOCK.SUBCRIPTION_KEY";
	public static final String API_VERSION = "STOCK.API_VERSION";
	public static final String URL_SOLICITAR_TOKEN = "STOCK.URL_TOKEN";
	public static final String URL_SOLICITAR_STOCK = "STOCK.URL_STOCK";

	/* GiftCard */
	public static final String GIFTCARD_SUBCRIPTION_KEY = "GIFTCARD.SUBCRIPTION_KEY";
	public static final String GIFTCARD_API_VERSION = "GIFTCARD.API_VERSION";
	public static final String GIFTCARD_URL_SOLICITAR_TOKEN = "GIFTCARD.URL_SOLICITAR_TOKEN";
	public static final String URL_SOLICITAR_ESTADO = "GIFTCARD.URL_SOLICITAR_ESTADO";
	public static final String URL_SOLICITAR_MOVIMIENTO = "GIFTCARD.URL_SOLICITAR_MOVIMIENTO";
	public static final String URL_INSERTAR_MOVIMIENTO = "GIFTCARD.URL_INSERTAR_MOVIMIENTO";
	public static final String URL_MODIFICAR_MOVIMIENTO = "GIFTCARD.URL_MODIFICAR_MOVIMIENTO";

	/* Axis */
	public static final String PATH_AXIS_CONFIGURACION = "AXIS.PATH_PROPERTIES";

	/* Puerto Rico fiscal */
	public static final String REST_URL_FISCAL_PUERTO_RICO = "PR.REST_URL_IDFISCAL";
	public static final String MERCHANT_ID_PUERTO_RICO = "PR.MERCHANT_ID";
	public static final String MUNICIPAL_TAX_PUERTO_RICO = "PR.MUNICIPAL_TAX_PORCENTAJE";
	public static final String STATE_TAX_PUERTO_RICO = "PR.STATE_TAX_PORCENTAJE";
	public static final String REDUCED_STATE_TAX_PUERTO_RICO = "PR.REDUCED_STATE_TAX_PORCENTAJE";

	/* Impuestos Articulos */

	public static final String URL_IMPUESTOS_SOLOCITAR_TOKEN = "VERTEX.URL_SOLICITAR_TOKEN";
	public static final String URL_SOLICITAR_IMPUESTO = "VERTEX.URL_SOLICITAR_IMPUESTO";

	/* US - Vertex online y offline */

	public static final String VERTEX_EMPRESA = "VERTEX.EMPRESA";
	public static final String US_TAX_AREA_ID = "US.TAX_AREA_ID";

	/* CO - Integracion con EDICOM */
	public static final String CO_EDICOM_USER = "CO.EDICOM_USER";
	public static final String CO_EDICOM_PASSWORD = "CO.EDICOM_PASSWORD";
	public static final String CO_EDICOM_DOMAIN = "CO.EDICOM_DOMAIN";
	public static final String CO_EDICOM_GROUP = "CO.EDICOM_GROUP";
	public static final String CO_EDICOM_PUBLISHTYPE = "CO.EDICOM_PUBLISHTYPE";
	public static final String CO_EDICOM_PROCESS = "CO.EDICOM_PROCESS";
	public static final String CO_EDICOM_RETURNDATATYPE = "CO.EDICOM_RETURNDATATYPE";
	public static final String CO_EDICOM_RETURNDATAPROCESS = "CO.EDICOM_RETURNDATAPROCESS";
	public static final String CO_EDICOM_RESTURL = "CO.EDICOM_RESTURL";
	public static final String FICHERO_FISCAL_CODIFICADO = "FICHERO_FISCAL_CODIFICADO";

	public static final String FICHERO_FISCAL_PROCESADO = "FICHERO_FISCAL_PROCESADO";

	public static final String ID_FISCAL_CO = "ID_FISCAL_CO";
	
	public static final String CO_EDICOM_CORREOELECTRONICO = "CO.EDICOM_CORREOELECTRONICO";
	public static final String CO_EDICOM_AMBIENTE = "CO.EDICOM_AMBIENTE";
	
	/* ECUADOR - Integración con WEBPOS */
	public static final String ID_FISCAL_EC = "ID_FISCAL_EC";
	public static final String EC_WEBPOS_ENTORNO = "EC.WEBPOS_ENTORNO";
	
	/* PANAMÁ - Integración con WEBPOS */
	public static final String ID_FISCAL_PA = "ID_FISCAL_PA";
	public static final String PA_WEBPOS_URL = "PA.WEBPOS_URL";
	public static final String PA_WEBPOS_ENTORNO = "PA.WEBPOS_ENTORNO";
	public static final String PA_WEBPOS_APIKEY = "PA.WEBPOS_APIKEY";
	public static final String PA_WEBPOS_COMPANYLICCOD = "PA.WEBPOS_COMPANYLICCOD";
	public static final String PA_FICHERO_FISCAL_CODIFICADO = "PA.PDF_FISCAL_CODIFICADO";
	public static final String PA_WEBPOS_RUTA_CARPETA = "PA.WEBPOS_RUTA_CARPETA";
	public static final String PA_WEBPOS_TIEMPO_DE_ESPERA_EN_SEGUNDOS = "PA.WEBPOS_TIEMPO_DE_ESPERA_EN_SEGUNDOS";

}
