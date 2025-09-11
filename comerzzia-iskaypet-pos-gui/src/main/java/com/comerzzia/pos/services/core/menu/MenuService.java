/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */

package com.comerzzia.pos.services.core.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.menu.MenuBean;
import com.comerzzia.pos.persistence.core.menu.MenuExample;
import com.comerzzia.pos.persistence.core.menu.POSMenuMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class MenuService {
    
    protected static Logger log = Logger.getLogger(MenuService.class);

    @Autowired
    protected POSMenuMapper menuMapper;
    
    /**
     * Devuelve el menu con el nombre de la aplicacion indicado que se encuentre en la BBDD en la vista de CONFIG_MENU . El menu debe estar activo
     *
     * @param aplicacion
     * @return :: Lista de menus 
     * @throws MenuNotFoundException :: Lanzada si no existe el menu con
     * el código indicado. El menu debe estar activo
     * @throws MenuServiceException
     */
    public Map<String,List<MenuBean>> consultarMenu(String uidMenu, String aplicacion) throws MenuNotFoundException, MenuServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());
            MenuExample exampleMenu = new MenuExample();
            exampleMenu.setOrderByClause("ORDEN ASC");
            exampleMenu.or().andUidMenuEqualTo(uidMenu).andAplicacionEqualTo(aplicacion).andActivoEqualTo(Boolean.TRUE);
            log.debug("consultarMenu() - Consultando los menus para aplicacion: "+ aplicacion);
            List<MenuBean> menusBean = menuMapper.selectByExample(exampleMenu);
            
            if(!menusBean.isEmpty()){
               
                Map<String,List<MenuBean>> mapMenus = new LinkedHashMap<>();
                for(MenuBean menu: menusBean){
                    String opcion = menu.getOpcion();
                    StringTokenizer token = new StringTokenizer (opcion,"\\");                     
                    String key = token.nextToken();
                    String valor = token.nextToken();
                    List<MenuBean> list = mapMenus.get(key);
                    if(list == null){
                        list = new ArrayList<>();
                        mapMenus.put(key,list);
                    }
                    menu.setOpcion(valor);
                    list.add(menu);
                }
                return mapMenus;
            }
            else{
                throw new MenuNotFoundException();
            }
        }
        catch(MenuNotFoundException e){
            log.error("consultarMenu() - Menu no encontrado para la aplicacion :"+ aplicacion);
            throw new MenuNotFoundException();
        }
        catch(Exception e){
            String mgs = "Error al consultar Menu para la aplicacion :" + aplicacion +", "+ e;
            log.error("consultarMenu() - "+ mgs, e);
            throw new MenuServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }
}
