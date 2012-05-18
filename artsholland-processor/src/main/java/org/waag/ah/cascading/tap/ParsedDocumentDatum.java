package org.waag.ah.cascading.tap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import bixo.datum.ParsedDatum;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

@SuppressWarnings("serial")
public class ParsedDocumentDatum extends ParsedDatum {

	public static final String URL_FN = fieldName(ParsedDatum.class, "url");
    public static final String PARSED_TEXT_FN = fieldName(ParsedDatum.class, "parsedText");
    public static final String PARSED_META_FN = fieldName(ParsedDatum.class, "parsedMeta");

	public static final Fields FIELDS = new Fields(URL_FN, PARSED_TEXT_FN, 
            PARSED_META_FN).append(getSuperFields(ParsedDatum.class));
    
	public ParsedDocumentDatum() {
//        super(FIELDS);
    }
    
	public ParsedDocumentDatum(TupleEntry tupleEntry) {
        super(tupleEntry);
        validateFields(tupleEntry, FIELDS);
    }
    
	public ParsedDocumentDatum(String url, String parsedText,
			Map<String, String> parsedMeta) {
		setUrl(url);
		setParsedText(parsedText);
		setParsedMeta(parsedMeta);
	}
	
    public String getUrl() {
        return _tupleEntry.getString(URL_FN);
    }

    public void setUrl(String url) {
        _tupleEntry.set(URL_FN, url);
    }

    public String getParsedText() {
        return _tupleEntry.getString(PARSED_TEXT_FN);
    }

    public void setParsedText(String parsedText) {
        _tupleEntry.set(PARSED_TEXT_FN, parsedText);
    }

    public Map<String, String> getParsedMeta() {
        return convertTupleToMap((Tuple)_tupleEntry.get(PARSED_META_FN));
    }

    public void setParsedMeta(Map<String, String> parsedMeta) {
        _tupleEntry.set(PARSED_META_FN, convertMapToTuple(parsedMeta));
    }	
    
    private Tuple convertMapToTuple(Map<String, String> map) {
        Tuple result = new Tuple();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                result.add(entry.getKey());
                result.add(entry.getValue());
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, String> convertTupleToMap(Tuple tuple) {
        Map<String, String> result = new HashMap<String, String>();
        Iterator<Comparable<?>> iter = tuple.iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String value = (String)iter.next();
            result.put(key, value);
        }
        
        return result;
    }    
}
