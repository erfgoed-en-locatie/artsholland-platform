package org.waag.ah.api.test;

import org.openrdf.annotations.Bind;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.annotations.Sparql;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.repository.query.NamedQuery;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.result.Result;
import org.openrdf.sail.memory.MemoryStore;

public class AlibabaTest {
	private static final String FOAF = "urn:test:foaf:";
	private static final String PREFIX = "PREFIX foaf:<" + FOAF + ">\n";
	private static final String DEFAULT_IMAGE_URI = "about:blank";

	@Iri(Document.NS + "Document")
	public static class Document {
		public static final String NS = "http://meta.leighnet.ca/rdf/2009/gs#";

		@Iri(NS + "title")
		String title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		// retrieve a Document by title using a named query
		@Sparql("PREFIX gs:<http://meta.leighnet.ca/rdf/2009/gs#>\n"
				+ "SELECT ?doc WHERE {?doc gs:title $title .}")
		public Document findDocumentByTitle(@Bind("title") String title) {
			return null;
		}
	}

	@Iri(FOAF + "Person")
	public interface Person {
		@Iri(FOAF + "depiction")
		Image getDepiction();

		@Iri(FOAF + "depiction")
		void setDepiction(Image depiction);
	}

	@Iri(FOAF + "Image")
	public interface Image {

		@Sparql(PREFIX + "SELECT ?person { ?person foaf:depiction $this }")
		Person getDepicts();

		@Sparql(PREFIX + "DELETE { ?p foaf:depiction $this }\n"
				+ "INSERT { $person foaf:depiction $this } WHERE {} ")
		void setDepicts(@Bind("person") Person person);
	}

	public abstract static class PersonSupport implements Person, RDFObject {

		@ParameterTypes({})
		public Image getDepiction(ObjectMessage msg) throws RepositoryException {
			Image depiction = (Image) msg.proceed();
			if (depiction == null) {
				return (Image) getObjectConnection().getObject(
						DEFAULT_IMAGE_URI);
			}
			return depiction;
		}
	}

	private ObjectRepository repository;
	private ObjectConnection con;

	public void setUp() throws Exception {
		ObjectRepositoryFactory orf = new ObjectRepositoryFactory();
		ObjectRepositoryConfig config = orf.getConfig();
		config.addConcept(Document.class);
		config.addConcept(Person.class);
		config.addConcept(Image.class);
		config.addBehaviour(PersonSupport.class);
		MemoryStore store = new MemoryStore();
		repository = orf.createRepository(config, new SailRepository(store));
		repository.initialize();
		con = repository.getConnection();
	}

	public void tearDown() throws Exception {
		con.close();
		repository.shutDown();
	}

	public boolean testCreateDocument() throws Exception {
		boolean result = true;
		
		// create a Document
		Document doc = new Document();
		doc.setTitle("Getting Started");

		// add a Document to the repository
		ValueFactory vf = con.getValueFactory();
		URI id = vf
				.createURI("http://meta.leighnet.ca/data/2009/getting-started");
		con.addObject(id, doc);

		// retrieve a Document by id
		doc = con.getObject(Document.class, id);
		
		result &= "Getting Started".equals(doc.getTitle());
		
		return result;

	}

	public boolean testRemoveDocument() throws Exception {
		boolean result = true;
		
		// create a Document
		Document doc = new Document();
		doc.setTitle("Getting Started");

		// add a Document to the repository
		ValueFactory vf = con.getValueFactory();
		URI id = vf
				.createURI("http://meta.leighnet.ca/data/2009/getting-started");
		con.addObject(id, doc);

		// retrieve a Document by id
		doc = con.getObject(Document.class, id);
		
		
		result &= "Getting Started".equals(doc.getTitle());

		// remove a Document from the repository
		doc = con.getObject(Document.class, id);
		doc.setTitle(null);
		con.removeDesignation(doc, Document.class);

		return result;
	}

	public boolean testAllDocuments() throws Exception {
		boolean result = true;
		
		// create a Document
		Document doc = new Document();
		doc.setTitle("Getting Started");

		// add a Document to the repository
		ValueFactory vf = con.getValueFactory();
		URI id = vf
				.createURI("http://meta.leighnet.ca/data/2009/getting-started");
		con.addObject(id, doc);

		// retrieve a Document by id
		doc = con.getObject(Document.class, id);
		result &= "Getting Started".equals(doc.getTitle());

		// retrieve all Documents
		Result<Document> docResult = con.getObjects(Document.class);
		
		while (docResult.hasNext()) {
			result &= "Getting Started".equals(docResult.next().getTitle());			
		}
		
		return result;
	}

	public boolean testNamedQuery() throws Exception {
		boolean result = true;
		
		// create a Document
		Document doc = new Document();
		doc.setTitle("Getting Started");

		// add a Document to the repository
		ValueFactory vf = con.getValueFactory();
		URI id = vf
				.createURI("http://meta.leighnet.ca/data/2009/getting-started");
		con.addObject(id, doc);

		// retrieve a Document by id
		doc = con.getObject(Document.class, id);
		result &= "Getting Started".equals(doc.getTitle());

		// retrieve a Document by title using a named query
		URI myQueryID = vf
				.createURI("http://meta.leighnet.ca/rdf/2011/my-query");
		NamedQuery named = con.getRepository().createNamedQuery(
				myQueryID,
				"PREFIX gs:<http://meta.leighnet.ca/rdf/2009/gs#>\n"
						+ "SELECT ?doc WHERE {?doc gs:title ?title}");

		ObjectQuery query = con.prepareObjectQuery(named.getQueryString());
		query.setObject("title", "Getting Started");
		doc = query.evaluate(Document.class).singleResult();
		result &= "Getting Started".equals(doc.getTitle());
		
		return result;
	}

	public boolean testDynamicQuery() throws Exception {
		boolean result = true;
		
		// create a Document
		Document doc = new Document();
		doc.setTitle("Getting Started");

		// add a Document to the repository
		ValueFactory vf = con.getValueFactory();
		URI id = vf
				.createURI("http://meta.leighnet.ca/data/2009/getting-started");
		con.addObject(id, doc);

		// retrieve a Document by id
		doc = con.getObject(Document.class, id);
		result &= "Getting Started".equals(doc.getTitle());

		// retrieve a Document by title using a dynamic query
		ObjectQuery query = con
				.prepareObjectQuery("PREFIX gs:<http://meta.leighnet.ca/rdf/2009/gs#>\n"
						+ "SELECT ?doc WHERE {?doc gs:title ?title}");
		query.setObject("title", "Getting Started");
		doc = query.evaluate(Document.class).singleResult();
		result &= "Getting Started".equals(doc.getTitle());
		
		return result;
	}

	public boolean testMethodQuery() throws Exception {
		boolean result = true;
		
		// create a Document
		Document doc = new Document();
		doc.setTitle("Getting Started");

		// add a Document to the repository
		ValueFactory vf = con.getValueFactory();
		URI id = vf
				.createURI("http://meta.leighnet.ca/data/2009/getting-started");
		con.addObject(id, doc);

		// retrieve a Document by id
		doc = con.getObject(Document.class, id);
		result &= "Getting Started".equals(doc.getTitle());

		doc = doc.findDocumentByTitle("Getting Started");
		result &= "Getting Started".equals(doc.getTitle());
		
		return result;
	}

	public boolean testProperty() throws Exception {
		boolean result = true;
		
		Image nil = con.addDesignation(con.getObject(DEFAULT_IMAGE_URI),
				Image.class);
		Person me = con.addDesignation(con.getObject("urn:test:me"),
				Person.class);
		Person myself = con.addDesignation(con.getObject("urn:test:myself"),
				Person.class);
		Image image = con.addDesignation(con.getObject("urn:test:image"),
				Image.class);
		me.setDepiction(image);
		me = con.getObject(Person.class, "urn:test:me");
		myself = con.getObject(Person.class, "urn:test:myself");
		image = con.getObject(Image.class, "urn:test:image");
		
		result &= image.equals(me.getDepiction());
		result &= nil.equals(myself.getDepiction());
		result &= me.equals(image.getDepicts());
		
		me.setDepiction(null);
		myself.setDepiction(image);
		me = con.getObject(Person.class, "urn:test:me");
		myself = con.getObject(Person.class, "urn:test:myself");
		image = con.getObject(Image.class, "urn:test:image");
		
		result &= image.equals(myself.getDepiction());
		result &= nil.equals(me.getDepiction());
		result &= myself.equals(image.getDepicts());
		
		return result;
	}

	public boolean testInverse() throws Exception {
		boolean result = true;
		
		Image nil = con.addDesignation(con.getObject(DEFAULT_IMAGE_URI),
				Image.class);
		Person me = con.addDesignation(con.getObject("urn:test:me"),
				Person.class);
		Person myself = con.addDesignation(con.getObject("urn:test:myself"),
				Person.class);
		Image image = con.addDesignation(con.getObject("urn:test:image"),
				Image.class);
		image.setDepicts(me);
		me = con.getObject(Person.class, "urn:test:me");
		myself = con.getObject(Person.class, "urn:test:myself");
		image = con.getObject(Image.class, "urn:test:image");
		
		result &= image.equals(me.getDepiction());
		result &= nil.equals(myself.getDepiction());
		result &= me.equals(image.getDepicts());
		
		image.setDepicts(myself);
		me = con.getObject(Person.class, "urn:test:me");
		myself = con.getObject(Person.class, "urn:test:myself");
		image = con.getObject(Image.class, "urn:test:image");
		
		result &= image.equals(myself.getDepiction());
		result &= nil.equals(me.getDepiction());
		result &= myself.equals(image.getDepicts());
		
		return result;
	}
}
