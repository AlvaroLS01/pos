package com.comerzzia.pos.util.config;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Clase que contiene el contexto Spring de la aplicación, con identificador "comerzzia", necesario para utilizar la inyección de dependencia.
 * Esta clase escanea el paquete com.comerzzia y busca los componentes y las clases de configuración para inicializar los
 * beans que podrán ser utilizados en cualquier momento.
 * </p>
 * <p>
 * Esta clase no inicializa los beans en el momento de la búsqueda inicial para evitar operaciones propias de JavaFX fuera del 
 * hilo principal de la aplicación, por lo que se realiza la inicialización en el momento que sea necesario un determinado bean.
 * </p>
 */
@Component
@Lazy(false)
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

	public static ApplicationContext get() {
		if (applicationContext == null) {
			throw new IllegalStateException("Spring context not initialized or the package " + SpringContext.class.getPackage().getName() + " not in context scan");
		}

		return applicationContext;
	}
	
	protected SpringContext() {
	}	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContext.applicationContext = applicationContext;
	}
    
    	
	/**
	 * Devuelve un bean del tipo que se le pasa por parámetro. Si encuentra más de un bean del mismo tipo devolverá aquel cuyo nombre
	 * coincida con el de la clase que se pasa.
	 * @param tipoRequerido Clase del objeto
	 * @return Objeto del tipo requerido
	 */
	@SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> tipoRequerido) {
		try{
			//Busca en el contexto por tipo
			return get().getBean(tipoRequerido);
		}catch(NoSuchBeanDefinitionException e){
			//Si falla buscaremos en el contexto por el nombre por defecto del Bean (nombre de la clase con primera letra minúscula)
			String[] split = tipoRequerido.getName().split("\\.");
			String name = split[split.length-1];
			name = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());
			return (T) get().getBean(name);
		}
	}
	
	/**
	 * Devuelve un bean del tipo que se le pasa por parámetro. Si encuentra más de un bean del mismo tipo devolverá aquel cuyo nombre
	 * coincida con el de la clase que se pasa.
	 * @param tipoRequerido Clase del objeto
	 * @param args Parámetros que se le pasan al constructor
	 * @return Objeto del tipo requerido
	 */
	@SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> tipoRequerido, Object... args) {
		try{
			//Busca en el contexto por tipo
			return get().getBean(tipoRequerido, args);
		}catch(NoSuchBeanDefinitionException e){
			//Si falla buscaremos en el contexto por el nombre por defecto del Bean (nombre de la clase con primera letra minúscula)
			String[] split = tipoRequerido.getName().split("\\.");
			String name = split[split.length-1];
			name = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());
			return (T) get().getBean(name, args);
		}
	}
		
	public static <T> void destroyBean(Class<T> tipoRequerido) {
		String[] split = tipoRequerido.getName().split("\\.");
		String name = split[split.length-1];
		name = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());
		get().getAutowireCapableBeanFactory();
		((DefaultListableBeanFactory) ((AbstractApplicationContext)get()).getBeanFactory()).destroySingleton(name);
	}
	
	public static <T> Map<String, T> getBeansOfType(Class<T> type) {
		return get().getBeansOfType(type);
	}

}
