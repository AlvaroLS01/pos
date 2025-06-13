package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import java.util.ArrayList;
import java.util.List;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Legalfac {

    private static final String LABEL_LEGALFAC = "LEGALFAC";
    private static final int MAX_LINE_LENGTH = 233;

    @Override
	public String toString() {
    	//En cada add tiene que ir un parrafo completo
		List<String> lista = new ArrayList<String>();
		lista.add("Gracias por su visita. Conserve este documento para cualquier cambio o devolución. Plazo máximo 30 días naturales desde la fecha de compra. Condiciones válidas para el territorio nacional excepto grandes almacenes, tiendas Outlet y aeropuertos. Para el cambio o devolución de artículos es imprescindible que estos no hayan sido usados o deteriorados, y se conserve intacta la etiqueta original. No se admitirán cambios ni devoluciones de artículos de baño sin la protección higiénica. Pendientes o accesorios sin el embalaje original, prendas modificadas a petición del cliente, ni de artículos comprados en otro país. El importe de su compra será reembolsado en la misma forma de pago, a excepción de pagos realizados en tarjeta, que se reembolsarán en efectivo, siempre y cuando presente el ticket original. En el caso de ticket regalo, el reembolso se efectuará en una tarjeta abono. Esta garantía es adicional y no afecta a los derechos legales del consumidor. Para cualquier cambio/devolución de artículos pagados con la Tarjeta, es imprescindible su conservación y presentación en el momento de dicha operación. El término de garantía legal es de 6 meses a partir de la fecha de compra para todos los artículos de la tienda.");
		lista.add("EXCEPCIONES – TIENDAS OUTLET");
		lista.add("Los artículos comprados en tiendas outlet sólo se podrán devolver en tiendas outlet. La devolución del importe de compra se hará únicamente en tarjeta abono. Condiciones válidas para tiendas OUTLET del territorio nacional. No se admitirán cambios ni devoluciones de artículos de fiesta ni bisutería.");
		lista.add("***Tarjeta Abono/Regalo***");
		lista.add("La Tarjeta es válida por un periodo de 1 año desde su recarga. La Tarjeta únicamente es válida para la compra de artículos a la venta en los puntos de venta de BIMBA Y LOLA presentes en el territorio nacional (excepto grandes almacenes, aeropuertos y tienda online www.bimbaylola.com) hasta que se consuma el saldo de la Tarjeta. Dicho saldo no será reembolsable ni será canjeable por dinero en ningún caso. No se reemplazará la Tarjeta en caso de robo, pérdida, extravío o deterioro. Los productos adquiridos con la Tarjeta están sujetos a la política de cambios y devoluciones de BIMBA Y LOLA. Asimismo, se aceptará la devolución de la Tarjeta Regalo que no haya sido utilizada en un plazo máximo de 30 días, siempre y cuando tenga el protector del código de seguridad sin manipular, respetando las condiciones de devolución establecidas en bimbaylola.com. Para cualquier cambio o devolución de artículos pagados con la Tarjeta, es imprescindible su conservación y presentación en el momento de dicha operación. Por favor, compruebe que el saldo de la Tarjeta es el que figura en el ticket vinculado a la misma. La adquisición y uso de la Tarjeta implica la aceptación de las condiciones para el uso de la misma disponibles en www.bimbaylola.com. Esta garantía es adicional y no afecta a los derechos del consumidor.");
		StringBuilder cadenaADevolver = new StringBuilder();

        for (String info : lista) {
        	
            List<String> partes = EdicomFormat.dividirEnPartes(info, MAX_LINE_LENGTH);

            for (String parte : partes) {
                cadenaADevolver.append(LABEL_LEGALFAC)
                               .append("|")
                               .append(EdicomFormat.rellenaEspaciosAlfanumericos(parte, MAX_LINE_LENGTH))
                               .append("|")
                               .append("\n");
            }
        }
        
        return cadenaADevolver.toString();
    }
}