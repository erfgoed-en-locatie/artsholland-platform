package org.waag.ah.spring.configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class JPAConfig {

	@Bean
	public EntityManagerFactory entityManagerFactory() {

		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(datasource());
		entityManagerFactory.setPackagesToScan(new String[] { "org.waag.ah.spring.model" });
		entityManagerFactory.setPersistenceProvider(new HibernatePersistence());
		entityManagerFactory.afterPropertiesSet();
		return entityManagerFactory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory());
		transactionManager.setDataSource(datasource());
		transactionManager.setJpaDialect(new HibernateJpaDialect());
		return transactionManager;
	}

	@Bean
	public DataSource datasource() {
//		EmbeddedDatabaseFactoryBean bean = new EmbeddedDatabaseFactoryBean();
//		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
//		databasePopulator.addScript(new ClassPathResource("org/waag/ah/spring/configuration/schema.sql"));
//		bean.setDatabasePopulator(databasePopulator);
//		bean.afterPropertiesSet();
//		return bean.getObject();
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/artsholland");
		dataSource.setUsername("artsholland");
		dataSource.setPassword("artsholland");
		return dataSource;		
	}

}

//import java.util.Properties;
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.ImportResource;
//import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableTransactionManagement
//@ImportResource("classpath*:*springDataConfig.xml")
////@ComponentScan({ "org.rest.sec.persistence" })
//public class JPAConfig {
//	
//	@Value("${jdbc.driverClassName}") private String driverClassName;
//	@Value("${jdbc.url}") private String url;
//	@Value("${jpa.generateDdl}") boolean jpaGenerateDdl;
//	
//	// Hibernate specific
//	@Value("${hibernate.dialect}") String hibernateDialect;
//	@Value("${hibernate.show_sql}") boolean hibernateShowSql;
//	@Value("${hibernate.hbm2ddl.auto}") String hibernateHbm2ddlAuto;
//	
//	@Bean
//	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
//		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//		factoryBean.setDataSource(restDataSource());
////		factoryBean.setPackagesToScan(new String[] { "org.rest" });
//		
//		final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
//			{
//				// setDatabase(Database.H2); // TODO: is this necessary
//				setDatabasePlatform(hibernateDialect);
//				setShowSql(hibernateShowSql);
//				setGenerateDdl(jpaGenerateDdl);
//			}
//		};
//		factoryBean.setJpaVendorAdapter(vendorAdapter);
//		factoryBean.setJpaProperties(additionlProperties());
//
//		return factoryBean;
//	}
//	
//	@Bean
//	public DataSource restDataSource() {
//		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName(driverClassName);
//		dataSource.setUrl(url);
//		dataSource.setUsername("artsholland");
//		dataSource.setPassword("artsholland");
//		return dataSource;
//	}
//	
//	@Bean
//	public JpaTransactionManager transactionManager() {
//		final JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
//		return transactionManager;
//	}
//	
//	@Bean
//	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
//		return new PersistenceExceptionTranslationPostProcessor();
//	}
//	
//	@SuppressWarnings("serial")
//	final Properties additionlProperties() {
//		return new Properties() {
//			{
//				// use this to inject additional properties in the EntityManager
////				setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
//			}
//		};
//	}
//}