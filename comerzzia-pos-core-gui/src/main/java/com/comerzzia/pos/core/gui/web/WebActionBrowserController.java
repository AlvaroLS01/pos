
package com.comerzzia.pos.core.gui.web;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.controllers.WebController;



@Component
@CzzActionScene
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebActionBrowserController extends WebController {

}
