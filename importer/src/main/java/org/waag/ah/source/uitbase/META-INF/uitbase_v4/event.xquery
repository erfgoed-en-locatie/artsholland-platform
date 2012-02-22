declare namespace dc="http://purl.org/dc/elements/1.1/";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace xsd="http://www.w3.org/2001/XMLSchema#";
declare namespace waag = "http://waag.org";
declare namespace functx = "http://www.functx.com";
declare namespace ah = "http://purl.org/artsholland/1.0/";
declare namespace owl="http://www.w3.org/2002/07/owl#";
declare namespace time="http://www.w3.org/2006/time#"; 
declare namespace gr="http://purl.org/goodrelations/v1#";

declare function waag:escape-for-regex($arg as xs:string) as xs:string {
	translate($arg, '":', "'&#x58;")
};

let $events := //event

let $baseuri := {"http://purl.org/artsholland/1.0/"}
let $baseuriUitburo := {"http://resources.uitburo.nl/"}

return
	for $event in $events
		let $cidn := {$event/cidn}
		let $euri := {concat($baseuri, "events/", $cidn)}
		let $euriUitburo := {concat($baseuriUitburo, "events/", $cidn)}
			
		let $puri := {$event/link[@type="production"]/@ref}
		let $luri := {$event/link[@type="location"]/@ref}
			
		construct {     
			$euri a ah:Event.
			$euri owl:sameAs $euriUitburo.
			
			$euri ah:atLocation $luri.
			$euri ah:hasProduction $puri.
			
			$euri dc:created {$event/@date-created}^^xsd:string.
			$euri dc:modified {$event/@date-changed}^^xsd:string.
			
			$euri dc:title {$event/title}^^xsd:string.
			$euri dc:description {waag:escape-for-regex($event/description)}^^xsd:string.
			
    	{ for $pricetag in $event/pricetags//pricetag
				#let $turi := {concat($euri, "/tickets/", $pricetag/type}
				construct {						
					$euri ah:hasTicket _:t.
    			_:t a gr:Offering.
    			
    			_:t gr:hasPriceSpecification _:p.
    			_:p a gr:UnitPriceSpecification.
    			
    			_:p gr:hasMinCurrencyValue {$pricetag/lowest}.    			
    			_:p gr:hasMaxCurrencyValue {$pricetag/highest}.
    			_:p gr:hasCurrency "EUR".
    			
    			#	<description>prijs omschrijving</description>
					#	<highest>25.0</highest>
					#	<lowest>12.0</lowest>
					#	<ticketsalesinformation>kvk info</ticketsalesinformation>
					#	<ticketsalesphone>kvk telefoon</ticketsalesphone>
					#	<ticketsalesurl>kvk url</ticketsalesurl>
					#	<type>Jeugd</type>
				}
			}.
    	
    	$euri time:hasBeginning {$event/datetime-start}.
    	$euri time:hasEnd {$event/datetime-end}.
    	    
	    #	Nog doen:
			#		<datetime-start>
			#		<datetime-end>
			#		<type key="PREMIERE"/>
	    #		<status key="NORMAL"/>
	    #		<pricetags>
	    #		<media>
	   	
		}