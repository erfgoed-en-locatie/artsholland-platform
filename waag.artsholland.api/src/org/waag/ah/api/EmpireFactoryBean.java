package org.waag.ah.api;
//package org.waag.ah.api;
//
//import java.util.Map;
//
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.spi.PersistenceProvider;
//
//import org.springframework.beans.factory.FactoryBean;
//
//import com.clarkparsia.empire.Empire;
//import com.clarkparsia.empire.sesametwo.OpenRdfEmpireModule;
//
//public class EmpireFactoryBean implements FactoryBean<EntityManagerFactory> {
//	private Map<String, String> config; 
//	public Map<String, String> getConfig() {
//		return config;
//	}
//	public void setConfig(Map<String, String> config) {
//		this.config = config;
//	}
//	public EntityManagerFactory getObject() throws Exception {
//		Empire.init(new OpenRdfEmpireModule());
//		PersistenceProvider aProvider = Empire.get().persistenceProvider();
//		return aProvider.createEntityManagerFactory("", config); //.createEntityManager();
//	}
//	public Class<?> getObjectType() {
//		return EntityManagerFactory.class;
//	}
//	public boolean isSingleton() {
//		return true;
//	}
//}
