Uitbase proxy
=============

Arts Holland depends heavily on Uitbase event data supplied by the Nederlands Uitburo and links to it in its Open Linked database. In some cases, it might be necessary to access the original source data. If you own an Arts Holland API key but no Uitbase API key, you can use the Arts Holland Uitbase proxy.

Endpoint
--------

The Uitbase proxy endpoint is `http://api.artsholland.com/uitbase`.

Authentication
--------------

A valid Arts Holland API key is needed for all requests to the Uitbase proxy. The API key is appended to the requested URL, i.e. `api_key=9cbce178ed121b61a0797500d62cd440`. The latter is also the API key that can be used for test purposes.

URI structure
-------------

The Arts Holland Uitbase proxy sends the URI string after `/uitbase` to the Uitbase server. Detailed information about the Uitbase URI structure is found on the Uitbase [Feed API 4.0 wiki](http://wiki.uitburo.nl/index.php/Feed_API_4.0) on the Nederlands Uitburo website.


Example query
-------------
To display a list of productions, issue the following query:

[`/uitbase/productions?api_key=9cbce178ed121b61a0797500d62cd440&rows=1`](http://api.artsholland.com/uitbase/productions?api_key=9cbce178ed121b61a0797500d62cd440&rows=1)