package org.waag.ah.enricher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.tinkerpop.AbstractEnricher;
import org.waag.rdf.sesame.EnricherConfig;
import org.waag.rdf.sesame.NamedGraph;

public class AddressCleanup extends AbstractEnricher {
	final static Logger logger = LoggerFactory.getLogger(AddressCleanup.class);

	static String[] PREDICATES = {
			"http://www.w3.org/2006/vcard/ns#street-address",
			"http://www.w3.org/2006/vcard/ns#postal-code",
			"http://www.w3.org/2006/vcard/ns#locality" };

	public AddressCleanup(EnricherConfig config) {
		super(config);
	}

	@Override
	public List<Statement> enrich(EnricherConfig config, NamedGraph graph) {
		List<Statement> statements = new ArrayList<Statement>();
		ValueFactory vf = graph.getValueFactory();

		RepositoryConnection connection = config.getConnection();

		for (String p : PREDICATES) {
			URI predicate = vf.createURI(p);
			Value subject = graph.getGraphUri();

			List<Value> objects = new ArrayList<Value>();
			objects.addAll((Collection<Value>) GraphUtil.getObjects(graph, null,
					predicate));
			if (objects.size() > 0) {
				String message = "Keeping \"";

				int i = getBestTriple(p, objects);
				Value objectKept = objects.get(i);
				message += objectKept.toString() + "\", removing ";
				objects.remove(objectKept);

				for (Value object : objects) {
					try {
						message += "\"" + object.toString() + "\", ";
						connection.remove((Resource) subject, predicate, object);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				logger.debug(message);
			}
		}
		return statements;
	}

	private int getBestTriple(String predicate, List<Value> objects) {
		/*class ValueComparator implements Comparator<Integer> {
			Map<Integer, Double> base;

			public ValueComparator(Map<Integer, Double> base) {
				this.base = base;
			}
			public int compare(Integer a, Integer b) {
				if (base.get(a) >= base.get(b)) {
					return -1;
				} else {
					return 1;
				} // returning 0 would merge keys
			}
		}
		if (predicate.endsWith("street-address")) {
			// vcard:street-address
			HashMap<Integer, Double> map = new HashMap<Integer, Double>();
			ValueComparator bvc = new ValueComparator(map);
			TreeMap<Integer, Double> sortedMap = new TreeMap<Integer, Double>(bvc);

			for (int i = 0; i < objects.size(); i++) {

			}
			map.put(1, 99.5);
			map.put(2, 67.4);
			map.put(3, 67.4);
			map.put(4, 67.3);

			sortedMap.putAll(map);
			return 0;
		} else {
			// vcard:postal-code and vcard:locality
			// Remove random item!
			Random rand = new Random();
			return rand.nextInt(objects.size());
		}*/
		Random rand = new Random();
		return rand.nextInt(objects.size());
	}

}
