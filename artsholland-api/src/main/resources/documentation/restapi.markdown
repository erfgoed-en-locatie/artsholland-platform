REST API
========

A REST API is available to access the most important parts of the Arts Holland semantic Open Linked Database.

Accessing this data using the [SPARQL endpoint](http://api.artsholland.com/sparql?api_key=9cbce178ed121b61a0797500d62cd440) provides the most flexibility, but its [query language](http://en.wikipedia.org/wiki/SPARQL) and response formats might sometimes be too complex and overwhelming. For simplicity’s sake, Arts Holland also provides a REST API, described below.

Authentication
--------------

A valid Arts Holland API key is needed for all REST API requests. The API key is appended to the requested URI, i.e. `api_key=9cbce178ed121b61a0797500d62cd440`. The latter is also the API key that can be used for test purposes.

Endpoint
--------

The REST API endpoint is `http://api.artsholland.com/rest/`

Data model
----------

more data, REST API only 



### Main elements ###
The objects returned by the REST API are described below in short:

<table>
	<tr>
		<th>Element</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>Production</td>
		<td>dsdsd</td>
	</tr>
	<tr>
		<td>Event</td>
		<td>A cultural production at a specific location and time</td>
	</tr>
	<tr>
		<td>Venue</td>
		<td>A physical location where events take place</td>
	</tr>
</table>
	
	
Each event hosts at least production and takes place in at least one venue.
	

Production	
A play, a movie, a concert, an exhibition, a lecture, etc. A production can be performed multiple times, and so can be be hosted by multiple events and venues.

Events, venues and productions are identified by a [CIDN number](http://wiki.uitburo.nl/index.php/Nationaal_CIDN_register) managed by the Nederlands Uitburo.

### Other elements ###
Element	Description
Room	Child element of a venue, in which events can take place. A venue can have multiple rooms. Events can be held in one or more rooms of a specific venue.
Attachment	Child element of both venues and events. An attachment holds information about images, movies or documents linked to the venue or event.
Offering	Child element of an event. Information about tickets; an event can have multiple offers.
PriceSpecification	Child element of an offer. Price and currency information.
Genre	Productions are categorized by genre. Examples are dance, documentary, exhibition and musical.
VenueType	Venues are categorized by type. Museums, cinemas and theaters, for example, all have a different VenueType.
URI structure
URI	Description
/rest/event	Lists and describes all events
/rest/venue	Lists and describes all venues
/rest/production	Lists and describes all productions
/rest/genre	Lists and describes all genres
/rest/venuetype	Lists and describes all venue types
All the above API requests are paginated.

Single objects
URI	Description
/rest/event/{cidn}	Describes event with cidn = {cidn}
/rest/venue/{cidn}	Describes venue with cidn = {cidn}
/rest/production/{cidn}	Describes production with cidn = {cidn}
Child elements and relations
URI	Description
/rest/event/{cidn}/venue	Lists and describes all venues in which event with cidn = {cidn} takes place
/rest/event/{cidn}/production	Lists and describes all productions associated with event with cidn = {cidn}
/rest/event/{cidn}/room	Lists and describes all rooms in which event with cidn = {cidn} takes place
/rest/event/{cidn}/attachment	Lists and describes all attachments associated with event with cidn = {cidn}
/rest/event/{cidn}/offering	Lists and describes all offers available for event with cidn = {cidn}
/rest/event/{cidn}/offering/{name}/price	Describes price specification of offer with name = {name}
/rest/venue/{cidn}/event	Lists and describes all events which take place in venue with cidn = {cidn}
/rest/venue/{cidn}/production	Lists and describes all productions which take place in venue with cidn = {cidn}
/rest/venue/{cidn}/room	Lists and describes all rooms in venue with with cidn = {cidn}
/rest/venue/{cidn}/attachment	Lists and describes all attachments associated with venue with cidn = {cidn}
/rest/production/{cidn}/event	Lists and describes all events associated with production with cidn = {cidn}
/rest/production/{cidn}/venue	Lists and describes all venues in which production with cidn = {cidn} takes place
All the above API requests are paginated.

Filters
URI	On	Description	Example
search	Event, Production, Venue	Free text search on description field (RDF dc:description property)	search=theater
genre	Production	Filters production by genre	genre=genreLiterature
type	Venue	Filters venues by venue type	type=venueTypeCinema
nearby	Event, Venue	Finds events or venues nearby a specific geographic location. The filter accepts WKT points: POINT(longitude latitude) (in the WKT specification, x is longitude and y is latitude). A second argument specifies the dinstance in meters.	nearby=POINT(5.807 53.201)&distance=2500
locality	Event, Venue	Filters events or venues on city or town (RDF vcard:locality property). The locality filter is case-insensitive.	locality=utrecht
before, after	Event	Filters events on start date and time, only returns events which start before or after specified date and time. The filters accept the ISO 8601 date and time format.	before=2012-04-06
after=2012-07-02
min_price, max_price	Event	Finds events which have a ticket with a price (in any currency) of at least min_price or at most max_price.	min_price=10
max_price=15
Filters currently only work on first level API requests (e.g. /rest/event, /rest/venue and /rest/production).

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
