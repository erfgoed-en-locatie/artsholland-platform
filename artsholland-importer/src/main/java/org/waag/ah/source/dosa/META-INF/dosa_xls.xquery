declare namespace dc="http://purl.org/dc/elements/1.1/";
declare namespace os="http://linkeddata.few.vu.nl/os-amsterdam/schema/";
declare namespace osgeo="http://linkeddata.few.vu.nl/os-amsterdam/plaats/";
declare namespace qb="http://purl.org/linked-data/cube#";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace rdfs="http://www.w3.org/2000/01/rdf-schema#";
declare namespace xsd="http://www.w3.org/2001/XMLSchema#";
declare namespace waag="http://waag.org";

declare namespace sdmxa="http://purl.org/linked-data/sdmx/2009/attribute#";
declare namespace sdmxc="http://purl.org/linked-data/sdmx/2009/concept#";
(: declare namespace sdmxd"http://purl.org/linked-data/sdmx/2009/dimension#"; :)
declare namespace sdmxmm="http://purl.org/linked-data/sdmx/2009/measure#";

declare default element namespace "http://www.w3.org/1999/xhtml";

declare function waag:if-empty($arg, $value) {
  if (string($arg) != "")
  then data($arg)
  else $value
};

declare function waag:capitalize-first($arg as xs:string) as xs:string {
   concat(upper-case(substring($arg,1,1)), substring($arg,2))
};

declare function waag:abs-uri($arg as xs:string, $context) as xs:string {
  let $parent := resolve-QName($arg, $context)
  return concat(namespace-uri-from-QName($parent), local-name-from-QName($parent))
};

declare variable $config external; 

let $sheets := //body/*[@class="page"]

for $sheet in $sheets

  let $name := data($sheet/h1)
  let $cnf := $config//structure[@sheet=$name]
  let $properties := $cnf//property
  let $structure := waag:abs-uri($cnf/@name, $cnf)
  let $layout := $cnf/layout
  let $dataset := //head/meta[@name="resourceName"]/@content

  (: sheet type dependent vals :)
  let $startrow := number($cnf/layout/@offsetx)
  let $startcol := waag:if-empty($cnf/layout/@offsety, 1)
  let $headers := data($sheet//tr[$startrow]/td[position()>$startcol])
  let $rows := $sheet//tr[position()>$startrow and td[1]!=""]
  let $cols := $sheet//tr[position()>$startrow]/td[position()>$startcol]
  let $rowtype := waag:abs-uri($layout/@cols, $cnf)
  let $coltype := waag:abs-uri($layout/@rows, $cnf)
  let $valtype := waag:abs-uri($layout/@vals, $cnf)

  construct {
    
    $structure a qb:DataStructureDefinition.
    { for $pos in (1 to count($properties))
        let $property := $properties[$pos]
        let $subject := data($property/@name)
        construct {
          $subject a rdf:Property, qb:{concat(waag:capitalize-first($property/@type), "Property")};
            rdfs:label {data($property/@label)};
            rdfs:subPropertyOf {waag:abs-uri($property/@extends, $cnf)};
            rdfs:range {waag:abs-uri($property/@range, $cnf)}.
          $structure qb:component _:a{data($pos)}.
            _:a{data($pos)} qb:{data($property/@type)} {$subject}.
        }
    }.

    $dataset a qb:DataSet;
      rdfs:label {$name};
      rdfs:comment {$sheet//tr[1]/td[1]};
      qb:structure {$structure}.
        
    { if ($cnf/layout/@type = "crosstab") 
      then
        for $row in (1 to count($rows))
          let $t1 := replace(data($rows[$row]/td[1]), " ", "_")
          let $fields := $rows[$row]/td[position()>1]
          for $col in (1 to count($fields))
            let $t2 := replace($headers[$col], "[/ _]+", "_")
            let $id := concat("rec_", $row, "_", $col)
            construct {
              _:{$id} a qb:Observation;
                qb:dataSet $dataset;
                <{$rowtype}> osgeo:{$t1};
                <{$coltype}> osgeo:{$t2};
                <{$valtype}> {$fields[$col]}.
            }
      else ()
    }
  }