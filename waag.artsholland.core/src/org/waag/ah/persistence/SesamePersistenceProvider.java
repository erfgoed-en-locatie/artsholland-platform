//package org.waag.ah.persistence;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.PersistenceException;
//import javax.persistence.spi.PersistenceProvider;
//import javax.persistence.spi.PersistenceUnitInfo;
//import javax.persistence.spi.ProviderUtil;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.apache.log4j.Logger;
//import org.openrdf.repository.RepositoryException;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.Text;
//import org.xml.sax.SAXException;
//
//public class SesamePersistenceProvider implements PersistenceProvider {
//	private Logger logger = Logger.getLogger(SesamePersistenceProvider.class);
//	private final static String PERSISTENCE_XML = "META-INF/persistence.xml";
//	private static final String PROVIDER_NAME = SesamePersistenceProvider.class.getName();
//	
//	@Override
//	public EntityManagerFactory createContainerEntityManagerFactory(
//			PersistenceUnitInfo info, Map map) {
//		logger.info("Got persistence lookup");
//  		if (!PROVIDER_NAME.equals(info.getPersistenceProviderClassName())) {
//	  		return null;
//  		}
//  		logger.info("Creating ContainerEntityManagerFactory");
//  		try {
//  			Map props = new HashMap();
//  			props.putAll(info.getProperties());
//  			if (map != null) {
//  				props.putAll(map);
//  			}
//  			logger.info("Returning ContainerEntityManagerFactory");
//  			return createSesameEntityManagerFactory(props);
//  		} catch (Exception e) {
//  			throw new PersistenceException(e);
//  		}
//	}
//	
//	@Override
//	public EntityManagerFactory createEntityManagerFactory(String emName, 
//			Map map) {
//  		logger.info("Creating EntityManagerFactory");
//		try {
//			URL unitUrl = findPresistenceResource(emName);
//			Element unit = findPresistenceUnit(emName, unitUrl);
//			NodeList providers = unit.getElementsByTagName("provider");
//			if (providers.getLength() > 0) {
//				Element provider = (Element) providers.item(0);
//				if (!PROVIDER_NAME.equals(getTextContent(provider)))
//					return null;
// 			}
// 			ClassLoader cl = Thread.currentThread().getContextClassLoader();
// 			boolean exclude = getBoolean(unit, "exclude-unlisted-classes");
// 			NodeList jarFiles = unit.getElementsByTagName("jar-file");
// 			List<URL> jarFileUrls = new ArrayList<URL>(jarFiles.getLength());
// 			if (!exclude || jarFiles.getLength() > 0) {
// 				URL base = new URL(unitUrl, "../");
// 				if (!exclude) {
// 					jarFileUrls.add(base);
// 				}
// 				for (int i = 0, n = jarFiles.getLength(); i < n; i++) {
// 					String url = getTextContent(jarFiles.item(i));
// 					jarFileUrls.add(new URL(base, url));
// 				}
// 			}
// 			NodeList names = unit.getElementsByTagName("class");
// 			List<String> types = new ArrayList<String>(names.getLength());
// 			for (int i = 0, n = names.getLength(); i < n; i++) {
// 				String name = getTextContent(names.item(i));
// 				types.add(name);
// 			}
// 			Map props = new HashMap();
// 			props.putAll(getProperties(unit));
// 			if (map != null) {
// 				props.putAll(map);
// 			}
//			return createSesameEntityManagerFactory(props);
//		} catch (Exception e) {
//			throw new PersistenceException(e);
//		}
//	}
//	
//	private SesameEntityManagerFactory createSesameEntityManagerFactory(
//			Map<String, String> props) throws MalformedURLException {
//		try {
//			logger.info("Using dataDir: "+props.get("dataDir"));
//	 		if (props.containsKey("dataDir")) {
//	 			File dataDir = new File(props.get("dataDir"));
//	 			return new SesameEntityManagerFactory(dataDir);
//	 		}
//	 		String id = props.get("repositoryId");
//	 		if (id != null && props.containsKey("serverUrl")) {
//	 			URL server = new URL(props.get("serverUrl"));
//					return new SesameEntityManagerFactory(server, id);
//	 		}
//		} catch (RepositoryException e) {
//			throw new PersistenceException(e.getMessage(), e);
//		}
// 		throw new PersistenceException();
// 	}
//	
//// 	private SesameEntityManagerFactory createSesameManagerFactory(ClassLoader cl,
//// 			Map<String, String> props, List<String> types,
//// 			List<URL> jarFileUrls) throws MalformedURLException {
//// 		ElmoModule module = new ElmoModule(cl);
//// 		
//// 		if (jarFileUrls != null) {
//// 			for (URL url : jarFileUrls) {
//// 				module.addJarFileUrl(url);
//// 			}
//// 		}
//// 		if (types != null) {
//// 			ClassLoader loader = module.getClassLoader();
//// 			for (String type : types) {
//// 				try {
//// 					module.addRole(Class.forName(type, true, loader));
//// 				} catch (ClassNotFoundException e) {
//// 					logger.warn(e.toString(), e);
//// 				}
//// 			}
//// 		}
//// 		if (props.containsKey("resources")) {
//// 			String resources = props.get("resources");
//// 			for (String resource : resources.trim().split("[,\\s]+")) {
//// 				module.addResources(resource);
//// 			}
//// 		}
//// 		return createSesameManagerFactory(props);
//// 	}
//
//	
// 	private boolean getBoolean(Element unit, String tagName) {
//		NodeList excludes = unit.getElementsByTagName(tagName);
//		return excludes.getLength() > 0
//				&& Boolean.valueOf(getTextContent(excludes.item(0)));
//	}
//		 
// 	private String getTextContent(Node node) {
// 		node.normalize();
// 		Text text = (Text) node.getFirstChild();
// 		return text.getData().trim();
// 	}
//		
//	@Override
//	public ProviderUtil getProviderUtil() {
//		return null;
//	}
//	
//	private URL findPresistenceResource(String emName) throws SAXException,
//			IOException, ParserConfigurationException {
//		ClassLoader cl = Thread.currentThread().getContextClassLoader();
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder parser = dbf.newDocumentBuilder();
//		Enumeration<URL> resources = cl.getResources(PERSISTENCE_XML);
//		while (resources.hasMoreElements()) {
//			URL url = resources.nextElement();
//			InputStream stream = url.openStream();
//			try {
//				Document doc = parser.parse(stream);
//				NodeList list = doc.getElementsByTagName("persistence-unit");
//				for (int i = 0, n = list.getLength(); i < n; i++) {
//					Element item = (Element) list.item(i);
//					if (emName.equals(item.getAttribute("name")))
//						return url;
//				}
//			} finally {
//				stream.close();
//			}
//		}
//		throw new IllegalArgumentException(
//				"Cannot find persistence-unit with name: " + emName);
//	 }
//	 
//	 private Element findPresistenceUnit(String emName, URL url)
//	 			throws SAXException, IOException, ParserConfigurationException {
//	 		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//	 		DocumentBuilder parser = dbf.newDocumentBuilder();
//	 		InputStream stream = url.openStream();
//	 		try {
//	 			Document doc = parser.parse(stream);
//	 			NodeList list = doc.getElementsByTagName("persistence-unit");
//			for (int i = 0, n = list.getLength(); i < n; i++) {
//				Element item = (Element) list.item(i);
//				if (emName.equals(item.getAttribute("name")))
//					return item;
//			}
//		} finally {
//			stream.close();
//		}
//		throw new IllegalArgumentException(
//				"Cannot find persistence-unit with name: " + emName);
//	 }
//	 
//	 private Map<String, String> getProperties(Element unit) {
//	 		Map<String, String> properties = new HashMap<String, String>();
//	 		NodeList list = unit.getElementsByTagName("property");
//		for (int i = 0, n = list.getLength(); i < n; i++) {
//			Element item = (Element) list.item(i);
//			properties.put(item.getAttribute("name"), item.getAttribute("value"));
//		}
//		return properties;
//	}	
//}
