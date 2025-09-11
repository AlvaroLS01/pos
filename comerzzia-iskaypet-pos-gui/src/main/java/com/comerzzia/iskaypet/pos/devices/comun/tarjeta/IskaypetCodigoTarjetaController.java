package com.comerzzia.iskaypet.pos.devices.comun.tarjeta;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class IskaypetCodigoTarjetaController extends CodigoTarjetaController {

    public static final String PARAMETRO_IN_TEXTONUMERO = "TEXTO_NUMERO";

    @FXML
    private Label lbNumero;

    @Override
    public void initializeForm() throws InitializeGuiException {
        String textoNumero = (String) getDatos().get(PARAMETRO_IN_TEXTONUMERO);
        if (StringUtils.isNotEmpty(textoNumero)) {
            lbNumero.setText(textoNumero);
        }

        super.initializeForm();
    }

}
