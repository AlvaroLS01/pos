package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident;

import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdent;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdentExample;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident.ItemsPetsIdentKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ItemsPetsIdentMapper {
    long countByExample(ItemsPetsIdentExample example);

    int deleteByExample(ItemsPetsIdentExample example);

    int deleteByPrimaryKey(ItemsPetsIdentKey key);

    int insert(ItemsPetsIdent record);

    int insertSelective(ItemsPetsIdent record);

    List<ItemsPetsIdent> selectByExample(ItemsPetsIdentExample example);

    ItemsPetsIdent selectByPrimaryKey(ItemsPetsIdentKey key);

    int updateByExampleSelective(@Param("record") ItemsPetsIdent record, @Param("example") ItemsPetsIdentExample example);

    int updateByExample(@Param("record") ItemsPetsIdent record, @Param("example") ItemsPetsIdentExample example);

    int updateByPrimaryKeySelective(ItemsPetsIdent record);

    int updateByPrimaryKey(ItemsPetsIdent record);
}