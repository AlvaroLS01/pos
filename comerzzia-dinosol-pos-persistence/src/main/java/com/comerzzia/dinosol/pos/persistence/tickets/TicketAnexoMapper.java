package com.comerzzia.dinosol.pos.persistence.tickets;

import com.comerzzia.dinosol.pos.persistence.tickets.TicketAnexoBean;
import com.comerzzia.dinosol.pos.persistence.tickets.TicketAnexoExample;
import com.comerzzia.dinosol.pos.persistence.tickets.TicketAnexoKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TicketAnexoMapper {
    int countByExample(TicketAnexoExample example);

    int deleteByExample(TicketAnexoExample example);

    int deleteByPrimaryKey(TicketAnexoKey key);

    int insert(TicketAnexoBean record);

    int insertSelective(TicketAnexoBean record);

    List<TicketAnexoBean> selectByExampleWithRowbounds(TicketAnexoExample example, RowBounds rowBounds);

    List<TicketAnexoBean> selectByExample(TicketAnexoExample example);

    TicketAnexoBean selectByPrimaryKey(TicketAnexoKey key);

    int updateByExampleSelective(@Param("record") TicketAnexoBean record, @Param("example") TicketAnexoExample example);

    int updateByExample(@Param("record") TicketAnexoBean record, @Param("example") TicketAnexoExample example);

    int updateByPrimaryKeySelective(TicketAnexoBean record);

    int updateByPrimaryKey(TicketAnexoBean record);
}