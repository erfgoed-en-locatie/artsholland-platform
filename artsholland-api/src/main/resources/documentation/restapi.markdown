REST API
========

A REST API is available to access the most important parts of the Arts Holland semantic Open Linked Database.

Accessing this data using the [SPARQL endpoint](http://api.artsholland.com/sparql?api_key=9cbce178ed121b61a0797500d62cd440) provides the most flexibility, but its [query language](http://en.wikipedia.org/wiki/SPARQL) and response formats might sometimes be too complex and overwhelming. For simplicity's sake, Arts Holland also provides a REST API, described below.

Endpoint
--------

The REST API endpoint is `http://api.artsholland.com/rest`.

Authentication
--------------

A valid Arts Holland API key is needed for all REST API requests. The API key is appended to the requested URI, i.e. `api_key=9cbce178ed121b61a0797500d62cd440`. The latter is also the API key that can be used for test purposes.

Data model
----------

more data, REST API only 


The objects returned by the REST API are described below in short.

Arts Holland namespace
----------------------

dlskadjldkjsdlakjdl

### Main elements ###

<table>
	<tr>
		<th>Element</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>Production</td>
		<td>A play, a movie, a concert, an exhibition, a lecture, etc. A production can be performed multiple times, and so can be be hosted by multiple events and venues.</td>
	</tr>
	<tr>
		<td>Event</td>
		<td>A instance of a production at a specific location and time.</td>
	</tr>
	<tr>
		<td>Venue</td>
		<td>A physical location where events take place.</td>
	</tr>
</table>

Events, venues and productions are identified by a [CIDN number](http://wiki.uitburo.nl/index.php/Nationaal_CIDN_register) managed by the Nederlands Uitburo.

### Other elements ###

<table>
	<tr>
		<th>Element</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>Room</td>
		<td>Child element of a venue, in which events can take place. A venue can have multiple rooms. Events can be held in one or more rooms of a specific venue.</td>
	</tr>
	<tr>
		<td>Address</td>
		<td>Child element of a venue, holds address information.</td>
	</tr>
	<tr>
		<td>Attachment</td>
		<td>Child element of both venues and events. An attachment holds information about images, movies or documents linked to the venue or event.</td>
	</tr>
	<tr>
		<td>Offering</td>
		<td>Child element of an event. Information about tickets; an event can have multiple offers.
		</td>
	</tr>
	<tr>
		<td>PriceSpecification</td>
		<td>Child element of an offer. Information about price and currency.</td>
	</tr>
	<tr>
		<td>Genre</td>
		<td>Productions are categorized by genre. Examples are dance, documentary, exhibition and musical.
		</td>
	</tr>
	<tr>
		<td>VenueType</td>
		<td>Venues are categorized by type. Museums, cinemas and theaters, for example, all have a different VenueType.</td>
	</tr>
</table>

URI structure
-------------
### Object lists

<table>
	<tr>
		<th>URI</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>/rest/event</code></td>
		<td>All events</td>
	</tr>
	<tr>
		<td><code>/rest/venue</code></td>
		<td>All venues</td>
	</tr>
	<tr>
		<td><code>/rest/production</code></td>
		<td>All productions</td>
	</tr>
	<tr>
		<td><code>/rest/genre</code></td>
		<td>All genres</td>
	</tr>
	<tr>
		<td><code>/rest/venuetype</code></td>
		<td>All venue types</td>
	</tr>
</table>

### Single objects

<table>
	<tr>
		<th>URI</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>/rest/event/{cidn}</code></td>
		<td>Event with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/venue/{cidn}</code></td>
		<td>Venue with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/production/{cidn}</code></td>
		<td>Production with CIDN = <code>{cidn}</code></td>
	</tr>
</table>

### Child elements and relations

<table>
	<tr>
		<th>URI</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>/rest/event/{cidn}/venue</code></td>		<td>All venues in which event with CIDN = {cidn} takes place</td>
	</tr>
	<tr>
		<td><code>/rest/event/{cidn}/production</code></td>
		<td>All productions associated with event with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/event/{cidn}/room	</code></td>
		<td>All rooms in which event with CIDN = {cidn} takes place</td>
	</tr>
	<tr>
		<td><code>/rest/event/{cidn}/attachment</code></td>
		<td>All attachments associated with event with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/event/{cidn}/offering</code></td>
		<td>All offers available for event with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/event/{cidn}/offering/{name}/price</code></td>
		<td>Price specification of offer with name = {name}</td>
	</tr>
	<tr>
		<td><code>/rest/venue/{cidn}/event</code></td>
		<td>All events which take place in venue with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/venue/{cidn}/production</code></td>
		<td>All productions which take place in venue with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/venue/{cidn}/room	</code></td>
		<td>All rooms in venue with with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/venue/{cidn}/attachment</code></td>
		<td>All attachments associated with venue with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
<td><code>/rest/venue/{cidn}/address</code></td>
<td>All addresses associated with venue with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/production/{cidn}/event</code></td>
		<td>All events associated with production with CIDN = <code>{cidn}</code></td>
	</tr>
	<tr>
		<td><code>/rest/production/{cidn}/venue</code></td>
		<td>All venues in which production with CIDN = {cidn} takes place</td>
	</tr>
	<tr>
</table>

Filters
-------

<table>
	<tr>
		<th>Parameter</th>
		<th>On</th>
		<th>Description</th>
		<th>Range</th>
	</tr>
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
	<tr>
		<td></td>
		<td></td>
	</tr>	
</table>

URI	On	Description	Example

search	Event, Production, Venue	Free text search on description field (RDF dc:description property)	search=theater

genre	Production	Filters production by genre	genre=genreLiterature

type	Venue	Filters venues by venue type	type=venueTypeCinema

nearby	Event, Venue	Finds events or venues nearby a specific geographic location. The filter 
accepts WKT points: POINT(longitude latitude) (in the WKT specification, x is longitude and y is latitude). A second argument specifies the dinstance in meters.	nearby=POINT(5.807 53.201)&distance=2500

locality	Event, Venue	Filters events or venues on city or town (RDF vcard:locality property). The locality filter is case-insensitive.	locality=utrecht

before, after	Event	Filters events on start date and time, only returns events which start 
before or after specified date and time. The filters accept the ISO 8601 date and time format.	before=2012-04-06

after=2012-07-02

min_price, max_price	Event	Finds events which have a ticket with a price (in any currency) of at least min_price or at most max_price.	min_price=10
max_price=15

Filters currently only work on first level main element API requests (e.g. `/rest/event`, `/rest/venue` and `/rest/production`).

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
		<td><a href="http://www.w3.org/TR/REC-rdf-syntax/">RDF/XML</a></td>
		<td>.rdf</td>
		<td>application/rdf+xml</td>
	</tr>
	<tr>
		<td><a href="http://www.json.org/">JSON</a></td>
		<td>.json</td>
		<td>application/json</td>
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TR/rdf-sparql-XMLres/">SPARQL Query Results JSON Format</a></td>
		<td>.jsonp</td>
		<td>application/jsonp</td>
	</tr>
	<tr>
		<td><a href="http://www.w3.org/TeamSubmission/turtle/">Turtle - Terse RDF Triple Language</a></td>
		<td>.turtle</td>
		<td>text/turtle</td>
	</tr>
</table>

Most browsers will download the response an content-type


	private boolean plainText = false;
	private String jsonpCallback;


Localization
------------

Text strings in the Arts Holland database are often localized, and available in more than one language (mostly Dutch and English). By specifying an [IETF language tag](http://en.wikipedia.org/wiki/IETF_language_tag) parameter `lang={tag}` in the request URI it is possible to let the REST API only return strings in the specified language. Not all strings in the database are localized; those strings will always be returned, regardsless of the specified language tag.

The language parameter defaults to English (i.e. `lang=en`). Language filtering can be disabled by specifying `lang=any`.

Pagination
----------
API requests that can return more than one object are always paginated. Two URI parameters are available to control pagination: `per_page={per_page}&page={page}`.

The parameter `{per_page}` specifies the number of desired results per page, and `{page}` the desired page. The default results per page is 10.

If JSON is the desired return format of the request, paginated requests return pagination metadata in a dedicated JSON object:

	{
		"metadata": {
		    "page": 3,
	        "per_page": 15,
	    	...
		},
		"results": [
			...
		]
	}
	
Ordering
--------

ordered=1
ordered by URI
boolean parameters ordered=true also works.

Slower

Counting
--------

The REST API will return the total number of available results for paged queries if `count=1` is added to the request parameters. The result will then include count metadata for that specific query, including filters:

	{
		"metadata": {
	    	"count": 718,
	    	...
		},
		"results": [
			...
		]
	}
	
Slower
	
The count parameter only works for requests where JSON is the desired return format.

Pretty-printed JSON results
----------------------

The REST API will pretty-print the JSON it produces if the `pretty=true` request parameter is used.

Again, this parameter only works for JSON requests.

Example requests
----------------

<table>
	<tr>
		<th>Description</th>
		<th>Request</th>
	</tr>
		<td>List of productions, results 16 to 20</td>
		<td>/rest/production.json?per_page=5&page=4&ordered=true</td>
	</tr>
	</tr>
		<td>Single production with CIDN 0006816f-426c-4e43-8ac4-c8376f4dc3b4, all string properties regardless of language</td>
		<td>/rest/production/0006816f-426c-4e43-8ac4-c8376f4dc3b4.json?lang=any</td>
	</tr>
	</tr>
		<td>Count</td>
		<td>&count=1</td>
	</tr>
	</tr>
		<td></td>
		<td>.rdf plaintext=true</td>
	</tr>
</table>		
