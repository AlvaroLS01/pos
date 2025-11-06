package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos;

import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.comerzzia.dinosol.librerias.cryptoutils.CryptoUtils;
import com.comerzzia.dinosol.pos.services.documents.LocatorManagerImpl;

public class GenerarQrFidelizado {

	public static void main(String[] args) {
//		String qr = "CUSSQRnuB2-0000000000";
		String qr = "CUSBg5VBld-0000000000";
//		String qr = "CUSFFKxiBw-0000000000";
//		String qr = "CUSxUZkt8w-0000000000";
//		String qr = "CUSBg5VBld-0000000000";
//		String qr = "CUSBg5V896-0000000000";
//		String qr = "CUS5QLBkeW-0000000000";
//		String qr = "CUSCPlWk7m-0000000000";
//		String qr = "CUSiTbitkV-0000000000";
//		String qr = "CUSmgEbdCT-0000000000";
//		String qr = "CUSQRnuB2-0000000000";
//		String qr = "CUSBg5VBld-0000000000";
//		String qr = "CUSiTbitkV-0000000000-R4HD-1605605400-9999";
		
//		Scanner in = new Scanner(System.in); 
//  
//		System.out.println("Introduzca el número de tarjeta: ");
//		String numeroTarjeta = in.next();
//		
//		System.out.println("Introduzca el timestamp de la tarjeta: ");
//		String timeoutTarjeta = in.next();
//		
//		String qr = numeroTarjeta + "-" + timeoutTarjeta;
//		
//		System.out.println("Introduzca el segmento adicional (este campo es opcional, sino desea introducirlo escriba N): ");
//		String segmento = in.next();
//		
//		if(StringUtils.isNotBlank(segmento) && !segmento.equals("N")) {
//			System.out.println("Introduzca la vigencia del segmento: ");
//			String vigenciaSegmento = in.next();
//			
//			System.out.println("Introduzca el centro del segmento: ");
//			String centroSegmento = in.next();
//			
//			qr = qr + "-" + segmento + "-" + vigenciaSegmento + "-" + centroSegmento;
//		}
		
		System.out.println("Generando QR encriptado a partir de la cadena: " + qr);
		String qrEncrypt = CryptoUtils.encrypt(qr, "dinosol");
		System.out.println(qrEncrypt);
		
//		in.close();
	}

}
