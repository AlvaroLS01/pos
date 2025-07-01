
package com.comerzzia.pos.core.gui.web;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.view.SceneView;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebActionBrowserView extends SceneView {

}
