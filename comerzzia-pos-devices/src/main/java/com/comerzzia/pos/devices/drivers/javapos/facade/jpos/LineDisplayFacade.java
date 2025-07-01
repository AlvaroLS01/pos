
package com.comerzzia.pos.devices.drivers.javapos.facade.jpos;

import jpos.JposException;
import jpos.LineDisplay;
import jpos.LineDisplayConst;

public class LineDisplayFacade extends CommonDeviceCatFacade {

    /**
     * Constructor: Instancia un nuevo LineDisplay con nombre lógico pasado por parámetro
     *
     * @param logicalName Nombre logico del dispositivo. Se configura de tal manera en el archivo jpos.xml
     */
    public LineDisplayFacade(String logicalName) {
        super();
        this.setDevice(new LineDisplay());
        this.setLogicalName(logicalName);
    }

    /**
     * Constructor: Reutiliza una instancia LineDisplay pasada como parametro con nombre logico de dispositivo pasado por paramatro
     *
     * @param lineDisplay La instancia de tipo LineDisplay a reutilizar
     * @param logicalName Nombre logico del dispositivo. Se configura de tal manera en el archivo jpos.xml
     */
    public LineDisplayFacade(LineDisplay lineDisplay, String logicalName) {
        super();
        this.setDevice(lineDisplay);
        this.setLogicalName(logicalName);
    }

    /* (non-Javadoc)
     * @see idinfor.jpos.CommonDeviceCatFacade#getDevice()
     */
    @Override
    public LineDisplay getDevice() {
        return (LineDisplay) super.getDevice();
    }

    /**
     * Limpia el texto del viewport del LineDisplay
     *
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void clearText() throws JposException {
        getDevice().clearText();
    }

    /**
     * Muestra el texto pasado por parametro en modo normal en la fila y columna donde se encuentre el cursor.
     *
     * @param text String a mostrar
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void displayText(String text) throws JposException {
        this.displayText(text, LineDisplayConst.DISP_DT_NORMAL);
    }

    /**
     * Muestra el texto pasado por parametro en modo pasado por parametro en la fila y columna donde se encuentre el cursor.
     *
     * @param text String a mostrar
     * @param mode Constante que representa el modo en el que se muestra el texto en el display. Los diferentes valores pueden encontrarse en la clase LineDisplayConst.class dentro de jposversion.jar, paquete jpos. Son: LineDisplayConst.DISP_DT_NORMAL, LineDisplayConst.DISP_DT_BLINK, LineDisplayConst.DISP_DT_REVERSE, LineDisplayConst.DISP_DT_BLINK_REVERSE
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void displayText(String text, int mode) throws JposException {
        getDevice().displayText(text, mode);
    }

    /**
     * Muestra el texto pasado por parametro en modo pasado por parametro en la fila y columna pasadas por parametro.
     *
     * @param text String a mostrar
     * @param row La fila donde debe comenzarse a mostrar el texto. Utilizar una fila que exceda el viewport puede generar JposException.
     * @param column La columna donde debe comenzarse a mostrar el texto. Utilizar una columna que exceda el viewport puede generar JposException.
     * @param mode Constante que representa el modo en el que se muestra el texto en el display. Los diferentes valores pueden encontrarse en la clase LineDisplayConst.class dentro de jposversion.jar, paquete jpos. Son: LineDisplayConst.DISP_DT_NORMAL, LineDisplayConst.DISP_DT_BLINK, LineDisplayConst.DISP_DT_REVERSE, LineDisplayConst.DISP_DT_BLINK_REVERSE
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void displayTextAt(String text, int row, int column, int mode) throws JposException {
        getDevice().displayTextAt(row, column, text, mode);
    }

    /**
     * Muestra el texto pasado por parametro en modo normal en la fila y columna pasadas por parametro.
     *
     * @param text String a mostrar
     * @param row La fila donde debe comenzarse a mostrar el texto. Utilizar una fila que exceda el viewport puede generar JposException.
     * @param column La columna donde debe comenzarse a mostrar el texto. Utilizar una columna que exceda el viewport puede generar JposException.
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void displayTextAt(String text, int row, int column) throws JposException {
        this.displayTextAt(text, row, column, LineDisplayConst.DISP_DT_NORMAL);
    }

    /**
     * Muestra el texto pasado por parametro alineado a la derecha, en modo normal en la fila y columna donde se encuentre el cursor.
     *
     * @param text String a mostrar
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void displayRightAlignText(String text) throws JposException {
        int startColumn = getDevice().getColumns() - text.length();
        this.displayTextAt(text, getDevice().getCursorRow(), startColumn);
    }

    /**
     * Muestra el texto pasado por parametro en modo normal en la fila y columna donde se encuentre el cursor, haciendo que el cursor pase a la siguiente fila, creando un salto de linea.
     *
     * @param text String a mostrar
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void displayLine(String text) throws JposException {
        this.displayText(text);
        if (getDevice().getCursorRow() < getDevice().getRows() - 1) {
            getDevice().setCursorRow(getDevice().getCursorRow() + 1);
        }
    }

    /**
     * Muestra el texto pasado por parametro en modo normal alineado a la derecha, en la fila y columna donde se encuentre el cursor, haciendo que el cursor pase a la siguiente fila, creando un salto de linea.
     *
     * @param text String a mostrar
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void displayLineRightAlign(String text) throws JposException {
        this.displayRightAlignText(text);
        if (getDevice().getCursorRow() < getDevice().getRows() - 1) {
            getDevice().setCursorRow(getDevice().getCursorRow() + 1);
        }
    }

    /**
     * Scrollea el viewport a la izquierda
     *
     * @param columns El numero de columnas a scrollear. Si el parametro aportado es null, scrolleara una columna.
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void scrollLeft(Integer columns) throws JposException {
        if (columns == null) {
            columns = 1;
        }
        getDevice().scrollText(LineDisplayConst.DISP_ST_RIGHT, columns);
    }

    /**
     * Scrollea el viewport a la derecha
     *
     * @param columns El numero de columnas a scrollear. Si el parametro aportado es null, scrolleara una columna.
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void scrollRight(Integer columns) throws JposException {
        if (columns == null) {
            columns = 1;
        }
        getDevice().scrollText(LineDisplayConst.DISP_ST_LEFT, columns);
    }

    /**
     * Scrollea el viewport hacia abajo
     *
     * @param rows El numero de filas a scrollear. Si el parametro aportado es null, scrolleara una filas.
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void scrollDown(Integer rows) throws JposException {
        if (rows == null) {
            rows = 1;
        }
        getDevice().scrollText(LineDisplayConst.DISP_ST_UP, rows);
    }

    /**
     * Scrollea el viewport hacia arriba
     *
     * @param rows El numero de filas a scrollear. Si el parametro aportado es null, scrolleara una filas.
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public void scrollUp(Integer rows) throws JposException {
        if (rows == null) {
            rows = 1;
        }
        getDevice().scrollText(LineDisplayConst.DISP_ST_DOWN, rows);
    }

    /**
     * Nos devuelve el número de columnas que tiene el visor
     *
     * @return El número de columnas que tiene el visor
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public int getColumns() throws JposException {
        return getDevice().getColumns();
    }
    
    /**
     * Nos devuelve el número de filas que tiene el visor
     *
     * @return El número de filas que tiene el visor
     * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
     */
    public int getRows() throws JposException {
        return getDevice().getRows();
    }
}
