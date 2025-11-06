package com.comerzzia.dinosol.pos.services.ticket.sorteos.conversion;

import org.springframework.stereotype.Component;

import com.comerzzia.api.v2.sorteos.client.model.RaffleTicketResponse;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.ParticipacionDTO;

@Component
public class RaffleTicketResponseAParticipacionDTOConversor {

	public ParticipacionDTO convertir(RaffleTicketResponse response) {

		ParticipacionDTO participacion = new ParticipacionDTO();

		participacion.setCodParticipacion(response.getRaffleTicketCode());

		// En caso de que este premiada
		if (response.getPrizeId() != null) {

			participacion.setIdPremioAsociado(response.getPrizeId());

			if (response.getTypePrizeCode() != null) {
				participacion.setCodTipoPremio(response.getTypePrizeCode());
			}

			if ("CUPON".equals(participacion.getCodTipoPremio())) {

				if (response.getCouponCode() != null) {
					participacion.setCodCupon(response.getCouponCode());
				}

				if (response.getCouponDescription() != null) {
					participacion.setDescripcionCupon(response.getCouponDescription());
				}

				if (response.getCouponEndDate() != null) {
					participacion.setFechaFinCupon(response.getCouponEndDate());
				}
			}
		} else {
			participacion.setIdPremioAsociado("");
			participacion.setCodTipoPremio("");
			participacion.setCodCupon("");
			participacion.setDescripcionCupon("");
			participacion.setFechaFinCupon(null);
		}
		return participacion;
	}

}

