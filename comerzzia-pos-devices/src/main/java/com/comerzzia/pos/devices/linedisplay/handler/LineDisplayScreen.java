
package com.comerzzia.pos.devices.linedisplay.handler;

public class LineDisplayScreen {

    public static final int TOP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public static final String EURO_SIGN = Character.toString((char) 305);
    public static final String DOLAR_SIGN = Character.toString((char) 36);
    public static final String ENYE_MINISCULA_SIGN = Character.toString((char) 241);
    public static final String ENYE_MAYUSCULA_SIGN = Character.toString((char) 209);

    private String lineaTopLeft;
    private String lineaTopRight;
    private String lineaDownLeft;
    private String lineaDownRight;
    private Integer numColumnas = null;

    public LineDisplayScreen(String lineaTopLeft, String lineaTopRight, String lineaDownLeft, String lineaDownRight) {
        if (lineaTopLeft != null) {
            lineaTopLeft = reemplazaValores(lineaTopLeft);
        }
        if (lineaTopRight != null) {
            lineaTopRight = reemplazaValores(lineaTopRight);
        }
        if (lineaDownLeft != null) {
            lineaDownLeft = reemplazaValores(lineaDownLeft);
        }
        if (lineaDownRight != null) {
            lineaDownRight = reemplazaValores(lineaDownRight);
        }
        this.lineaTopLeft = (lineaTopLeft == null ? "" : lineaTopLeft);
        this.lineaTopRight = (lineaTopRight == null ? "" : lineaTopRight);
        this.lineaDownLeft = (lineaDownLeft == null ? "" : lineaDownLeft);
        this.lineaDownRight = (lineaDownRight == null ? "" : lineaDownRight);
    }

    public LineDisplayScreen(String left, String rigth, int tipo) {
        if (left != null) {
            left = reemplazaValores(left);
        }
        if (rigth != null) {
            rigth = reemplazaValores(rigth);
        }
        if (tipo == TOP) {
            this.lineaTopLeft = (left == null ? "" : left);
            this.lineaTopRight = (rigth == null ? "" : rigth);
        }
        else {
            this.lineaDownLeft = (left == null ? "" : left);
            this.lineaDownRight = (rigth == null ? "" : rigth);
        }
    }

    public LineDisplayScreen(String cadena, int tipoVertical, int tipoHorizontal) {
        if (cadena != null) {
            cadena = reemplazaValores(cadena);
        }
        else {
            cadena = "";
        }
        if (tipoVertical == TOP) {
            if (tipoHorizontal == LEFT) {
                this.lineaTopLeft = cadena;
            }
            else {
                this.lineaTopRight = cadena;
            }
        }
        else {
            if (tipoHorizontal == LEFT) {
                this.lineaDownLeft = cadena;
            }
            else {
                this.lineaDownRight = cadena;
            }
        }
    }

    public String getLineaTopLeft() {
        return lineaTopLeft;
    }

    public void setLineaTopLeft(String lineaTopLeft) {
        this.lineaTopLeft = (lineaTopLeft == null ? "" : lineaTopLeft);
        divideColumnas();
    }

    public String getLineaTopRight() {
        return lineaTopRight;
    }

    public void setLineaTopRight(String lineaTopRight) {
        this.lineaTopRight = (lineaTopRight == null ? "" : lineaTopRight);
        divideColumnas();
    }

    public String getLineaDownLeft() {
        return lineaDownLeft;
    }

    public void setLineaDownLeft(String lineaDownLeft) {
        this.lineaDownLeft = (lineaDownLeft == null ? "" : lineaDownLeft);
        divideColumnas();
    }

    public String getLineaDownRight() {
        return lineaDownRight;
    }

    public void setLineaDownRight(String lineaDownRight) {
        this.lineaDownRight = (lineaDownRight == null ? "" : lineaDownRight);
        divideColumnas();
    }

    public void setNumColumnas(Integer numColumnas) {
        this.numColumnas = numColumnas;
        divideColumnas();
    }

    public boolean hayLineaArriba() {
        if (lineaTopRight.isEmpty()) {
            return !lineaTopLeft.isEmpty();
        }
        return true;
    }

    public boolean hayLineaAbajo() {
        if (lineaDownRight.isEmpty()) {
            return !lineaDownLeft.isEmpty();
        }
        return true;
    }

    private void divideColumnas() {
        if (numColumnas != null) {
            if (hayLineaArriba()) {
                int longright = lineaTopRight == null ? 0 : lineaTopRight.length();
                if (longright + 2 < numColumnas) {
                    if (lineaTopLeft != null && !lineaTopLeft.isEmpty()) {
                        if (lineaTopLeft.length() > (numColumnas - longright + 2)) {
                            lineaTopLeft = lineaTopLeft.substring(0, numColumnas - (longright + 2)) + "..";
                        }
                    }
                }
            }
            if (hayLineaAbajo()) {
                int longright = lineaDownRight == null ? 0 : lineaDownRight.length();
                if (longright + 2 < numColumnas) {
                    if (lineaDownLeft != null && !lineaDownLeft.isEmpty()) {
                        if (lineaDownLeft.length() > (numColumnas - longright + 2)) {
                            lineaDownLeft = lineaDownLeft.substring(0, numColumnas - (longright + 2)) + "..";
                        }
                    }
                }
            }
        }
    }

    private String reemplazaValores(String cadena) {
        cadena = cadena.replace("€", EURO_SIGN);
        cadena = cadena.replace("$", DOLAR_SIGN);
        cadena = cadena.replace("ñ", ENYE_MINISCULA_SIGN);
        cadena = cadena.replace("Ñ", ENYE_MAYUSCULA_SIGN);
        return cadena;
    }
}
