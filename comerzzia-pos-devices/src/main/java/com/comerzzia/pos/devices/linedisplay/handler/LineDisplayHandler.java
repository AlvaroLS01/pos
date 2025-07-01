
package com.comerzzia.pos.devices.linedisplay.handler;

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
        screen.setNumColumnas(visor.getNumColumns());
        if (screen.hayLineaArriba() && screen.hayLineaAbajo()) {
            visor.writeLineUp(screen.getLineaTopLeft());
            visor.writeRightUp(screen.getLineaTopRight());
            visor.writeLineDown(screen.getLineaDownLeft());
            visor.writeRightDown(screen.getLineaDownRight());
        }
        else if (screen.hayLineaArriba()) {
            visor.writeLineUp(screen.getLineaTopLeft());
            visor.writeRightUp(screen.getLineaTopRight());
        }
        else if (screen.hayLineaAbajo()) {
            visor.writeLineDown(screen.getLineaDownLeft());
            visor.writeRightDown(screen.getLineaDownRight());
        }
        else {
            //Se ha llamado a showScreen sin mensajes
        }
    }

}
