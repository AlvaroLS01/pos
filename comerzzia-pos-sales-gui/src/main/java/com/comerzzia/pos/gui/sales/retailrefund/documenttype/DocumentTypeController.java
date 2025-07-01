package com.comerzzia.pos.gui.sales.retailrefund.documenttype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.helper.HelperSceneController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.Initializable;

@Controller
@SuppressWarnings({"unchecked"})
@CzzScene
public class DocumentTypeController extends HelperSceneController<DocumentTypeRow> implements Initializable, ButtonsGroupController{

    public static final String PARAM_DOC_POSSIBLE_INPUT = "POSSIBLES_DOCS";
   
	public void setTitle() {
		lbTitle.setText(I18N.getText("Tipo de Documento"));
	}

	@Override
	public List<DocumentTypeRow> buildHelpersRows(Map<String, Object> params) {
		List<DocumentTypeRow> docs = new ArrayList<>();
		List<String> documents = (List<String>)params.get(PARAM_DOC_POSSIBLE_INPUT);
        
        for(String codDocument: documents){
    		docs.add(new DocumentTypeRow(session.getApplicationSession().getDocTypeByDocTypeCode(codDocument)));
        }
        
        return docs;
	}
}