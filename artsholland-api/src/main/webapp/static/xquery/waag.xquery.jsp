<%@ page import="java.util.*" %>
<%@ page import="org.waag.ah.tika.util.XSPARQLCharacterEncoder" %>
<%@ page contentType="text/html" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	String replace = XSPARQLCharacterEncoder.getXQueryReplaceString();
%>
module namespace waag = "http://waag.org";

declare function waag:string($arg as xs:string?) {
	if (waag:empty($arg)) then () else waag:replace-characters($arg)
};

declare function waag:replace-characters($arg as xs:string?) as xs:string {
	(: See https://redmine.waag.org/issues/6297 :)
	replace(<%= replace %>, '"', '\\"')
};

declare function waag:capitalize-first-only($arg as xs:string?) as xs:string {
	concat(upper-case(substring($arg, 1, 1)), lower-case(substring($arg, 2)))
};

declare function waag:make-uri($arg as xs:string?) as xs:string {
	encode-for-uri(lower-case(translate($arg, " ", "-")))
};

declare function waag:get-language-tag($arg as xs:string?) {
	substring($arg, 1, 2)	
};

declare function waag:empty($arg as xs:string?) as xs:boolean {
	($arg eq "") or (not(exists($arg)))
};

declare function waag:correct-url($arg as xs:string?) as xs:string {
	if (compare(substring($arg, 1, 4), 'http') != 0) 
	then concat('http://', $arg)
	else $arg
};