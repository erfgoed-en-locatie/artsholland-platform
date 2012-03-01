package org.waag.ah.api.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.openrdf.annotations.Bind;
import org.openrdf.annotations.Sparql;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.query.NamedQuery;
import org.openrdf.result.MultipleResultException;
import org.openrdf.result.NoResultException;
import org.openrdf.result.Result;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import org.waag.ah.ObjectConnectionFactory;
import org.waag.ah.api.test.AlibabaTest;
import org.waag.ah.model.Room;
import org.waag.ah.model.RoomImpl;

@Service("restService")
public class RestService implements InitializingBean, DisposableBean {

	@EJB(mappedName = "java:app/datastore/ObjectConnectionFactoryImpl")
	private ObjectConnectionFactory connFactory;
	private ObjectConnection conn;

	@Override
	public void afterPropertiesSet() throws Exception {
		conn = connFactory.getObjectConnection();
	}

	@Override
	public void destroy() throws Exception {
		conn.close();
	}

	private static final String prefixString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";

	private static final String uriPrefix = "http://purl.org/artsholland/1.0/";

	private static final String classQueryString = "SELECT DISTINCT ?property ?hasValue ?isValueOf\n"
			+ "WHERE {\n"
			+ "  { ?c ?property ?hasValue }\n"
			+ "  UNION\n"
			+ "  { ?isValueOf ?property ?c }\n" + "}\n";
	
	private static final String roomsQueryString1 = "SELECT DISTINCT ?instance \n"
			+ "WHERE { ?instance a <http://purl.org/artsholland/1.0/Room> . }";
	
	
	private static final String roomsPagingQueryString = "SELECT DISTINCT ?instance \n"
			+ "WHERE { ?instance a <http://purl.org/artsholland/1.0/Room> . } LIMIT [[limit]] OFFSET [[offset]]";
	
	
	private static final String roomsQueryString2 =	
			"SELECT ?c WHERE { ?c ?p <http://purl.org/artsholland/1.0/Room> . } LIMIT ?x OFFSET ?y";

	// + "LIMIT ?l\n" + "OFFSET ?o";

	public void vis() throws RepositoryException, QueryEvaluationException {
		// retrieve all Documents
		Result<Room> result = conn.getObjects(Room.class);
		while (result.hasNext()) {

		}
	}

	public Room findRoomByLabel(String label) throws RepositoryException,
			NoResultException, MultipleResultException, QueryEvaluationException,
			MalformedQueryException {

		// retrieve a Document by title using a named query
		// ValueFactory vf = conn.getRepository().getValueFactory();

		ObjectQuery query = conn
				.prepareObjectQuery(prefixString
						+ "SELECT DISTINCT ?instance WHERE { ?instance a <http://purl.org/artsholland/1.0/Room> }");

		/*
		 * ObjectQuery query = conn.prepareObjectQuery(
		 * "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		 * "PREFIX ah:<http://purl.org/artsholland/1.0/>\n"+
		 * "SELECT DISTINCT ?room WHERE { \n" +
		 * "?room a <http://purl.org/artsholland/1.0/Room> . \n" +
		 * "?room rdf:Label ?label .}" );
		 */

		// query.setObject("label", label);
		Object vis = query.evaluate().singleResult();
		// Room room = (Room) query.evaluate().singleResult();

		// return room;
		return null;
	}

	public String alleStatements(int page, int count) throws RepositoryException {
		URI uri = new URIImpl("http://purl.org/artsholland/1.0/Production");
		RepositoryResult<Statement> statements = conn.getStatements(null, null,
				uri, false);

		Statement statement = null;
		ArrayList<String> lines = new ArrayList<String>();
		int i = 0;
		while (statements.hasNext() && i < (page + 1) * count) {
			statement = statements.next();
			if (i >= page * count) {

				Resource koek = statement.getSubject();
				lines.add(koek.toString() + "\n");
			}
			i += 1;
		}
		return lines.toString();
	}
	
	public Set<RoomImpl> Tsest(int count, int page) throws MalformedQueryException, RepositoryException, NoResultException, MultipleResultException, QueryEvaluationException {
		
		int limit = count;
		int offset = page * count;		
		String queryString = roomsPagingQueryString.replace("[[limit]]",Integer.toString(limit)).replace("[[offset]]", Integer.toString(offset)) ;
		
		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
				prefixString + queryString);

		//Value koekje = conn.getValueFactory().createLiteral("koekje");
		//query.setBinding("sdda", koekje); 

		Set<RoomImpl> vis = (Set<RoomImpl>) query.evaluate().asSet();
		
		
		vis.clear();
		
		RoomImpl ios = new RoomImpl();
		ios.setLabel("Dikke vis");
		vis.add(ios);
		
		return vis;
		
	}
	
	public Set<RoomImpl> Ppokpokcdkdk(int count, int page) throws MalformedQueryException, RepositoryException, NoResultException, MultipleResultException, QueryEvaluationException {
//		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
//				prefixString + roomsQueryString2);
		Set<RoomImpl> vis = Tsest(count,  page);
		return  vis;
	}
	
	public String query() throws TupleQueryResultHandlerException, QueryEvaluationException, MalformedQueryException, RepositoryException {
		
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
				prefixString + classQueryString);

		// <http://purl.org/artsholland/1.0/[[class]]s/[[id]]>
		URI uri = conn
				.getValueFactory()
				.createURI(
						"http://purl.org/artsholland/1.0/productions/f297a29eb7da60d929a7ab30143083d7");
		// Literal limit = conn.getValueFactory().createLiteral(count);
		// Literal offset = conn.getValueFactory().createLiteral(page * count);

		query.setBinding("c", uri);
		// query.setBinding("l", limit);
		// query.setBinding("o", offset);		


		TupleQueryResult queryResult = query.evaluate();
		OutputStream outputStream = null;
		TupleQueryResultWriter resultsWriter = new SPARQLResultsJSONWriter(
				outputStream);

		if (resultsWriter != null) {
			resultsWriter.startQueryResult(queryResult.getBindingNames());
			try {
				while (queryResult.hasNext()) {
					BindingSet bindingSet = queryResult.next();
					resultsWriter.handleSolution(bindingSet);
				}
			} finally {
				queryResult.close();
			}
			resultsWriter.endQueryResult();
		}
		
		return null;
	}
	
	public Set<RoomImpl> getRooms(int count, int page) throws NoResultException, MultipleResultException, MalformedQueryException, RepositoryException, QueryEvaluationException {
			return Ppokpokcdkdk(count, page);
	}
	
	public String getClass(String classname, String id, int count, int page) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {


			conn.setAutoCommit(false);

			Set<RoomImpl> vis = Tsest(count, page);			
			ArrayList<String> lines = new ArrayList<String>();
			for (RoomImpl room : vis) {
				lines.add(room.getLabel());
			}			
			return lines.toString();		
			

			//return alleStatements(page, count);
			
			// Room roem = findRoomByLabel("Salon");
			//
			// Room room = conn.addDesignation(conn.getObjectFactory().createObject(),
			// Room.class);
			// Room koekje = room.findRoomByLabel("Salon");
			// String lab = koekje.getLabel();

			// Room room =
			// conn.addDesignation("http://purl.org/artsholland/1.0/venues/295aac76-24d2-460f-a9e9-f3c0de838e5e/rooms/salon",
			// Room.class);

			// Room koek = room.findRoomByLabel("Kleine Zaal");
			// String vis = koek.getLabel();

			/*
			 * Result<Room> result = conn.getObjects(Room.class); while
			 * (result.hasNext()) { lines.add(result.next().getLabel() + "\n<br />");
			 * } return lines.toString();
			 */

			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputStream.toString();
	}

}
