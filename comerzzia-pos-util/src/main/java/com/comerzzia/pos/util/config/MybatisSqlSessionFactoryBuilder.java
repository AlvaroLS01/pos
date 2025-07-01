package com.comerzzia.pos.util.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MybatisSqlSessionFactoryBuilder {
	private static Logger log = Logger.getLogger(MybatisSqlSessionFactoryBuilder.class);

	private static SqlSessionFactory springManagedSqlSessionFactory;

	public static SqlSessionFactory getSqlSessionFactory(DataSource dataSource) {
		if (springManagedSqlSessionFactory == null) {
			createFactoryBean(dataSource);
		}
		return springManagedSqlSessionFactory;
	}

	protected static void createFactoryBean(DataSource dataSource) {
		log.debug("\tCreating SqlSessionFactory...");
		
		LogFactory.useLog4JLogging();

		// needs independent configurations for spring and non-spring sqlSessionFactory
		// because the TransactionManager are different
		Configuration configuration = null;
		Configuration springMybatisConfiguration = null;

		Resource mybatisXmlConfiguration = new ClassPathResource("mybatis-config.xml");

		// try load mybatis configuration file
		if (mybatisXmlConfiguration.exists()) {
			try {
				configuration = new XMLConfigBuilder(mybatisXmlConfiguration.getInputStream()).parse();
				springMybatisConfiguration = new XMLConfigBuilder(mybatisXmlConfiguration.getInputStream()).parse();

				// datasource defined in mybatis configuration file
				if (configuration.getEnvironment() != null) {
					dataSource = configuration.getEnvironment().getDataSource();
				} else {
					// environment not defined in configuration file
					configuration
							.setEnvironment(new Environment("development", new JdbcTransactionFactory(), dataSource));
				}

				log.info("\tMybatis Configuration loaded from [" + mybatisXmlConfiguration.getURL().toString() + "]");
			} catch (BuilderException e) {
				log.error("\tError loading mybatis-config.xml: " + e.getMessage(), e);
			} catch (IOException ignore) {
			}
		}

		if (configuration == null || springMybatisConfiguration == null) {
			configuration = new Configuration(new Environment("development", new JdbcTransactionFactory(), dataSource));
			springMybatisConfiguration = new Configuration();
		}

		if (dataSource == null) {
			throw new RuntimeException("Datasource configuration not found");
		}
		
		// apply default configuration
		applyDefaultConfiguration(dataSource, configuration, springMybatisConfiguration);
		
		// for compatibility only, for non-oracle databases
//		if (!StringUtils.equals(configuration.getDatabaseId(), "Oracle")) { 
//		   configuration.addInterceptor(new BlankStringToNullInterceptor());
//		}

		// build sqlSessionFactory for spring managed sqlSessions & transactions
		SqlSessionFactoryBean springSqlSessionFactoryBean = new SqlSessionFactoryBean();
		springSqlSessionFactoryBean.setConfiguration(springMybatisConfiguration);
		springSqlSessionFactoryBean.setDataSource(dataSource);
		// force transaction factory to spring, if loaded from configuration file
		springSqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());

		try {
			springManagedSqlSessionFactory = springSqlSessionFactoryBean.getObject();
		} catch (Exception e) {
			throw new RuntimeException(
					"createSpringFactoryBean() Error creando SqlSessionFactoryBean: " + e.getMessage());
		}
	}

	protected static void applyDefaultConfiguration(DataSource dataSource, Configuration... configurations) {
		for (Configuration configuration : configurations) {
			// mybatis required type handles and interceptors
			configuration.setMapUnderscoreToCamelCase(true);		
			configuration.getTypeHandlerRegistry().register(BooleanTypeHandler.class);
//			configuration.getTypeHandlerRegistry().register(BooleanStringTypeHandler.class);
		}
	}
}
