package com.comerzzia.pos.util.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
public class SoftwareVersion {
	private static boolean jarsScanned = false;
	private static VersionInfo versionInfo = new VersionInfo();
	
	@Data
	@NoArgsConstructor
	public static class VersionInfo {
		private String version;
		private String versionRevision;
		private String buildCommit;
		private String buildNumber;
		
		private String remoteRepositoryVersion;				
		private String localRepositoryVersion;
		private boolean localCopyUpdated = false;
		
		private Map<String, String> modules = new HashMap<>();
		
		public void addModule(String module, String version) {
			modules.put(module, version);
		}
		
		public String toString() {			
			if (StringUtils.isNotBlank(localRepositoryVersion)) {
				return getLocalRepositoryVersion();
			}
			
			String result = "";
			
			if (StringUtils.isNotBlank(version)) {
				result = version;
				
				if (StringUtils.isNotBlank(versionRevision)) {
					result += " r-" + versionRevision;
				}
				
				String build = null;
				
				if (StringUtils.isNotBlank(buildNumber)) {
				   build = buildNumber;	
				} else if (StringUtils.isNotBlank(buildCommit)) {
					build = buildCommit;
				} 
				
				if (build != null) {
					result += " (Build " + build + ")";
				}
			}
			
			
			return result;
		}
	}
	
	public static VersionInfo get() {
		updateLocalCopyVersion();
		
		if (jarsScanned) return versionInfo;
		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		
		try {
	        Resource[] resources = resolver.getResources("classpath*:/META-INF/MANIFEST.MF");
	        if (resources != null) {
	            for (Resource resource : resources) {
	                if (resource == null) {
	                    continue;
	                }
	                URL url = resource.getURL();
	                InputStream is = url.openStream();
	                
	                if (is != null) {
	                    Manifest manifest = new Manifest(is);
	                    Attributes mainAttribs = manifest.getMainAttributes();	                    
	                    
	                    if(versionInfo.getVersion() == null && !StringUtils.isEmpty(mainAttribs.getValue("Version"))) {
	                    	versionInfo.setVersion(mainAttribs.getValue("Version"));
	                    	
	                    	
	                    	if (StringUtils.isNotEmpty(mainAttribs.getValue("comerzzia-custom-version"))) {
	                    		versionInfo.setVersionRevision("(" + mainAttribs.getValue("comerzzia-custom-version") +")");
	                    	}
	                    	
	                    	if (StringUtils.isNotEmpty(mainAttribs.getValue("Build-commit"))) {
	                    		versionInfo.setBuildCommit(StringUtils.right(mainAttribs.getValue("Build-commit"), 8));
	                    	} else {
		                    	if (StringUtils.isNotEmpty(mainAttribs.getValue("Revision-SVN"))) {
		                    		versionInfo.setBuildCommit(mainAttribs.getValue("Revision-SVN"));	                    	
		                    	}
	                    	}
	                    	
	                    	if (StringUtils.isNotEmpty(mainAttribs.getValue("Build-number"))) {
	                    		versionInfo.setBuildNumber(mainAttribs.getValue("Build-number"));
	                    	}
	                    }
	                    	                    	                    
	                    String module = mainAttribs.getValue("comerzzia-module");
	                    String moduleVersion = mainAttribs.getValue("comerzzia-module-version");
	                    
	                    if (StringUtils.isNotEmpty(module) && StringUtils.isNotEmpty(moduleVersion)) {
	                    	versionInfo.addModule(module, moduleVersion);	
	                    }
	                    
	                }
	            }
	        }
	        
	        jarsScanned = true;
	    } catch(Exception e){
	    	log.warn("Error obteniendo version de comerzzia " + e.getMessage());
	    }
		
		return versionInfo;
	}
	
	protected static void updateLocalCopyVersion() {
		// Si existe archivo revision, tomar valores para la version local y remota del repositorio
        File revisionFile = new File("./revision");
        
        if (revisionFile.exists()) {
        	try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(revisionFile), StandardCharsets.UTF_8));) {

                String linea = br.readLine();
                
                if (linea != null) {
                	versionInfo.setRemoteRepositoryVersion(linea);
                	versionInfo.setLocalRepositoryVersion(linea);
                	
                	linea = br.readLine();
                	
                	if (linea != null) {
                		versionInfo.setLocalRepositoryVersion(linea);
                	}
                }	                 
            } catch (Exception ignore) {            	
            }
        }
        	
        versionInfo.setLocalCopyUpdated(StringUtils.equals(versionInfo.getLocalRepositoryVersion(), versionInfo.getRemoteRepositoryVersion()));
	}
}
