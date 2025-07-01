package com.comerzzia.pos.util.config;

import com.comerzzia.pos.util.xml.XMLDocumentNode;

import lombok.extern.log4j.Log4j;

@Log4j
public class KeyCodesInfo {
	protected String tableKeyCodeAction;
	protected String tableKeyCodeNegate;
	protected String tableKeyCodeDelete;
	protected String tableKeyCodeGeneralAction;
	protected String tableKeyCodeGeneralNegate;
	protected String tableKeyCodeGemeralDelete;
	protected String tableKeyCodeGeneralFirst;
	protected String tableKeyCodeGeneralNext;
	protected String tableKeyCodeGeneralPrevious;
	protected String tableKeyCodeGeneralLast;
	
	/**Parsea el arbol de <KeyCodes>
	 * */
	public static KeyCodesInfo parse(XMLDocumentNode nodeRoot) {
		KeyCodesInfo keyCodesInfo = new KeyCodesInfo();
    	
//    	try {
//			XMLDocumentNode nodeKeyCodes = nodeRoot.getNodo("KeyCodes", true);
//			if (nodeKeyCodes != null) {
//				XMLDocumentNode nodeKeyCodesTabla = nodeKeyCodes.getNodo("Tablas", true);
//				if (nodeKeyCodesTabla != null) {
//					keyCodesInfo.keyCodeTablaAccion = getOptValue(nodeKeyCodesTabla, "Accion");
//					keyCodesInfo.keyCodeTablaNegar = getOptValue(nodeKeyCodesTabla, "Negar");             
//					keyCodesInfo.keyCodeTablaEliminar = getOptValue(nodeKeyCodesTabla, "Eliminar");          
//					keyCodesInfo.keyCodeTablaAccionGeneral = getOptValue(nodeKeyCodesTabla, "AccionGeneral");     
//					keyCodesInfo.keyCodeTablaNegarGeneral = getOptValue(nodeKeyCodesTabla, "NegarGeneral");      
//					keyCodesInfo.keyCodeTablaEliminarGeneral = getOptValue(nodeKeyCodesTabla, "EliminarGeneral");   
//					keyCodesInfo.keyCodeTablaPrimeroGeneral = getOptValue(nodeKeyCodesTabla, "PrimeroGeneral");    
//					keyCodesInfo.keyCodeTablaSiguienteGeneral = getOptValue(nodeKeyCodesTabla, "SiguienteGeneral");  
//					keyCodesInfo.keyCodeTablaAnteriorGeneral = getOptValue(nodeKeyCodesTabla, "AnteriorGeneral");   
//					keyCodesInfo.keyCodeTablaUltimoGeneral = getOptValue(nodeKeyCodesTabla, "UltimoGeneral");     
//				}
//			}
//		} catch (Exception e) {
//			log.error("parse() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
//		}
		return keyCodesInfo;
	}

	public String getTableKeyCodeAction() {
		return tableKeyCodeAction;
	}

	public String getTableKeyCodeNegate() {
		return tableKeyCodeNegate;
	}

	public String getTableKeyCodeDelete() {
		return tableKeyCodeDelete;
	}

	public String getTableKeyCodeGeneralAction() {
		return tableKeyCodeGeneralAction;
	}

	public String getTableKeyCodeGeneralNegate() {
		return tableKeyCodeGeneralNegate;
	}

	public String getTableKeyCodeGemeralDelete() {
		return tableKeyCodeGemeralDelete;
	}

	public String getTableKeyCodeGeneralFirst() {
		return tableKeyCodeGeneralFirst;
	}

	public String getTableKeyCodeGeneralNext() {
		return tableKeyCodeGeneralNext;
	}

	public String getTableKeyCodeGeneralPrevious() {
		return tableKeyCodeGeneralPrevious;
	}

	public String getTableKeyCodeGeneralLast() {
		return tableKeyCodeGeneralLast;
	}
	
}
