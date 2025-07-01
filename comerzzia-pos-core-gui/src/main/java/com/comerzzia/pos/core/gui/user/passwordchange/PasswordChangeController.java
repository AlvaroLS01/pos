
package com.comerzzia.pos.core.gui.user.passwordchange;

import java.awt.Toolkit;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.service.user.UserInvalidLoginException;
import com.comerzzia.core.facade.service.user.UserServiceFacade;
import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.cryptography.CriptoException;
import com.comerzzia.pos.util.cryptography.CriptoUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import lombok.extern.log4j.Log4j;

@Controller
@CzzScene
@Log4j
public class PasswordChangeController extends SceneController {
    @FXML
    protected PasswordField pfNewPw, pfOldPw, pfConfirmPw;
    
    @FXML
    protected Label lbUser;
    
    @Autowired
    protected Session session;

    @Autowired
    protected UserServiceFacade userService;
    
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
        
        lbUser.setText(session.getPOSUserSession().getUser().getUserDes());
        pfOldPw.setText("");
        pfNewPw.setText("");
        pfConfirmPw.setText("");
    }

    @Override
    public void initializeFocus() {
        pfOldPw.requestFocus();
    }
    
    @FXML
    public void actionAccept(){
    	String oldPw = pfOldPw.getText();
    	String newPw = pfNewPw.getText();
    	String confirmPw = pfConfirmPw.getText();
    	
    	if(oldPw == null || oldPw.isEmpty()){
    		Toolkit.getDefaultToolkit().beep();
    		pfOldPw.requestFocus();
    		return;
    	}
    	if(newPw == null || newPw.isEmpty()){
    		Toolkit.getDefaultToolkit().beep();
    		pfNewPw.requestFocus();
    		return;
    	}
    	if(confirmPw == null || confirmPw.isEmpty()){
    		Toolkit.getDefaultToolkit().beep();
    		pfConfirmPw.requestFocus();
    		return;
    	}
    	if(!newPw.equals(confirmPw)){
    		DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Los campos contraseña nueva y confirmar contraseña no coinciden"));
    		pfNewPw.requestFocus();
    		return;
    	}
    	
    	auditOperation(new ComerzziaAuditEventBuilder().addOperation("changePassword"));
    	
		try {
			
			userService.changePassword(session.getPOSUserSession().getUser().getUserId(), 
					CriptoUtils.encrypt(CriptoUtils.ALGORITHM_MD5, oldPw.getBytes("UTF-8")), 
					CriptoUtils.encrypt(CriptoUtils.ALGORITHM_MD5, newPw.getBytes("UTF-8")));
			
        	DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("Clave cambiada correctamente"));
            closeSuccess();

		} catch (UnsupportedEncodingException | CriptoException e) {
			log.debug("actionAccept() - Unexpected cripto exception: ", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Error cambiando la contraseña"), e);
		} catch (UserInvalidLoginException ex) {
       		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La clave actual no es correcta"));
		} 
    }

}
