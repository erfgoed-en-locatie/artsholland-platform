declare namespace dc="http://purl.org/dc/elements/1.1/";
declare namespace event="http://purl.org/NET/c4dm/event.owl#";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace xsd="http://www.w3.org/2001/XMLSchema#";
declare namespace waag = "http://waag.org";
declare namespace functx = "http://www.functx.com";
declare namespace ah = "http://purl.org/artsholland/1.0/";

declare function waag:escape-for-regex($arg as xs:string) as xs:string {
	translate($arg, '":', "'&#x58;")
};

let $events := //event
return
	for $event in $events
  
		let $baseuri := {"http://www.amsterdamsuitburo.nl/"}
		let $address := {$event/location/address/physical}
    
		let $euri := {concat($baseuri, "event/", $event/@eventcidn)}
		let $puri := {concat($baseuri, "event/", $event/@productioncidn)}
    let $luri := {concat($baseuri, "location/", $event/location/@locationcidn)}
    
		let $auri := {concat($baseuri, "address/", replace($address/zipcode, " ", ""), $address/street/@number)}		
		let $ouri := {concat($baseuri, "organization/", replace($event/location/title, " ", ""))}    
    
		construct {     
			$euri a ah:Event.
			
			# =============================================================================================
			# dc:created, dc:modified	
			# =============================================================================================
			$euri dc:created {$event/@datecreated}^^xsd:string.
			$euri dc:modified {$event/@datechanged}^^xsd:string.
			
			# =============================================================================================
			# ah:hasProduction > ah:Production	
			# =============================================================================================
			$euri ah:hasProduction _:b.
				_:b a ah:Production;
					dc:title {$event/eventdetail/title}^^xsd:string;
					dc:description {waag:escape-for-regex($event/eventdetail/shortdescription)}^^xsd:string;
					dc:description {waag:escape-for-regex($event/eventdetail/longdescription)}^^xsd:string.
				
			# =============================================================================================
			# ah:atLocation > ah:Venue	
			# =============================================================================================
			$euri ah:atLocation _:b.
       	_:b a ah:Venue;
       		dc:description {waag:escape-for-regex($event/location/shortdescription)}^^xsd:string;
       		dc:description {waag:escape-for-regex($event/location/longdescription)}^^xsd:string.		
						
			# =============================================================================================
			# ah:hasRoom > ah:Room
			# =============================================================================================
			{ for $room in $event/location/rooms//room
       	construct {
       		_:b a ah:Room.
       		_:b rdf:Label $room.
        	$luri ah:hasRoom _:b.
      	}
      }.
      
      # =============================================================================================
			# ah:inRoom > ah:Room	
			# =============================================================================================
      { for $room in $event/location/rooms//room[@eventat="true"]
       	construct {
       		_:b a ah:Room.
       		_:b rdf:Label $room.
        	$euri ah:inRoom _:b.
      	}
      }
      
			# =============================================================================================
			# ah:hasGenre
			# =============================================================================================
      
      #{ for $genre in $event/eventcategories/genres//genre
      # 	construct {
      # 		_:b a ah:Room.
      # 		_:b rdf:Label $room.
      #  	$luri ah:hasRoom _:b.
      #	}
      #}.
      
      # =============================================================================================
			# ah:hasTicket
			# =============================================================================================
      
      
      # =============================================================================================
			# gr:hasPriceSpecification
			# =============================================================================================
      
      
      # =============================================================================================
			# rdfs:label
			# =============================================================================================					
		  
			     
		}