package com.comerzzia.pos.util.listeners;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.util.config.SpringContext;

@Component
public class ListenersExecutor {
	
	private Logger log = Logger.getLogger(ListenersExecutor.class);

	@SuppressWarnings("unchecked")
    public void executeListeners(Class<? extends POSListener> listenerType, Object... args) throws POSListenerException {
		Map<String, POSListener> listenersMap = (Map<String, POSListener>) SpringContext.getBeansOfType(listenerType);
		
		Collection<POSListener> values = listenersMap.values();
		List<POSListener> listeners = new ArrayList<POSListener>();
		listeners.addAll(values);
		Collections.sort(listeners, new ComparatorPriorityListener());
		
		for (POSListener listener : listeners) {
			try {
				listener.execute(args);
			}
			catch (POSListenerException e) {
				log.error("init() - Ha habido un problema arrancando el listener " + listener.getClass().getSimpleName());
				throw e;
			}
		}
	}

	@SuppressWarnings("unchecked")
    public void executeListeners(Class<? extends POSListener> listenerType, String methodName, Object... args) throws Exception {
		Map<String, POSListener> listenersMap = (Map<String, POSListener>) SpringContext.getBeansOfType(listenerType);
		
		Collection<POSListener> values = listenersMap.values();
		List<POSListener> listeners = new ArrayList<POSListener>();
		listeners.addAll(values);
		Collections.sort(listeners, new ComparatorPriorityListener());
		
		for (POSListener listener : listeners) {
			try {
				Object[] parameters ={new Object()};
			    Object[] param ={args};
				
				Method method = listener.getClass().getMethod(methodName, parameters.getClass());
				if(method != null) {
					method.invoke(listener, param);
				}
				else {
					log.error("executeListeners() - No existe el método " + methodName + " para la clase " + listenerType.getSimpleName());
				}
			}
			catch (Exception e) {
				log.error("init() - Ha habido un problema arrancando el listener " + listener.getClass().getSimpleName());
				throw e;
			}
		}
	}

}
