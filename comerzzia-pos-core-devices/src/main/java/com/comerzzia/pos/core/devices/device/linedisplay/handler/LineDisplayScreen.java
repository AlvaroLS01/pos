
package com.comerzzia.pos.core.devices.device.linedisplay.handler;

public class LineDisplayScreen {

    public static final int TOP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public static final String EURO_SIGN = Character.toString((char) 305);
    public static final String DOLAR_SIGN = Character.toString((char) 36);
    public static final String ENYE_LOWERCASE_SIGN = Character.toString((char) 241);
    public static final String ENYE_UPPERCASE_SIGN = Character.toString((char) 209);

    protected String topLeftLine;
    protected String topRightLine;
    protected String downLeftLine;
    protected String downRightLine;
    protected Integer colNum = null;

    public LineDisplayScreen(String lineaTopLeft, String lineaTopRight, String lineaDownLeft, String lineaDownRight) {
        if (lineaTopLeft != null) {
            lineaTopLeft = replaceValues(lineaTopLeft);
        }
        if (lineaTopRight != null) {
            lineaTopRight = replaceValues(lineaTopRight);
        }
        if (lineaDownLeft != null) {
            lineaDownLeft = replaceValues(lineaDownLeft);
        }
        if (lineaDownRight != null) {
            lineaDownRight = replaceValues(lineaDownRight);
        }
        this.topLeftLine = (lineaTopLeft == null ? "" : lineaTopLeft);
        this.topRightLine = (lineaTopRight == null ? "" : lineaTopRight);
        this.downLeftLine = (lineaDownLeft == null ? "" : lineaDownLeft);
        this.downRightLine = (lineaDownRight == null ? "" : lineaDownRight);
    }

    public LineDisplayScreen(String left, String rigth, int tipo) {
        if (left != null) {
            left = replaceValues(left);
        }
        if (rigth != null) {
            rigth = replaceValues(rigth);
        }
        if (tipo == TOP) {
            this.topLeftLine = (left == null ? "" : left);
            this.topRightLine = (rigth == null ? "" : rigth);
        }
        else {
            this.downLeftLine = (left == null ? "" : left);
            this.downRightLine = (rigth == null ? "" : rigth);
        }
    }

    public LineDisplayScreen(String cadena, int tipoVertical, int tipoHorizontal) {
        if (cadena != null) {
            cadena = replaceValues(cadena);
        }
        else {
            cadena = "";
        }
        if (tipoVertical == TOP) {
            if (tipoHorizontal == LEFT) {
                this.topLeftLine = cadena;
            }
            else {
                this.topRightLine = cadena;
            }
        }
        else {
            if (tipoHorizontal == LEFT) {
                this.downLeftLine = cadena;
            }
            else {
                this.downRightLine = cadena;
            }
        }
    }

    public String getTopLeftLine() {
        return topLeftLine;
    }

    public void setTopLeftLine(String lineaTopLeft) {
        this.topLeftLine = (lineaTopLeft == null ? "" : lineaTopLeft);
        divideColumns();
    }

    public String getTopRightLine() {
        return topRightLine;
    }

    public void setTopRightLine(String lineaTopRight) {
        this.topRightLine = (lineaTopRight == null ? "" : lineaTopRight);
        divideColumns();
    }

    public String getDownLeftLine() {
        return downLeftLine;
    }

    public void setDownLeftLine(String lineaDownLeft) {
        this.downLeftLine = (lineaDownLeft == null ? "" : lineaDownLeft);
        divideColumns();
    }

    public String getDownRightLine() {
        return downRightLine;
    }

    public void setDownRightLine(String lineaDownRight) {
        this.downRightLine = (lineaDownRight == null ? "" : lineaDownRight);
        divideColumns();
    }

    public void setColNum(Integer numColumnas) {
        this.colNum = numColumnas;
        divideColumns();
    }

    public boolean existLineUp() {
        if (topRightLine.isEmpty()) {
            return !topLeftLine.isEmpty();
        }
        return true;
    }

    public boolean existLineDown() {
        if (downRightLine.isEmpty()) {
            return !downLeftLine.isEmpty();
        }
        return true;
    }

    protected void divideColumns() {
        if (colNum != null) {
            if (existLineUp()) {
                int longright = topRightLine == null ? 0 : topRightLine.length();
                if (longright + 2 < colNum) {
                    if (topLeftLine != null && !topLeftLine.isEmpty()) {
                        if (topLeftLine.length() > (colNum - longright + 2)) {
                            topLeftLine = topLeftLine.substring(0, colNum - (longright + 2)) + "..";
                        }
                    }
                }
            }
            if (existLineDown()) {
                int longright = downRightLine == null ? 0 : downRightLine.length();
                if (longright + 2 < colNum) {
                    if (downLeftLine != null && !downLeftLine.isEmpty()) {
                        if (downLeftLine.length() > (colNum - longright + 2)) {
                            downLeftLine = downLeftLine.substring(0, colNum - (longright + 2)) + "..";
                        }
                    }
                }
            }
        }
    }

    protected String replaceValues(String cadena) {
        cadena = cadena.replace("€", EURO_SIGN);
        cadena = cadena.replace("$", DOLAR_SIGN);
        cadena = cadena.replace("ñ", ENYE_LOWERCASE_SIGN);
        cadena = cadena.replace("Ñ", ENYE_UPPERCASE_SIGN);
        return cadena;
    }
}
