declare namespace dc="http://purl.org/dc/elements/1.1/";
#declare namespace event="http://purl.org/NET/c4dm/event.owl#";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace xsd="http://www.w3.org/2001/XMLSchema#";
declare namespace waag = "http://waag.org";
declare namespace functx = "http://www.functx.com";
declare namespace ah = "http://purl.org/artsholland/1.0/";

declare function waag:escape-for-regex($arg as xs:string) as xs:string {
	translate($arg, '":', "'&#x58;")
};

let $events := //event
let $productions := //production
let $locations := //location
#let $groups := //group

let $baseuri := {"http://resources.uitburo.nl/"}

return

	let $ecidn := {$event/cidn}
	let $euri := {concat($baseuri, "events/", $ecidn)}

	for $event in $events
		let $ecidn := {$event/cidn}
		let $euri := {concat($baseuri, "events/", $ecidn)}
	
			construct {     
				$euri a ah:Event;
					dc:title {$event/cidn}^^xsd:string.
			
		
			}
			
	for $production in $productions		
		let $pcidn := {$production/cidn}
		let $puri := {concat($baseuri, "productions/", $pcidn)}
	
			construct {     
				$puri a ah:Production;
					dc:title {$production/cidn}^^xsd:string.
			
		
			}
	
	
			
	
			
