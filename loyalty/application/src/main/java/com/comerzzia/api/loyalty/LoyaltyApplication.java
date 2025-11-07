package com.comerzzia.api.loyalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

// Esta configuracion elimina el control de errores por defecto de spring mvc
// que provocaba que por ejemplo un 401 (Unauthorized) se devolvia como un 404. 
// Esto se debe a que una aplicacion web tendria que redirigir a la pagina que 
// controlara esa situacion. Esto no procede en una aplicacion que solo maneja REST
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@ImportResource({"classpath*:comerzzia-*context.xml"})
@SpringBootApplication
public class LoyaltyApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(LoyaltyApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LoyaltyApplication.class);
    }
		
}
