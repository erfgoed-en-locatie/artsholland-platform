<%
	String apiKey = "";
	String apiKeyUrl = "";
	if (request.getParameterMap().containsKey("api_key")) {
		apiKey = request.getParameter("api_key");
		if (apiKey.length() > 0) {
			apiKeyUrl = "&api_key=" + apiKey;
		}			
	}	
	
%>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Arts Holland - SPARQL Endpoint</title>
    <link rel="stylesheet" type="text/css" href="/static/snorql/style.css" />
    <script type="text/javascript" src="/static/snorql/prototype.js"></script>
    <script type="text/javascript" src="/static/snorql/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="/static/snorql/sparql.js"></script>
    <script type="text/javascript" src="/static/namespaces/namespaces.jsp"></script>
    <script type="text/javascript" src="/static/snorql/snorql.js"></script>
    
    <script src="/static/codemirror/lib/codemirror.js"></script>
		<script src="/static/codemirror/mode/sparql/sparql.js"></script>
    
    <link rel="stylesheet" href="/static/codemirror/lib/codemirror.css">
    <!-- <link rel="stylesheet" href="/static/codemirror/theme/eclipse.css"> -->
  </head>

  <body class="snorql" onload="snorql.start()">
    <div id="header">
      <h1 id="title">Arts Holland - SPARQL Endpoint</h1>
    </div>

    <div class="section" style="float: right; width: 8em">
      <h2>Browse:</h2>
      <ul>
        <li><a class="graph-link" href="?browse=classes<%= apiKeyUrl %>">Classes</a></li>
        <li><a class="graph-link" href="?browse=properties<%= apiKeyUrl %>">Properties</a></li>
        <li id="browse-named-graphs-link"><a href="?browse=graphs<%= apiKeyUrl %>">Named Graphs</a></li>
      </ul>
    </div>

    <div id="default-graph-section" class="section" style="margin-right: 12em">
      <h2 style="display: inline">GRAPH:</h2>
      <p style="display: inline">
        Default graph.
        <a href="?browse=graphs">List named graphs</a>
      </p>
    </div>

    <div id="named-graph-section" class="section" style="margin-right: 12em">
      <h2 style="display: inline">GRAPH:</h2>
      <p style="display: inline">
        <span id="selected-named-graph">Named graph goes here</span>.
        <a href="javascript:snorql.switchToDefaultGraph()">Switch back to default graph</a>
      </p>
    </div>

    <div class="section" style="margin-right: 12em">
      <h2>SPARQL <a href="/sparql" id="toggleprefixes">(show PREFIX tags)</a>:</h2>
      <pre id="prefixestext"></pre>
      <form id="queryform" action="#" method="get"><div>
        <input type="hidden" name="query" value="" id="query" />
        <input type="hidden" name="apiKey" value="<%= apiKey %>" id="apiKey" />
        <input type="hidden" name="output" value="json" id="jsonoutput" disabled="disabled" />
        <input type="hidden" name="stylesheet" value="" id="stylesheet" disabled="disabled" />
        <input type="hidden" name="graph" value="" id="graph-uri" disabled="disabled" />
        <input type="hidden" name="selectoutput" id="selectoutput" value="browse" />
      </div></form>
      <div>
        <textarea name="query" rows="12" cols="80" id="querytext"></textarea>
        <!-- Results:
        <select id="selectoutput" onchange="snorql.updateOutputMode()">
          <option selected="selected" value="browse">Browse</option>
          <option value="json">as JSON</option>
          <option value="xml">as XML</option>
          <option value="xslt">as XML+XSLT</option>
        </select>
        <span id="xsltcontainer"><span id="xsltinput">
          XSLT stylesheet URL:
          <input id="xsltstylesheet" type="text" value="snorql/xml-to-html.xsl" size="30" />
        </span></span> -->
        <input type="button" value="Go!" onclick="snorql.submitQuery()" />
        <input type="button" value="Reset" onclick="snorql.resetQuery()" />
      </div>
    </div>

    <div class="section">
      <div id="result"><span></span></div>
    </div>

    <div id="footer">Powered by <a id="poweredby" href="#">Snorql</a></div>
  </body>
</html>
