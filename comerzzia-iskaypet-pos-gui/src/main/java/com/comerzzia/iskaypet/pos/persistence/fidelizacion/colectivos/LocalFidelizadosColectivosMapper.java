package com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos;

import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosExample;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos.LocalFidelizadosColectivosKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LocalFidelizadosColectivosMapper {
    long countByExample(LocalFidelizadosColectivosExample example);

    int deleteByExample(LocalFidelizadosColectivosExample example);

    int deleteByPrimaryKey(LocalFidelizadosColectivosKey key);

    int insert(LocalFidelizadosColectivosKey row);

    int insertSelective(LocalFidelizadosColectivosKey row);

    List<LocalFidelizadosColectivosKey> selectByExampleWithRowbounds(LocalFidelizadosColectivosExample example, RowBounds rowBounds);

    List<LocalFidelizadosColectivosKey> selectByExample(LocalFidelizadosColectivosExample example);

    int updateByExampleSelective(@Param("row") LocalFidelizadosColectivosKey row, @Param("example") LocalFidelizadosColectivosExample example);

    int updateByExample(@Param("row") LocalFidelizadosColectivosKey row, @Param("example") LocalFidelizadosColectivosExample example);
}