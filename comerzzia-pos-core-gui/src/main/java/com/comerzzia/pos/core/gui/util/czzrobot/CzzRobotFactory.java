package com.comerzzia.pos.core.gui.util.czzrobot;


import com.comerzzia.core.commons.CoreContextHolder;

import javafx.scene.Scene;

public class CzzRobotFactory {
	
	/**
     * Creates CzzRobot instance which controls given scene.
     *
     */
    public static CzzRobot createRobot(Scene scene) {
    	return CoreContextHolder.get().getBean(CzzRobot.class, scene);
    }

}
