SPARQL endpoint
===============

You can use SPARQL, a query language for RDF data, to query the Linked Open Data from the Arts Holland database.

More information about SPARQL is found in the [W3C SPARQL specification](http://www.w3.org/TR/rdf-sparql-query/).

Endpoint
--------

The SPARQL endpoint is `http://api.artsholland.com/sparql`.

RDF vocabulary
--------------

The Arts Holland RDF vocabulary can be found on
[http://api.artsholland.com/vocabulary/1.0/artsholland.rdf](http://api.artsholland.com/vocabulary/1.0/artsholland.rdf). Also, a basic description of the Arts Holland data model can be found in the [REST API documentation](restapi).

Authentication
--------------

A valid Arts Holland API key is needed for all requests to the SPARQL endpoint. The API key is appended to the requested URL, i.e. `api_key=9cbce178ed121b61a0797500d62cd440`. The latter is also the API key that can be used for test purposes.

SPARQL requests
---------------
SPARQL queries can be submitted to the database server by doing a `GET` or `POST` request to `/sparql` and URL encoding the SPARQL query in the `query` parameter.

Make sure to set the `Content-Type` header to `application/x-www-form-urlencoded` and the `Accept` header to a media type from the table in the next section.

Response formats
----------------
You can select one of the response data formats by setting the `Accept` header to the appropriate media type, or by appending an file extension to the request. 

<table>
	<tr>
		<th>Response</th>
		<th>Extension</th>
		<th>Media type</th>	
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TR/rdf-sparql-XMLres/">SPARQL Query Results XML Format</a></td>
		<td>.rdf</td>
		<td><code>application/sparql-results+xml</code></td>
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TR/rdf-sparql-XMLres/">SPARQL Query Results JSON Format</a></td>
		<td>.json</td>
		<td><code>application/sparql-results+json</code></td>
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TR/2012/WD-sparql11-results-csv-tsv-20120501/">SPARQL Query Results CSV</a></td>
		<td>.csv</td>
		<td><code>text/csv</code></td>
	</tr>
	<tr>
		<td>SPARQL browser</td>
		<td></td>
		<td><code>text/html</code></td>
	</tr>
</table>

If you want to view the result in your browser, you can overwrite the response media type to `text/html` by specifying the request parameter `plaintext=true`.

SPARQL browser
------------
Arts Holland also provides a [web-based SPARQL browser](http://api.artsholland.com/sparql?api_key=9cbce178ed121b61a0797500d62cd440). You can use this browser to test SPARQL queries or just to get an impression about the data available in the Arts Holland database.

RDF namespaces
--------------

The SPARQL browser automatically appends a set of default RDF namespace prefixes to all submitted SPARQL queries, something that you have to do yourself when doing direct `GET` or `POST` requests. Alternatively, you can rewrite your queries and use full URIs instead of namespaced ones.

For example, see the two following queries. With namespaces:

	PREFIX ah: <http://purl.org/artsholland/1.0/>
	PREFIX dc: <http://purl.org/dc/terms/>
	SELECT ?venue ?title {	
		?venue a ah:Venue ;
			dc:title ?title .
	} LIMIT 10
		
Without namespaces:

	SELECT ?venue ?title {	
		?venue a <http://purl.org/artsholland/1.0/Venue> ;
			<http://purl.org/dc/terms/title> ?title .
	} LIMIT 10

Example request
---------------
The following query, for example, retrieves the URIs and English titles of 25 venues in Amsterdam from the Arts Holland database.

	SELECT DISTINCT ?venue ?title
	WHERE { 
		?venue a ah:Venue .
		?venue dc:title ?title .
		?venue ah:locationAddress ?address .
		?address vcard:locality "Amsterdam" . 
		FILTER(langMatches(lang(?title), "en"))
	} ORDER BY ?venue
	LIMIT 25

You can [paste this query in the Arts Holland SPARQL browser](http://api.artsholland.com/sparql?query=SELECT+DISTINCT+%3Fvenue+%3Ftitle%0D%0AWHERE+%7B+%0D%0A%09%3Fvenue+a+ah%3AVenue+.%0D%0A%09%3Fvenue+dc%3Atitle+%3Ftitle+.%0D%0A%09%3Fvenue+ah%3AlocationAddress+%3Faddress+.%0D%0A%09%3Faddress+vcard%3Alocality+%22Amsterdam%22+.+%0D%0A%09FILTER%28langMatches%28lang%28%3Ftitle%29%2C+%22en%22%29%29%0D%0A%7D+ORDER+BY+%3Fvenue%0D%0ALIMIT+25&apiKey=9cbce178ed121b61a0797500d62cd440&selectoutput=browse), or directly do a `GET` or `POST` request to the SPARQL endpoint. In the latter case, don't forget to add the following namespace prefixes:

	PREFIX ah: <http://purl.org/artsholland/1.0/>
	PREFIX dc: <http://purl.org/dc/terms/>
	PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>

Full text search
----------------

Arts Holland provides two ways of full text searching its database:

1. You can use the direct [full text search implementation](http://sourceforge.net/apps/mediawiki/bigdata/index.php?title=FullTextSearch) of the [Bigdata](http://www.systap.com/bigdata.htm) triple store used by Arts Holland. This works on all text triples in the database. However, due to a [bug in Bigdata](http://sourceforge.net/apps/trac/bigdata/ticket/581), it does not work in nested queries.

2. If you need full text search in nested queries, you can use the [full text search functionality](https://dev.opensahara.com/projects/useekm/wiki/IndexingSail#Full-Text-Search) of the [uSeekM library](https://dev.opensahara.com/projects/useekm). uSeekM text search currently only indexes `dc:description` values.

Geo-spatial search
------------------

The uSeekM library is also used to add geo-spatial computations and indexing its database. This functionality can be used in all SPARQL queries submitted to the Arts Holland SPARQL endpoint. More documentation can be found in the [uSeekM wiki](https://dev.opensahara.com/projects/useekm/wiki/IndexingSail#GeoSPARQL).