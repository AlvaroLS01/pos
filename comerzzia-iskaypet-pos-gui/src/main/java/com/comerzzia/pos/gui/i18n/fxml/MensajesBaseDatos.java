package com.comerzzia.pos.gui.i18n.fxml;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.comerzzia.core.util.db.Database;
import com.comerzzia.pos.persistence.core.menu.MenuBean;
import com.comerzzia.pos.persistence.core.menu.MenuExample;
import com.comerzzia.pos.persistence.core.menu.POSMenuMapper;

public class MensajesBaseDatos {
	
	public static List<String> obtenerCadenas() throws MensajesException {
		List<String> res = new LinkedList<>();
				
        MenuExample exampleMenu = new MenuExample();
        exampleMenu.or().andAplicacionEqualTo("JPOS").andActivoEqualTo(true);
        exampleMenu.setOrderByClause("ORDEN ASC");
        
        POSMenuMapper menuMapper = Database.getSqlSession().getMapper(POSMenuMapper.class);
        
        List<MenuBean> menusBean = menuMapper.selectByExample(exampleMenu);
		
        for (MenuBean menu : menusBean) {
        	String opcion = menu.getOpcion();
            StringTokenizer token = new StringTokenizer (opcion,"\\");                     
            String key = token.nextToken();
        	res.add(key);
			res.add(menu.getAccion().getTitulo());
		}
        
        return res;
	}
}
