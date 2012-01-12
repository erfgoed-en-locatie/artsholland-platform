<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" >
<title>bigdata&#174; NanoSparqlServer</title>
<!-- $Id: index.html 5738 2011-11-22 20:52:40Z thompsonbry $ -->
</head>
<body>

<p>

Welcome to bigdata&#174;.  Please consult the 
<a href="https://sourceforge.net/apps/mediawiki/bigdata/index.php?title=NanoSparqlServer"> 
documentation</a> for information on using the NanoSparqlServer's REST Api.

</p>

<!-- Note: Some applications (firefox 7) can not handle a GET with a very long
     URL.  For that reason ONLY this operation defaults to a POST.  You SHOULD
     use GET for database queries since they are, by and large, idempotent. 
     -->
<FORM action="sparql" method="post">
   <P>
   <TEXTAREA name="query" rows="30" cols="100" title="Enter SPARQL Query."
   >SELECT * { ?s ?p ?o } LIMIT 1</TEXTAREA>
   </P><P>
   <INPUT type="submit" value="Send" title="Submit query.">
   <INPUT type="checkbox" name="explain" value="true"
          title="Explain query plan rather than returning the query results."
          > Explain
   <INPUT type="checkbox" name="analytic" value="true"
          title="Enable the analytic query package."
          > Analytic
   </P>
</FORM>

<p>
The following URLs should be active when deployed in the default configuration:
</p>
<dl>
<dt>http://hostname:port/bigdata</dt>
<dd>This page.</dd>
<dt>http://hostname:port/bigdata/sparql/</dt>
<dd>The SPARQL REST API.</dd>
<dt>http://hostname:port/bigdata/status</dt>
<dd>A <a href="status">status</a> page.</dd>
<dt>http://hostname:port/bigdata/counters</dt>
<dd>A <a href="counters"> performance counters</a> page.</dd>
</dl>

<p>
Where <i>hostname</i> is the name of this host and <i>port</i> is the port at
which this page was accessed.
</p>

</body>
</html>