package org.waag.ah.importer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.DBObject;

public class ImportResult implements DBObject {
	private final Map<String, Object> data = new HashMap<String, Object>();
//	private final Set<String> fields = new HashSet<String>(Arrays.asList(
//			"_id",
//			"_transientFields",
//			"timestamp",
//			"jobId",
//			"strategy",
//			"source"));

//	public ImportResult(ImportJob job) {
//		this.put("jobKey", job.context.getJobDetail().getKey().toString());
//		result.put("jobId", context.getFireInstanceId());
//		result.put("timestamp", context.getFireTime().getTime());
//		result.put("strategy", this.strategy.toString());
//	}

	@Override
	public void markAsPartialObject() {}

	@Override
	public boolean isPartialObject() {
		return false;
	}
	
	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	public Object put(String key, Object value) {
//		if (!containsField(key)) {
//			throw new IllegalArgumentException();
//		}
		data.put(key, value);
		return value;
	}

	@Override
	public void putAll(BSONObject o) {
		putAll(o.toMap());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void putAll(Map m) {
	    Iterator<?> it = m.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<String, Object> pairs = (Map.Entry) it.next();
	    	put(pairs.getKey(), pairs.getValue());
	    }
	}

	@Override
	public Object get(String key) {
//		if (!containsField(key)) {
//			throw new IllegalArgumentException("Field '"+key+"' not defined");
//		}
		return data.get(key);
	}

	@Override
	public Map<String, Object> toMap() {
		return data;
	}

	@Override
	public Object removeField(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(String s) {
		return containsField(s);
	}

	@Override
	public boolean containsField(String s) {
		return data.containsKey(s);
	}

	@Override
	public final Set<String> keySet() {
		return data.keySet();
	}	
}
