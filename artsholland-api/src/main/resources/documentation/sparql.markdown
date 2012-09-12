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
SPARQL queries can be submitted to the database server by doing a [HTTP POST request](http://en.wikipedia.org/wiki/POST_%28HTTP%29) to `/sparql` and URL encoding the SPARQL query in the query parameter:

http://dsldkjhn

Also 

Response formats
----------------
You can select one of the response data formats by setting the accept header to the appropriate media type, or by appending an file extension to the request. 

<table>
	<tr>
		<th>Response</th>
		<th>extension</th>
		<th>media type</th>	
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TR/rdf-sparql-XMLres/">SPARQL Query Results XML Format</a></td>
		<td>.rdf</td>
		<td>application/sparql-results+xml</td>
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TR/rdf-sparql-XMLres/">SPARQL Query Results JSON Format</a></td>
		<td>.json</td>
		<td>application/sparql-results+json</td>
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TR/2012/WD-sparql11-results-csv-tsv-20120501/">SPARQL Query Results CSV</a></td>
		<td>.csv</td>
		<td>text/csv</td>
	</tr>
</table>

Data browser
------------
Arts Holland also provides a [web-based SPARQL browser](http://api.artsholland.com/sparql?api_key=9cbce178ed121b61a0797500d62cd440). You can use this browser to test SPARQL queries or just to get an impression about the data available in the Arts Holland database.

RDF namespaces
--------------
PREFIX ah: <http://purl.org/artsholland/1.0/>

<http://www.w3.org/2006/vcard/ns#locality> instead of vcard:locality
PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>


Be careful: the data browser automatically appends the default namespace prefixes to all submitted queries. When your application uses GET and POST requests to directly query the SPARQL endpoint, you have to either append those prefixes yourself, or you have to 

not use namespaces but full URIs.



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


The Language tag
http://www.w3.org/TR/rdf-sparql-query/#func-langMatches

You can do a POST query to the SPARQL endpoint, or paste the query in the SPARQL browser.


http://api.artsholland.com/sparql?query=SELECT+DISTINCT+%3Fvenue+%3Ftitle%0D%0AWHERE+%7B+%0D%0A%09%3Fvenue+a+ah%3AVenue+.%0D%0A%09%3Fvenue+dc%3Atitle+%3Ftitle+.%0D%0A%09%3Fvenue+ah%3AlocationAddress+%3Faddress+.%0D%0A%09%3Faddress+vcard%3Alocality+%22Amsterdam%22+.+%0D%0A%09FILTER%28langMatches%28lang%28%3Ftitle%29%2C+%22en%22%29%29%0D%0A%7D+ORDER+BY+%3Fvenue%0D%0ALIMIT+25&apiKey=85715d4734ee8a22571c6b69a789d8ac&selectoutput=browse

example with extension
plaintext=true

Full text search
----------------

Arts Holland uses the uSeekM library to add geo-spatial computations and indexing as well as free text search to its semantic database. This functionality can be used in all SPARQL queries submitted to the Arts Holland SPARQL endpoint. More documentation can be found in the uSeekM wiki:

Geo-spatial functions
Bigdata text search (bug)

Geo-spatial search
------------------

lknjkjh