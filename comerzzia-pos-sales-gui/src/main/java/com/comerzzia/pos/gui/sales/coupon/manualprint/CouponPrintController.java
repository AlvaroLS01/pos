package com.comerzzia.pos.gui.sales.coupon.manualprint;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
@CzzScene
public class CouponPrintController extends SceneController {

	private static Logger log = Logger.getLogger(CouponPrintController.class);
	
    @FXML
    protected Button btPrint;
    
    @FXML
    protected Label lbError;
    
//    @FXML
//    private ComboBox<PromocionGeneracionCuponesBean> cbPromociones;
            
    @FXML
    protected TextField tfCouponsCount;
    
//    @Autowired
//    private CuponesService cuponesService;
    
    @Autowired
    protected Session session;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void initializeComponents() {
    }

	@Override
    public void onSceneOpen() throws InitializeGuiException {
//        try {
//            // Actualizamos promociones activas
//            sesion.getSesionPromociones().actualizarPromocionesActivas();
//            
//            List<PromocionGeneracionCuponesBean> promosCupon = new ArrayList<PromocionGeneracionCuponesBean>();
//            Map<Long, PromocionGeneracionCuponesBean> promocionesActivasEmisionCuponManual = sesion.getSesionPromociones().getPromocionesActivasEmisionCuponManual();
//            for(PromocionGeneracionCuponesBean promocion : promocionesActivasEmisionCuponManual.values()) {
//            	if(promocion.isActiva()) {
//            		promosCupon.add(promocion);
//            	}
//            }
//            
//            ObservableList<PromocionGeneracionCuponesBean> listaPromos = FXCollections.observableList(promosCupon);
//			cbPromociones.setItems(listaPromos);
//        }
//        catch (PromocionesServiceException ex) {
//        	VentanaDialogoComponent.crearVentanaError(getStage(), ex);
//        	log.error("onSceneOpen() - Error al consultar promociones: " + ex.getMessage(), ex);
//        }
    }

    @Override
    public void initializeFocus() {
        btPrint.requestFocus();
    }

    @FXML
    public void printCouponsIntro(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            printCoupons();
        }
    }
    
    @FXML
    public void printCoupons(){
//    	try {
//            
//            PromocionGeneracionCuponesBean promo = (PromocionGeneracionCuponesBean)cbPromociones.getSelectionModel().getSelectedItem();
//            Long cantidad= FormatUtil.getInstance().desformateaLong(tfNumCupones.getText());
//            if(promo == null){
//                //No hay cupones para imprimir
//                lbError.setText(I18N.getTexto("Debe escoger una promoción generadora de cupones"));
//            }
//            else if(cantidad ==null){
//                lbError.setText(I18N.getTexto("Debe introducir una cantidad válida"));
//            }
//            else if(promo.isActiva()){
//                lbError.setText("");
//                List<CuponEmitidoTicket> cupones = cuponesService.getCuponesImpresionManual(promo.getIdPromocion(), cantidad.intValue());
//                if(cupones.isEmpty()){
//                    lbError.setText(I18N.getTexto("No existen cupones para impresión manual"));
//                }
//                else{
//                	Map<String,Object> mapaParametrosCupon = new HashMap<String,Object>();
//                    for(CuponEmitidoTicket cuponEmitido: cupones){
//                    	mapaParametrosCupon.put("cupon", cuponEmitido);                    	
//                    	SimpleDateFormat df = new SimpleDateFormat();
//                    	mapaParametrosCupon.put("fechaEmision", df.format(new Date()));
//                        try {
//							ServicioImpresion.imprimir(PagosController.PLANTILLA_CUPON, mapaParametrosCupon);
//						} catch (DispositivoException e) {
//							log.error("imprimirCupones() - Error al imprimir", e);
//						}
//                    }
//                }
//            }
//            else{
//                lbError.setText(I18N.getTexto("La promoción escogida no es válida"));
//            }
//        }
//        catch (CuponesServiceException ex) {
//        	log.error("imprimirCupones() - Error al imprimir", ex);
//            lbError.setText(I18N.getTexto("Error obteniendo los cupones a imprimir"));
//        }
//        catch (PromocionesServiceException ex) {
//        	log.error("imprimirCupones() - Error al imprimir", ex);
//            lbError.setText(I18N.getTexto("Error obteniendo los cupones a imprimir"));
//        }
        
    }
    
}
