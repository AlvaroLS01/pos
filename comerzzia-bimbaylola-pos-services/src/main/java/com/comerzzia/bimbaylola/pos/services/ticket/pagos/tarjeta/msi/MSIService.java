package com.comerzzia.bimbaylola.pos.services.ticket.pagos.tarjeta.msi;

import java.util.List;

import com.comerzzia.bimbaylola.pos.persistence.mediosPago.MediosPagoBIN;

public interface MSIService {

	List<MediosPagoBIN> consultar(String uidActividad, String codMedPag) throws MediosPagoBINNotFoundException, MediosPagoBINException;
}
