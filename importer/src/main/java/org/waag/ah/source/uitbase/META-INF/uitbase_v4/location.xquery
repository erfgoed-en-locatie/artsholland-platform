declare namespace dc="http://purl.org/dc/elements/1.1/";
#declare namespace event="http://purl.org/NET/c4dm/event.owl#";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace xsd="http://www.w3.org/2001/XMLSchema#";
declare namespace waag = "http://waag.org";
declare namespace functx = "http://www.functx.com";
declare namespace ah = "http://purl.org/artsholland/1.0/";
declare namespace geo="http://www.w3.org/2003/01/geo/wgs84_pos#";
declare namespace foaf="http://xmlns.com/foaf/0.1/";
declare namespace vcard="http://www.w3.org/2006/vcard/ns#";
declare namespace owl="http://www.w3.org/2002/07/owl#";

declare function waag:escape-for-regex($arg as xs:string) as xs:string {
	translate($arg, '":', "'&#x58;")
};

let $locations := //location

let $baseuri := {"http://purl.org/artsholland/1.0/"}
let $baseuriUitburo := {"http://resources.uitburo.nl/"}

return
	for $location in $locations
		
		let $cidn := {$location/cidn}
		let $luri := {concat($baseuri, "venues/", $cidn)}
		let $luriUitburo := {concat($baseuriUitburo, "locations/", $cidn)}
		let $venueType := {concat("ah:VenueType", $location/type/@key)}
			
		construct {     
			$luri a ah:Venue.			
			$luri owl:sameAs $luriUitburo.
			
			$luri dc:title {$location/name}^^xsd:string.
				
			# Wat doet functie data()???
			$luri geo:lat {data($location/geocode/latitude)}^^xsd:decimal.
			$luri geo:long {data($location/geocode/longitude)}^^xsd:decimal.

			$luri dc:created {$location/@date-created}^^xsd:string.
			$luri dc:modified {$location/@date-changed}^^xsd:string.
			
			{ for $room in $location/rooms//room
				let $ruri := {concat($luri, "/rooms/", $room)}
				construct {
					$ruri a ah:Room.
					$ruri rdf:Label $room.
					$luri ah:hasRoom $ruri.
				}
			}.
			
			{ for $url in $location/urls//url
				construct {
					#$luri vcard:url $url.	
					$luri foaf:homepage $url.
				}
			}.
			
			$luri ah:hasVenueType $venueType.			
			$venueType rdf:label {$location/type}.		
			
			{ for $medium in $location/media//medium
				let $muri := {$medium/ref}
				construct {
				
				 	# Of:
				 	# 	Voeg alleen plaatjes toe (type=afbeelding)				 	
				 	# 	Gebruik geen foaf:Image maar iets als attachment etc.
				 	
				 	$muri a foaf:Image.
          $muri dc:title {$medium/title}^^xsd:string.
          $muri dc:description {$medium/description}^^xsd:string.
          $muri foaf:depiction {$muri}.
            
        	#	<title>A family again</title>
          #	<ref>http://uitbase.aub.nl/gfx/content/family308.jpg</ref>
          #	<alt>A family again</alt>
          #	<description>Pic description</description>
          #	<type>afbeelding</type>
        }
      }.
			
			$luri vcard:street-address {concat($location/street/name, " ", $location/street/number, $location/street/addition)}.
			$luri vcard:locality {$location/place}.
			$luri vcard:postal-code {$location/postal-code}.
	    	    
			{ for $tag in $location/tags//tag
				construct {						
					$luri ah:hasTag $tag.
				}
			}
			
			#	<languages>
      #		<language locale="nl_NL">
      #			<sortname>Pathé Maastricht</sortname>
      #			<short-description>short description</short-description>
      #			<description>long description</description>
      #			<opening-hours>5-9 m/f</opening-hours>
      #			<ov-info>OV Info here</ov-info>
      #			<handicapped-info>Handicapped info here</handicapped-info>
      #			<pricing-information>pricing information</pricing-information>
      #		</language>
      #		<language locale="en_UK">
      #			<sortname>Pathé Maastricht</sortname>
      #			<short-description>short description</short-description>
      #			<description>long description</description>
      #			<opening-hours>5-9 m/f</opening-hours>
      #			<ov-info>OV Info here</ov-info>
      #			<handicapped-info>Handicapped info here</handicapped-info>
      #			<pricing-information>pricing information</pricing-information>
      #		</language>
    	#	</languages>
			
			#	TODO:
			#	editions		
		}
