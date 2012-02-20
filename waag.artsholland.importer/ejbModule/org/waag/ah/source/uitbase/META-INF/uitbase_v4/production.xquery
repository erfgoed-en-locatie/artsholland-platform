declare namespace dc="http://purl.org/dc/elements/1.1/";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace xsd="http://www.w3.org/2001/XMLSchema#";
declare namespace waag = "http://waag.org";
declare namespace functx = "http://www.functx.com";
declare namespace ah = "http://purl.org/artsholland/1.0/";
declare namespace foaf="http://xmlns.com/foaf/0.1/";
declare namespace owl="http://www.w3.org/2002/07/owl#";

declare function waag:escape-for-regex($arg as xs:string) as xs:string {
	translate($arg, '":', "'&#x58;")
};

let $productions := //production

let $baseuri := {"http://purl.org/artsholland/1.0/"}
let $baseuriUitburo := {"http://resources.uitburo.nl/"}

return
	for $production in $productions		
		let $cidn := {$production/cidn}
		let $puri := {concat($baseuri, "productions/", $cidn)}
		let $puriUitburo := {concat($baseuriUitburo, "productions/", $cidn)}
	
		let $genre := {concat("ah:Genre", $production/type/@key)}
	
		construct {
			$puri a ah:Production.			
			$puri owl:sameAs $puriUitburo.
			
			#	Title uit $production/title of uit $production/languages/language/title ?
			#	$puri dc:title {$production/title}^^xsd:string.
				
			$puri ah:hasGenre $genre.			
			$genre rdf:label {$production/type}.	
			#	En wat is dit dan??
			# <genres>
			# 	<genre key="FILM">Film</genre>
			# </genres>
			
			{ for $url in $production/urls//url
				construct {
					#$puri vcard:url $url.
					$puri foaf:homepage $url.
				}
			}.
      
			{ for $language in $production/languages//language
				let $locale := {$language/@locale}
				#	@nl
				construct {
					$puri dc:title {$language/title}^^xsd:string.
					$puri dc:description {$language/description}^^xsd:string.
					$puri ah:shortDescription {$language/short-description}^^xsd:string.
										
					#<party>Party</party>
					#<people>People</people>
				}
			}.
			
			$puri ah:hasAgeLimitLower {$production/age/lower}.
	    $puri ah:hasAgeLimitUpper {$production/age/upper}.
	    
	    { for $tag in $production/tags//tag
				construct {						
					$puri ah:hasTag $tag.
				}
			}
	    
	    #	TODO:
			#	notes	    
			#	<country>NL</country>
			#	<language>nl_NL</language>
			#	<subtitles>en_UK</subtitles>				
		}