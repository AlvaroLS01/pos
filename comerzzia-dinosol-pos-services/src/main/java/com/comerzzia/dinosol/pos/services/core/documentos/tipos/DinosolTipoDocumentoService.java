package com.comerzzia.dinosol.pos.services.core.documentos.tipos;

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

@Primary
@Service
public class DinosolTipoDocumentoService extends TipoDocumentoService{
	
	public TipoDocumentoBean consultar(String uidActividad, String codPais, String codTipoDoc) throws DocumentoException, DocumentoNotFoundException {
        SqlSession sqlSession =  new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());

            TipoDocumentoExample filtro = new TipoDocumentoExample();
            filtro.or().andUidActividadEqualTo(uidActividad).andCodpaisEqualTo(codPais).andCodtipodocumentoEqualTo(codTipoDoc);
            filtro.setOrderByClause(TipoDocumentoExample.ORDER_BY_CODTIPODOCUMENTO);
            
            List<TipoDocumentoBean> tiposDocumentos = tipoDocumentoMapper.selectByExample(filtro);
            
            if(tiposDocumentos != null && !tiposDocumentos.isEmpty()){
                return tiposDocumentos.get(0);
            }
            else{
                throw new DocumentoNotFoundException();
            }            
        }
        catch(DocumentoNotFoundException e){
            log.error("consultarTiposDocumentos() - No se ha encontrado ningún tipo de documento para la actividad configurada");
            throw new DocumentoNotFoundException();
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando documentos para instancia y pais. " + e.getMessage();
            log.error("consultarTiposDocumentos()() - " + msg, e);
            throw new DocumentoException(e);
        }
        finally{
            sqlSession.close();
        }
    }

}
