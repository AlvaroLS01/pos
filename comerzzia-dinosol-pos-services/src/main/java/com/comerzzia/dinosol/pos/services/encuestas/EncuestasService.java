package com.comerzzia.dinosol.pos.services.encuestas;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.persistence.encuestas.Encuesta;
import com.comerzzia.dinosol.pos.persistence.encuestas.EncuestaExample;
import com.comerzzia.dinosol.pos.persistence.encuestas.EncuestaMapper;
import com.comerzzia.dinosol.pos.persistence.encuestas.preguntas.PreguntaEncuesta;
import com.comerzzia.dinosol.pos.persistence.encuestas.preguntas.PreguntaEncuestaExample;
import com.comerzzia.dinosol.pos.persistence.encuestas.preguntas.PreguntaEncuestaMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Component
public class EncuestasService {
	
	@Autowired
	private EncuestaMapper encuestaMapper;
	
	@Autowired
	private PreguntaEncuestaMapper preguntaEncuestaMapper;
	
	@Autowired
	private Sesion sesion;

	public List<Encuesta> getEncuestasActivas() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date hoy = calendar.getTime();
		
		EncuestaExample example = new EncuestaExample();
		example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andActivoEqualTo("1").andFechaInicioLessThanOrEqualTo(hoy).andFechaFinGreaterThanOrEqualTo(hoy);
		
		List<Encuesta> encuestas = encuestaMapper.selectByExample(example);
		
		Iterator<Encuesta> it = encuestas.iterator();
		while (it.hasNext()) {
			Encuesta encuesta = it.next();
			
			PreguntaEncuestaExample preguntaExample = new PreguntaEncuestaExample();
			preguntaExample.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andIdEncuestaEqualTo(encuesta.getIdEncuesta());
			
			List<PreguntaEncuesta> preguntas = preguntaEncuestaMapper.selectByExample(preguntaExample);
			
			if(preguntas.isEmpty()) {
				it.remove();
			}
			else {				
				encuesta.setPreguntas(preguntas);
			}
		}
		
		return encuestas;
	}

}
