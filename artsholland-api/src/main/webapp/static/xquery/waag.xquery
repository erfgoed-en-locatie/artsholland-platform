module namespace waag = "http://waag.org";

declare function waag:replace-characters($arg as xs:string?) as xs:string {
	(: See https://redmine.waag.org/issues/6297 :)
	replace(replace(replace(replace($arg, ":", "[[waag_colon]]"), "<", "[[waag_less_than]]"), ">", "[[waag_greater_than]]"), '"', '\\"')
	
	(: translate($arg, '":<>', "'&#58;&#60;$#62;") :)
	
	(: translate($arg, '":', "'&#x58;") :)
};

declare function waag:capitalize-first-only($arg as xs:string?) as xs:string {
	concat(upper-case(substring($arg, 1, 1)), lower-case(substring($arg, 2)))
};

declare function waag:make-uri($arg as xs:string?) as xs:string {
	encode-for-uri(lower-case(translate($arg, " ", "-")))
};

declare function waag:get-language-tag($arg as xs:string?) as xs:string {
	substring($arg, 1, 2)
};