package com.comerzzia.pos.core.gui.components.fxtable.cells;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import com.comerzzia.pos.util.format.FormatUtils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Esta clase da solución a la construcción de celdas personalizadas para las
 * tablas así como el alineamiento dentro de estas tablas de los valores
 * recibidos
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CellFactoryBuilder {

    // Log
    private static final Logger log = Logger.getLogger(CellFactoryBuilder.class.getName());

    // Instancia
    private static CellFactoryBuilder instance;

    // FormatUtil
    private static FormatUtils fu =  FormatUtils.getInstance();

    public static final String RIGHT_ALIGN_STYLE = "rightCol";
    public static final String LEFT_ALIGN_STYLE = "leftCol";
    public static final String CENTER_ALIGN_STYLE = "centerCol";

    /**
     * Devuelve la instancia única de la clase
     *
     * @return
     */
    public static CellFactoryBuilder getInstance() {
        if (instance == null) {
            instance = new CellFactoryBuilder();
        }
        return instance;
    }

    /**
     * Constructor
     */
    public CellFactoryBuilder() {
    }

    /**
     * Construye un renderer para una tabla y columna
     *
     * @param nombreTabla
     * @param nombreColumna
     * @param decimales
     * @param alineacion
     * @return
     */    
	public static Callback createCellRendererCelda(final String nombreTabla, final String nombreColumna, final Integer decimales, final String alineacion) {
        //log.debug("createCellRendererCelda() -tabla :" + nombreTabla + " , en columna:" + nombreColumna);
        Callback rendererCelda;
        rendererCelda = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            return;
                        }

                        try {
                            String espaciadoDer = "";
                            if (alineacion!=null && alineacion.equals(RIGHT_ALIGN_STYLE)){
                            	//Se añade espacio para que los datos no se tapen por el autoscroll vertical de la tabla
                                espaciadoDer = espaciadoDer + "     ";
                            }
                            if (item != null) {
                                if (item instanceof String) {
                                    setText(item.toString()+espaciadoDer);
                                }
                                else if (item instanceof BigDecimal) { // Si es BigDecimal, redondeamos
                                    BigDecimal valor = (BigDecimal) item;
                                    setText(fu.formatNumber(valor, decimales)+espaciadoDer);
                                }
                                else if (item instanceof Date) {
                                    setText(fu.formatDate((Date) item)+espaciadoDer);
                                }
                                else {
                                    setText(item.toString()+espaciadoDer);
                                }
                            }
                        }
                        catch (Exception e) {
                            log.error("createCellRendererCelda() - Error actualizando valor de la tabla :" + nombreTabla + " , en columna:" + nombreColumna + " , valor:" + item.toString(), e);
                            setText("ERR");
                        }
                    }
                };
                cell.getStyleClass().add(nombreTabla + "-" + nombreColumna);
                cell.getStyleClass().add(alineacion);
                //cell.setTextAlignment(TextAlignment.RIGHT);
                return cell;
            }
        };

        return rendererCelda;
    }

    public static Callback createCellRendererCeldaFechaHora(final String nombreTabla, final String nombreColumna, final Integer decimales, final String alineacion) {
        //log.debug("createCellRendererCeldaFechaHora() -tabla :" + nombreTabla + " , en columna:" + nombreColumna);
        Callback rendererCelda;
        rendererCelda = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell() {                    
					@Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            return;
                        }

                        try {
                            if (item != null) {
                                setText(fu.formatDate((Date) item) + " - " + fu.formatTime((Date) item));
                            }
                        }
                        catch (Exception e) {
                            log.error("createCellRendererCeldaFechaHora() - Error actualizando valor de la tabla :" + nombreTabla + " , en columna:" + nombreColumna + " , valor:" + item.toString(), e);
                            setText("ERR");
                        }
                    }
                };
                cell.getStyleClass().add(nombreTabla + "-" + nombreColumna);
                cell.getStyleClass().add(alineacion);
                return cell;
            }
        };

        return rendererCelda;
    }
    
    public static Callback createCellRendererCeldaFecha(final String nombreTabla, final String nombreColumna, final Integer decimales, final String alineacion) {
        //log.debug("createCellRendererCeldaFechaHora() -tabla :" + nombreTabla + " , en columna:" + nombreColumna);
        Callback rendererCelda;
        rendererCelda = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            return;
                        }

                        try {
                            if (item != null) {
                                setText(fu.formatDate((Date) item));
                            }
                        }
                        catch (Exception e) {
                            log.error("createCellRendererCeldaFecha() - Error actualizando valor de la tabla :" + nombreTabla + " , en columna:" + nombreColumna + " , valor:" + item.toString(), e);
                            setText("ERR");
                        }
                    }
                };
                cell.getStyleClass().add(nombreTabla + "-" + nombreColumna);
                cell.getStyleClass().add(alineacion);
                return cell;
            }
        };

        return rendererCelda;
    }

    /**
     * Construye un renderer para una tabla y columna dando el medio de pago para un código de medio de pago
     *
     * @param nombreTabla
     * @param nombreColumna
     * @param decimales
     * @return
     */
    public static Callback createCellRendererCeldaMedioPago(final String nombreTabla, final String nombreColumna, final String alineacion) {
        //log.debug("createCellRendererCelda() -tabla :" + nombreTabla + " , en columna:" + nombreColumna);
        Callback rendererCelda;
        rendererCelda = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            return;
                        }

//                        try {
//                            if (item != null) {
//                                if (item instanceof String) {
//                                    MedioPagoBean pago = SpringContext.getBean(MediosPagosService.class).getMedioPago((String) item);
//                                    if (pago != null) {
//                                        setText(pago.getDesMedioPago());
//                                    }
//                                    else {
//                                        log.error("createCellRendererCeldaMedioPago() - No se ha encontrado el medio de pago con código: " + item);
//                                    }
//                                }
//                            }
//                        }
//                        catch (Exception e) {
//                            log.error("createCellRendererCeldaMedioPago() - Error actualizando valor de la tabla :" + nombreTabla + " , en columna:" + nombreColumna + " , valor:" + item.toString(), e);
//                            setText("ERR");
//                        }
                    }
                };
                cell.getStyleClass().add(nombreTabla + "-" + nombreColumna);
                cell.getStyleClass().add(alineacion);
                //cell.setTextAlignment(TextAlignment.RIGHT);
                return cell;
            }
        };

        return rendererCelda;
    }

    /**
     * Construye un renderer para una tabla y columna
     *
     * @param nombreTabla
     * @param nombreColumna
     * @param decimales
     * @return
     */
    public static Callback createCellRendererCeldaPermiso(final String nombreTabla, final String nombreColumna, final String alineacion) {
        //log.debug("createCellRendererPermiso() -tabla :" + nombreTabla + " , en columna:" + nombreColumna);
        Callback rendererCelda;
        rendererCelda = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            return;
                        }

                        try {
                            if (item != null && !empty) {
                                if (item instanceof String) {
                                    if (getStyleClass().contains("concedido")) {
                                        getStyleClass().remove("concedido");
                                    }
                                    if (getStyleClass().contains("denegado")) {
                                        getStyleClass().remove("denegado");
                                    }
                                    if (getStyleClass().contains("administrar")) {
                                        getStyleClass().remove("administrar");
                                    }
                                    if (getStyleClass().contains("noestablecido")) {
                                        getStyleClass().remove("noestablecido");
                                    }
                                    if (item.equals("permisos.concedido")) {
                                        getStyleClass().add("concedido");
                                    }
                                    else if (item.equals("permisos.denegado")) {
                                        getStyleClass().add("denegado");
                                    }
                                    else if (item.equals("permisos.administrar")) {
                                        getStyleClass().add("administrar");
                                    }
                                    else if (item.equals("permisos.noestablecido")) {
                                        getStyleClass().add("noestablecido");
                                    }
                                    else {
                                        getStyleClass().add("noestablecido");
                                    }

                                    //setText((String)item);
                                    // Si en lugar de poner estados por estilos deseamos imprimir texto, activaríamos la siguiente línea comentada
                                    //setText(I18nBundle.getInstance().getTexto(item.toString()));
                                }
                            }
                        }
                        catch (Exception e) {
                            log.error("createCellRendererPermiso() -Error actualizando valor de la tabla :" + nombreTabla + " , en columna:" + nombreColumna + " , valor:" + item.toString(), e);
                            setText("ERR");
                        }
                    }
                };
                cell.getStyleClass().add(nombreTabla + "-" + nombreColumna);
                cell.getStyleClass().add(alineacion);
                return cell;
            }
        };

        return rendererCelda;
    }
    
    public static Callback createCellRendererCeldaPorcentaje(final String nombreTabla, final String nombreColumna, final Integer decimales, final String alineacion){
        
        Callback rendererCelda;
        rendererCelda = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            return;
                        }

                        try {
                            if (item != null) {
                                setText(fu.formatNumber((BigDecimal) item, decimales) + "%");
                            }
                        }
                        catch (Exception e) {
                            log.error("createCellRendererCeldaPorcentaje() - Error actualizando valor de la tabla :" + nombreTabla + " , en columna:" + nombreColumna + " , valor:" + item.toString(), e);
                            setText("ERR");
                        }
                    }
                };
                cell.getStyleClass().add(nombreTabla + "-" + nombreColumna);
                cell.getStyleClass().add(alineacion);
                return cell;
            }
        };

        return rendererCelda;
        
    }
    
    public static Callback createCellRendererCeldaImporte(final String nombreTabla, final String nombreColumna, final String alineacion){
        
        Callback rendererCelda;
        rendererCelda = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                            return;
                        }

                        try {
                        	String espaciadoDer = "";
                            if (alineacion!=null && alineacion.equals(RIGHT_ALIGN_STYLE)){
                            	//Se añade espacio para que los datos no se tapen por el autoscroll vertical de la tabla
                                espaciadoDer = espaciadoDer + "     ";
                            }
                        	
                            if (item != null) {
                            	if(item instanceof BigDecimal) {
                            		setText(fu.formatAmount((BigDecimal) item) + espaciadoDer);
                            	} else {
                            		BigDecimal importe = fu.parseAmount((String) item);
                            		setText(fu.formatAmount(importe) + espaciadoDer);
                            	}
                            }
                        }
                        catch (Exception e) {
                            log.error("createCellRendererCeldaImporte() - Error actualizando valor de la tabla :" + nombreTabla + " , en columna: " + nombreColumna + ", valor: " + item.toString(), e);
                            setText("ERR");
                        }
                    }
                };
                cell.getStyleClass().add(nombreTabla + "-" + nombreColumna);
                cell.getStyleClass().add(alineacion);
                return cell;
            }
        };

        return rendererCelda;
        
    }

}
