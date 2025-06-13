package com.comerzzia.bimbaylola.pos.services.core.documentos.tipos;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoExample;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.DocumentoNotFoundException;
import com.comerzzia.pos.services.core.documentos.tipos.TipoDocumentoService;
import com.comerzzia.pos.util.i18n.I18N;

@Primary
@Service
public class ByLTipoDocumentoService extends TipoDocumentoService{
	
	public TipoDocumentoBean consultar(String uidActividad, String codPais, String codTipoDoc) 
			throws DocumentoException, DocumentoNotFoundException{
        SqlSession sqlSession =  new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());

            log.debug("consultar() - Iniciando consulta para el tipo de documento : ");
            log.debug("consultar() - UidActividad : " + uidActividad);
            log.debug("consultar() - Codigo : " + codTipoDoc);
            log.debug("consultar() - Pais : " + codPais);
            
            TipoDocumentoExample filtro = new TipoDocumentoExample();
            filtro.or().andUidActividadEqualTo(uidActividad).andCodpaisEqualTo(codPais).andCodtipodocumentoEqualTo(codTipoDoc);
            filtro.setOrderByClause(TipoDocumentoExample.ORDER_BY_CODTIPODOCUMENTO);
            
            List<TipoDocumentoBean> tiposDocumentos = tipoDocumentoMapper.selectByExample(filtro);
            
            if(tiposDocumentos != null && !tiposDocumentos.isEmpty()){
                return tiposDocumentos.get(0);
            }else{
                throw new DocumentoNotFoundException();
            }            
        }catch(DocumentoNotFoundException e){
        	String mensajeError = "No se ha encontrado ningún tipo de documento para la actividad configurada";
            log.error("consultarTiposDocumentos() - " + mensajeError + " - " + e.getMessage());
            throw new DocumentoNotFoundException(I18N.getTexto(mensajeError), e);
        }catch(Exception e){
            String mensajeError = "Se ha producido un error consultando documentos para instancia y pais";
            log.error("consultarTiposDocumentos()() - " + mensajeError + " - " + e.getMessage());
            throw new DocumentoException(I18N.getTexto(mensajeError), e);
        }finally{
            sqlSession.close();
        }
    }

}
