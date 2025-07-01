package com.comerzzia.pos;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.comerzzia.pos.core.gui.POSApplication;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(lazyInit = true)
@ImportResource({"classpath*:comerzzia-*context.xml"})
@EnableCaching
@EnableScheduling
@EnableFeignClients("com.comerzzia.api")
public class ComerzziaPOSApplication {
	public static void main(String[] args) {
		boolean alreadyRunning=true;
		try {
			JUnique.acquireLock("comerzzia/jpos/4.9");
			alreadyRunning = false;
		} catch (AlreadyLockedException e) {
		}
		
		if (alreadyRunning) {
			System.out.println("comerzzia POS already running. Aborting.");
			System.exit(POSApplication.EXIT_CODE_ALREADY_RUNNING);
		}
		
		if(StringUtils.isBlank(System.getProperties().getProperty("javafx.preloader"))) {
			System.getProperties().setProperty("javafx.preloader", "com.comerzzia.pos.core.gui.POSPreloader");
		}
		POSApplication.source = ComerzziaPOSApplication.class;
		POSApplication.args = args;
		Application.launch(POSApplication.class, args);		
	}
}
