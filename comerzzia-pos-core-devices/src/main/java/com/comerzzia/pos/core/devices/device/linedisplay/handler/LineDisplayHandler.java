
package com.comerzzia.pos.core.devices.device.linedisplay.handler;

import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplay;

public class LineDisplayHandler {

    private static LineDisplayHandler handler;

    private LineDisplayHandler() {
    }

    public static LineDisplayHandler getVisorHandler() {
        if (handler == null) {
            handler = new LineDisplayHandler();
        }
        return handler;
    }

    public void showScreen(LineDisplayScreen screen, DeviceLineDisplay visor) {
        visor.clear();
        screen.setColNum(visor.getNumColumns());
        if (screen.existLineUp() && screen.existLineDown()) {
            visor.writeLineUp(screen.getTopLeftLine());
            visor.writeRightUp(screen.getTopRightLine());
            visor.writeLineDown(screen.getDownLeftLine());
            visor.writeRightDown(screen.getDownRightLine());
        }
        else if (screen.existLineUp()) {
            visor.writeLineUp(screen.getTopLeftLine());
            visor.writeRightUp(screen.getTopRightLine());
        }
        else if (screen.existLineDown()) {
            visor.writeLineDown(screen.getDownLeftLine());
            visor.writeRightDown(screen.getDownRightLine());
        }
        else {
            //Se ha llamado a showScreen sin mensajes
        }
    }

}
