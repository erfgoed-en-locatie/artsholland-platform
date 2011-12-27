declare namespace dc="http://purl.org/dc/elements/1.1/";
declare namespace event="http://purl.org/NET/c4dm/event.owl#";
declare namespace foaf="http://xmlns.com/foaf/0.1/";
declare namespace geo="http://www.w3.org/2003/01/geo/wgs84_pos#";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace rel="http://purl.org/vocab/relationship/";
declare namespace tl="http://purl.org/NET/c4dm/timeline.owl#";
declare namespace vcard="http://www.w3.org/2006/vcard/ns#";
declare namespace xsd="http://www.w3.org/2001/XMLSchema#";
declare namespace saxon = "http://saxon.sf.net/";
declare namespace waag = "http://waag.org";
declare namespace functx = "http://www.functx.com";

declare function waag:escape-for-regex($arg as xs:string) as xs:string {
   (: replace($arg, '(")', '\\$1') :)
   translate($arg, '":', "'&#x58;")
   (: replace($arg, ":", "&#x58;") :)
   (: functx:replace-multi($arg, ('"', ":"), ("'", "&#x58;")) :)
};

let $events := //event
return
  for $event in $events
  
    let $baseuri := {"http://www.amsterdamsuitburo.nl/"}
    let $address := {$event/location/address/physical}
    
    let $auri := {concat($baseuri, "address/", replace($address/zipcode, " ", ""), $address/street/@number)}
    let $euri := {concat($baseuri, "event/", $event/@eventcidn)}
    let $luri := {concat($baseuri, "location/", $event/location/@locationcidn)}
    let $ouri := {concat($baseuri, "organization/", replace($event/location/title, " ", ""))}

    (: let $searchstring := {concat("<![CDATA[", $event/eventdetail/longdescription, "]]>")} :)
    (: let $searchstring := {saxon:string-to-base64Binary($event/eventdetail/longdescription)} :)
    
    construct {
     
      $euri a event:Event; 
      dc:title {$event/eventdetail/title}^^xsd:string;
      dc:description {waag:escape-for-regex($event/eventdetail/shortdescription)}^^xsd:string;
      dc:description {waag:escape-for-regex($event/eventdetail/longdescription)}^^xsd:string.
          
      { for $genre in $event/eventcategories/genres//genre
          construct {
            $euri dc:subject {$genre/headgenre}^^xsd:string.
            $euri dc:subject {$genre/subgenre}^^xsd:string.
          }
      }. 
      
      $euri event:time _:b.
      _:b a tl:Interval;
          tl:beginsAtDateTime {$event/calendar/startdatetime}^^xsd:dateTime;
          tl:endsAtDateTime {$event/calendar/enddatetime}^^xsd:dateTime.
          
      $euri event:place _:b.
      _:b a geo:Point;
          geo:lat {data($address/geocode/latitude)}^^xsd:decimal;
          geo:long {data($address/geocode/longitude)}^^xsd:decimal.
          
      { for $image in $event/eventdetail/media//file[@type="afbeelding"]
          let $url := $image/hlink
          construct {
            $url a foaf:Image;
            dc:title {$image/title}^^xsd:string; 
            dc:description {$image/description}^^xsd:string.
            $euri foaf:depiction <{$url}>.
          }
      }. 
      
      $auri a vcard:Address;
      vcard:street-address {concat($address/street, " ", $address/street/@number)}^^xsd:string;
      vcard:postal-code {$address/zipcode}^^xsd:string;
      vcard:locality {$address/city}^^xsd:string;
      vcard:country-name {$address/country}^^xsd:string;
      vcard:geo <{$luri}>.
      
      $luri a vcard:Location;
      vcard:adr <{$auri}>;
      vcard:org <{$ouri}>;
      vcard:latitude {$address/geocode/latitude}^^xsd:decimal;
      vcard:longitude {$address/geocode/longitude}^^xsd:decimal.
      
      $ouri a foaf:Organization;
      foaf:name {waag:escape-for-regex($event/location/title)}^^xsd:string.
      
      $ouri foaf:based_near _:b.
      _:b a geo:Point;
          geo:lat {$address/geocode/latitude}^^xsd:decimal;
          geo:long {$address/geocode/longitude}^^xsd:decimal.
      
      $ouri vcard:Organization _:b.
      _:b vcard:organization-name {$event/location/title}^^xsd:string;
          vcard:geo <{$luri}>;
          vcard:adr <{$auri}>;
          vcard:tel {$event/location/contactinfo/overall}^^xsd:string;
          vcard:email {$event/location/contactinfo/mail}^^xsd:string.
            
      { for $url in $event/location/urls//url
          construct {
            #$ouri vcard:url <{$url}>.
            $ouri foaf:homepage <{$url}>.
          }
      }.
            
      { for $image in $event/location/media//file[@type="afbeelding"]
          let $url := $image/hlink
          construct {
            $url a foaf:Image;
            dc:title {string($image/title)}; 
            dc:description {$image/description}^^xsd:string.
            $ouri foaf:depiction <{$url}>.
          }
      }.     
      
      { for $person in $event/medewerkers//medewerker
          let $firstname := {waag:escape-for-regex($person/@firstname)}
          let $surname := {waag:escape-for-regex($person/@lastName)}
          let $fullname := {concat($firstname, " ", $surname)}
          let $puri := {concat("http://www.amsterdamsuitburo.nl/person/", $firstname, $surname)}
          construct {
            $puri a foaf:Person;
            foaf:name {$fullname}^^xsd:string;
            foaf:Organization <{$ouri}>.
            $puri vcard:VCard _:b.
            _:b vcard:fn {$fullname}^^xsd:string;
                vcard:org <{$ouri}>;
                vcard:role {$person/@position}^^xsd:string.
          }
      }
    }
