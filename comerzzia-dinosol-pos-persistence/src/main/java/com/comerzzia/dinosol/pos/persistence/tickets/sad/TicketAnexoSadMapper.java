package com.comerzzia.dinosol.pos.persistence.tickets.sad;

import com.comerzzia.dinosol.pos.persistence.tickets.sad.TicketAnexoSadBean;
import com.comerzzia.dinosol.pos.persistence.tickets.sad.TicketAnexoSadExample;
import com.comerzzia.dinosol.pos.persistence.tickets.sad.TicketAnexoSadKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TicketAnexoSadMapper {
    int countByExample(TicketAnexoSadExample example);

    int deleteByExample(TicketAnexoSadExample example);

    int deleteByPrimaryKey(TicketAnexoSadKey key);

    int insert(TicketAnexoSadBean record);

    int insertSelective(TicketAnexoSadBean record);

    List<TicketAnexoSadBean> selectByExampleWithRowbounds(TicketAnexoSadExample example, RowBounds rowBounds);

    List<TicketAnexoSadBean> selectByExample(TicketAnexoSadExample example);

    TicketAnexoSadBean selectByPrimaryKey(TicketAnexoSadKey key);

    int updateByExampleSelective(@Param("record") TicketAnexoSadBean record, @Param("example") TicketAnexoSadExample example);

    int updateByExample(@Param("record") TicketAnexoSadBean record, @Param("example") TicketAnexoSadExample example);

    int updateByPrimaryKeySelective(TicketAnexoSadBean record);

    int updateByPrimaryKey(TicketAnexoSadBean record);
}