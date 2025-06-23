package com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.comerzzia.pos.util.i18n.I18N;

@Service
public class FileCounterRangeManagerImpl implements CounterRangeManager{

	 protected static Logger log = Logger.getLogger(FileCounterRangeManagerImpl.class);
	
	 protected static final String FISCAL_DIRECTORY = "fiscal/";
	 protected static final String EXTENSION_PROPERTIES = ".properties";
	 
	 protected Map<String, String> rangeIdsCache = new HashMap<String, String>();
	 
	 public String findRangeId(CounterRangeParamDto counterRangeParam) {
	 	String key = counterRangeParam.getCounterId()+"__"+counterRangeParam.getDivisor1()+"__"+counterRangeParam.getDivisor2()+"__"+counterRangeParam.getDivisor3();
		String value = this.rangeIdsCache.get(key);
		
		if(StringUtils.isBlank(value)) {
			value = readFromPropertiesFile(key);
		}
		return value;
	 }
	 
	@Override
	public List<CounterRangeDto> findAllRanges() throws Exception {
		log.debug("findAllRanges() - Consultando todos los rangos");
		List<CounterRangeDto> counterRanges = new ArrayList<CounterRangeDto>();
		try {
			String filePath = FISCAL_DIRECTORY;
			Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			URL url = classLoader.getResource(filePath);
			if(url != null) {
				File directory = Paths.get(url.toURI()).toFile();
			
				FilenameFilter fileFilter = new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						if(name.endsWith(EXTENSION_PROPERTIES)) {
							return true;
						}
						
						return false;
					}
				};
				
				File[] files = directory.listFiles(fileFilter);
				for(File file : files) {
					String key = file.getName().substring(0, file.getName().lastIndexOf(EXTENSION_PROPERTIES));
					String[] keySplit = key.split("__");
					CounterRangeDto counterRange = new CounterRangeDto();
					counterRange.setCounterId(keySplit[0]);
					counterRange.setDivisor1(keySplit[1]);
					counterRange.setDivisor2(keySplit[2]);
					counterRange.setDivisor3(keySplit[3]);
					CounterRangeParamDto param = new CounterRangeParamDto();
					BeanUtils.copyProperties(param, counterRange);
					counterRange.setRangeId(findRangeId(param));
					counterRanges.add(counterRange);
				}
			}else {
				throw new Exception(I18N.getTexto("No existe un directorio /fiscal en los recursos"));
			}
			
		} catch (Exception e) {
			log.error("findAllRanges() - Ha ocurrido un error al buscar los rangos configurados", e);
			throw e;
		}
		
		
		return counterRanges;
	}	
	 
	 protected String readFromPropertiesFile(String key) {
	    	try {
	    		String filePath = FISCAL_DIRECTORY+key+EXTENSION_PROPERTIES;
	    		log.debug("readFromPropertiesFile() - Reading file " + filePath);
	    		
		    	Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
				ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
				URL url = classLoader.getResource(filePath);
				
				
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	            
	            String value = in.readLine();
	            in.close();
	            if(value != null) {
	            	value = value.trim();
	            	rangeIdsCache.put(key, value);
	            }
				
				return rangeIdsCache.get(key);
	    	}
	    	catch (Exception e) {
	    		log.warn("readFromPropertiesFile() - No se ha encontrado el fichero "+key+".properties: " + e.getMessage());
	    		return null;
	    	}
			
	}

	@Override
	public CounterRangeDto saveRange(CounterRangeKey key, String value) throws Exception {
		log.debug("saveRange() - Salvando rango "+key.getCounterId()+"__"+key.getDivisor1()+"__"+key.getDivisor2()+"__"+key.getDivisor3()+" with value "+value);
		try {
			String fileName = key.getCounterId()+"__"+key.getDivisor1()+"__"+key.getDivisor2()+"__"+key.getDivisor3();
			String filePath = FISCAL_DIRECTORY+fileName+EXTENSION_PROPERTIES;
			Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			URL url = classLoader.getResource(filePath);

			if(url == null) {
				URL urlParent = classLoader.getResource(FISCAL_DIRECTORY);
				String finalFileName = fileName+EXTENSION_PROPERTIES;
				File newFile = new File(URLDecoder.decode(urlParent.getFile(), StandardCharsets.UTF_8.toString()), finalFileName);
				newFile.createNewFile();
				PrintWriter writer = new PrintWriter(newFile);
				writer.write(value);
				writer.close();
			}else {
				PrintWriter writer = new PrintWriter(new File(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8.toString())));
				writer.write(value);
				writer.close();
			}
				this.rangeIdsCache.clear();
				
			CounterRangeDto counterRange = new CounterRangeDto();
			counterRange.setCounterId(key.getCounterId());
			counterRange.setDivisor1(key.getDivisor1());
			counterRange.setDivisor2(key.getDivisor2());
			counterRange.setDivisor3(key.getDivisor3());
			counterRange.setRangeId(value);
			return counterRange;
		}catch (Exception e) {
			log.error("saveRange() - Ha ocurrido un error al salvar el rango", e);
			throw e;
    	}
	}

	@Override
	public void deleteRange(CounterRangeKey key) throws Exception {
		log.debug("deleteRange() - Eliminando rango "+key.getCounterId()+"__"+key.getDivisor1()+"__"+key.getDivisor2()+"__"+key.getDivisor3());
		try {
			String fileName = key.getCounterId()+"__"+key.getDivisor1()+"__"+key.getDivisor2()+"__"+key.getDivisor3();
			String filePath = FISCAL_DIRECTORY+fileName+EXTENSION_PROPERTIES;
			Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			URL url = classLoader.getResource(filePath);
			
			File fileToDelete = new File(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8.toString()));
			fileToDelete.delete();
			this.rangeIdsCache.clear();
		}catch(Exception e) {
			log.error("deleteRange() - Ha ocurrido un error al eliminar el rango", e);
			throw e;
		}

	}
	
}
