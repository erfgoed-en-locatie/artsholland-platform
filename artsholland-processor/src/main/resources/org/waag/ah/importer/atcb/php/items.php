<?php
mb_internal_encoding("UTF-8"); 

require_once 'csv.php';
require_once 'xml.php';

class Items{
    var $output;
    var $append;
    var $typestart;
    var $list;
    var $items;
    var $debug;
    var $starttime;
    var $total;
    
    function Items(){
      
        date_default_timezone_set('Europe/Amsterdam');
        $this->starttime = time();
        $this->append = false;
        $this->list = Array();
        $this->items = Array();
        $this->debug = false;
        $this->total = 0;
    }
    
    function setOutput($url){
        $this->output = $url;
    }
    
    function setAppend($append){
        $this->append = $append;
    }
    
    function setTypeStart($startswith){
        $this->typestart = $startswith;
    }
    
    function setDebug($debug){
        $this->debug = $debug;
    }
      
    function debug($txt){
        if($this->debug){
            print(date("i:s", time() - $this->starttime) . " - ". $txt ."<BR>\n");
        }
    }

    /**
    * Input is an array SimpleXML Object, returned is the specific element with the specified Title
    * 
    * @param SimpleXML Object $links 
    * @param SimpleXML Object $title
    */
    function getLink(&$links, $title){
        foreach($links as $link){
            if((string)$link["title"] == $title){
                return $link;
            }
        }
        return null;
    }

    /**
    * Given an NDTRC-item in an Simple XML Object, this function returns a boolean indicating of the item should be included, based on Publication status and Calendar
    * 
    * @param SimpleXML Object $item
    */

    function itemToInclude(&$item){
        //$trcid = str_replace(Array("NTrcitems('", "')"), "", $item->link[0]["href"]);                
        //$url = "http://catalog.atcb.nl/Service.svc/NTrcitems('". $trcid ."')?\$expand=Trcitemdetails,Medias,Calendar,Calendar/Patterns,Calendar/Singles,Trcitemcategory/Types,Contactinfo/Phones,Contactinfo/Addresses,Contactinfo/Mails,Contactinfo/Urls,Contactinfo/Faxes,Location,Location/Addres/Physical&\$select=Trcid,Trcitemdetails/*,Published,Medias/*,Calendar/Excludeholidays,Calendar/Cancelled,Calendar/Soldout,Calendar/Patterns/*,Calendar/Singles/*,Trcitemcategory/Types/*,Contactinfo/Phones/*,Contactinfo/Addresses/*,Contactinfo/Mails/*,Contactinfo/Urls/*,Contactinfo/Faxes/*,Location/*,Location/Addres/Physical/Country,Location/Addres/Physical/Housenr,Location/Addres/Physical/Zipcode,Location/Addres/Physical/City,Location/Addres/Physical/Street,Location/Addres/Physical/Xcoordinate,Location/Addres/Physical/Ycoordinate";
        
        $result = true;
        //Check if published
        if(!(string)$item->content->properties->Published == "true") return false;
        
        //Check Calendar
        $calendar = $this->getLink($item->link, "Calendar");
        $today = strtotime(date("Y-m-d 00:00:00"));
        if($calendar){
            $patterns = $this->getLink($calendar->inline->entry->link, "Patterns");
            $singles = $this->getLink($calendar->inline->entry->link, "Singles");
            if($singles->inline->feed->entry->content){
                foreach($singles->inline->feed->entry as $single){
                    $date = str_replace("/","-", $single->content->properties->Date);
                    if(strtotime($date) > $today) return true;
                }
            }
            if($patterns->inline->feed->entry->content){
                foreach($patterns->inline->feed->entry as $period){
                    $date = str_replace("/","-", (string)$period->content->properties->Enddate);
                    if(trim($date) == "" || strtotime($date) > $today) return true; //No enddate, or enddate < today
                }
            }

            if(!$patterns->inline->feed->entry->content && !$singles->inline->feed->entry->content){
                //No pattern and no singles, so no date information ("Altijd open")
                return true;
            }   else {
                //Either patterns or singles are checked, but no match is found.
                return false;
            }
        } else {
            //No calendar (All items seem to have a calendar). Continue item;
            return true;
        }
        return false;
    }


    /**
    * Get a list of items from an URL, and return an Array of trcid's for all items to include (based on Publication status and Calendar)
    * 
    */

    function getList(){
        $items = Array();
        
        $url = "http://catalog.atcb.nl/Service.svc/NTypesTypes()?\$filter=startswith(Catid,'". $this->typestart ."')&\$orderby=Catid&\$expand=TypesTrcitemTrcitemcategorys,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar/Patterns,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar/Singles&\$select=Catid,Value,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Trcid,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Published,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar/Excludeholidays,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar/Cancelled,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar/Soldout,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar/Patterns/*,TypesTrcitemTrcitemcategorys/TrcitemcategoryTrcitem/Calendar/Singles/*";
        $context = stream_context_create(array(
        'http' => array(
            'header'  => "Authorization: Basic " . base64_encode("opendata:c@t@log")
        )
        ));
        
        $data = file_get_contents($url, false, $context);
        //$data = file_get_contents("list.xml", false, $context); //Use this for local emulation
        
        $data = str_replace(Array("<m:", "</m:", "<d:", "</d:"),  Array("<","</","<","</"), $data);
        $list = new SimpleXMLElement($data);
        
        foreach($list->entry as $category){
            $cat = (string)$category->id;
            foreach($category->link[1]->inline->feed->entry as $entry){
                $item = $entry->link[1]->inline->entry;
                $trcid = str_replace(Array("NTrcitems('", "')"), "", $item->link[0]["href"]);
                if($this->itemToInclude($item)){
                    $items[] = $trcid;
                }
                $this->total++;
            }
        }
        
        $this->list = $items;
    }

    /**
    * Download the details of an TRCitem,  extract all needed values in an Array and add Array to specified item
    * 
    * @param mixed $trcid
    * @param mixed $items
    */

    function addItem($trcid){
        //http://catalog.atcb.nl/Service.svc/NTrcitems('70418598-d45a-4909-8bd6-8af1a522ead0')?$expand=Trcitemdetails,Medias,Calendar,Calendar/Patterns,Calendar/Singles,Trcitemcategory/Types,Contactinfo/Phones,Contactinfo/Addresses,Contactinfo/Mails,Contactinfo/Urls,Contactinfo/Faxes,Location,Location/Addres/Physical&$select=Trcid,Trcitemdetails/*,Published,Medias/*,Calendar/Excludeholidays,Calendar/Cancelled,Calendar/Soldout,Calendar/Patterns/*,Calendar/Singles/*,Trcitemcategory/Types/*,Contactinfo/Phones/*,Contactinfo/Addresses/*,Contactinfo/Mails/*,Contactinfo/Urls/*,Contactinfo/Faxes/*,Location/*,Location/Addres/Physical/Country,Location/Addres/Physical/Housenr,Location/Addres/Physical/Zipcode,Location/Addres/Physical/City,Location/Addres/Physical/Street,Location/Addres/Physical/Xcoordinate,Location/Addres/Physical/Ycoordinate
        //Set URL
        $url = "http://catalog.atcb.nl/Service.svc/NTrcitems('". $trcid ."')?\$expand=Trcitemdetails,Medias,Calendar,Calendar/Patterns,Calendar/Singles,Trcitemcategory/Types,Contactinfo/Phones,Contactinfo/Addresses,Contactinfo/Mails,Contactinfo/Urls,Contactinfo/Faxes,Location,Location/Addres/Physical&\$select=Trcid,Trcitemdetails/*,Published,Lastupdated,Medias/*,Calendar/Excludeholidays,Calendar/Cancelled,Calendar/Soldout,Calendar/Patterns/*,Calendar/Singles/*,Trcitemcategory/Types/*,Contactinfo/Phones/*,Contactinfo/Addresses/*,Contactinfo/Mails/*,Contactinfo/Urls/*,Contactinfo/Faxes/*,Location/*,Location/Addres/Physical/Country,Location/Addres/Physical/Housenr,Location/Addres/Physical/Zipcode,Location/Addres/Physical/City,Location/Addres/Physical/Street,Location/Addres/Physical/Xcoordinate,Location/Addres/Physical/Ycoordinate";
        //Inclusief Prijs:
        //$url = "http://catalog.atcb.nl/Service.svc/NTrcitems('". $trcid ."')?\$expand=Trcitemdetails,Price,Medias,Calendar,Calendar/Patterns,Calendar/Singles,Trcitemcategory/Types,Contactinfo/Phones,Contactinfo/Addresses,Contactinfo/Mails,Contactinfo/Urls,Contactinfo/Faxes,Location,Location/Addres/Physical&\$select=Trcid,Trcitemdetails/*,Price/*,Published,Medias/*,Calendar/Excludeholidays,Calendar/Cancelled,Calendar/Soldout,Calendar/Patterns/*,Calendar/Singles/*,Trcitemcategory/Types/*,Contactinfo/Phones/*,Contactinfo/Addresses/*,Contactinfo/Mails/*,Contactinfo/Urls/*,Contactinfo/Faxes/*,Location/*,Location/Addres/Physical/Country,Location/Addres/Physical/Housenr,Location/Addres/Physical/Zipcode,Location/Addres/Physical/City,Location/Addres/Physical/Street,Location/Addres/Physical/Xcoordinate,Location/Addres/Physical/Ycoordinate";        
        //Set Authentication
        $context = stream_context_create(array(
        'http' => array(
            'header'  => "Authorization: Basic " . base64_encode("opendata:c@t@log"),
            'timeout' => 10
        )
        ));
                    
        //echo "Downloading: ";
        //echo $url . "\n";
        
        //Get content and parse to SimpleXML
        $data = @file_get_contents($url, false, $context);
        
        if ($data !== FALSE) {
                
          $data = str_replace(Array("<m:", "</m:", "<d:", "</d:"),  Array("<","</","<","</"), $data);
          $item = new SimpleXMLElement($data);

          //echo "Gelukt!!!!!!\n\n";        

          //Set default Array
          $temp = Array("trcid" => $trcid, "title" => null, "shortdescription" => null, "longdescription" => null, "calendarsummary" => null,"titleEN" => null, "shortdescriptionEN" => null, "longdescriptionEN" => null, "calendarsummaryEN" => null, "types" => Array(), "ids" => Array(), "locatienaam" => "", "city" => null, "adres" => null, "zipcode" => null, "latitude" => null, "longitude" => null, "urls" => Array(), "media" => Array(), "thumbnail" => null, "datepattern_startdate" => null, "datepattern_enddate" => null, "singledates" => Array(), "lastupdated" => date("Y-m-d H:i:s", strtotime($item->content->properties->Lastupdated)));    

          //Extract needed links
          $calendar = $this->getLink($item->link, "Calendar");
          //$price = $this->getLink($item->link, "Price");
          $contactinfo = $this->getLink($item->link, "Contactinfo");
          $categories =  $this->getLink($item->link, "Trcitemcategory");
          $types = $this->getLink($categories->inline->entry->link, "Types");
          $media =  $this->getLink($item->link, "Medias");
          $details =  $this->getLink($item->link, "Trcitemdetails");
          $location =  $this->getLink($item->link, "Location");
        
          //DEBUG
          /*
          print("<PRE>"); 
              print_r($temp); 
          print("</PRE>"); 
          exit();
          */
        
          //Get Details (Dutch and English (NDTRC supports de, sp, it and fr as well))
          foreach($details->inline->feed->entry as $language){
              if($language->content->properties->Lang == "nl"){
                   $temp["title"] = (string)$language->content->properties->Title;
                   $temp["calendarsummary"] = (string)$language->content->properties->Calendarsummary;
                   $temp["shortdescription"] = (string)$language->content->properties->Shortdescription;
                   $temp["longdescription"] = (string)$language->content->properties->Longdescription;
              }elseif($language->content->properties->Lang == "en"){
                   $temp["titleEN"] = (string)$language->content->properties->Title;
                   $temp["calendarsummaryEN"] = (string)$language->content->properties->Calendarsummary;
                   $temp["shortdescriptionEN"] = (string)$language->content->properties->Shortdescription;
                   $temp["longdescriptionEN"] = (string)$language->content->properties->Longdescription;
              }
          }

          //Get Types    
          foreach($types->inline->feed->entry as $type){
              $temp["types"][] = (string)$type->content->properties->Value;
              $temp["ids"][] = (string)$type->content->properties->Catid;
          }
        
          //Get Address
          $addres = $this->getLink($location->inline->entry->link, "Addres");
          $physical = $this->getLink($addres->inline->entry->link, "Physical");
        
          if($addres){
              $temp["locatienaam"] = (string)$location->inline->entry->content->properties->Label->Value;
              $temp["city"] = (string)$physical->inline->entry->content->properties->City->Value;
              $temp["adres"] = trim((string)$physical->inline->entry->content->properties->Street->Value ." " .(string)$physical->inline->entry->content->properties->Housenr);
              $temp["zipcode"] = (string)$physical->inline->entry->content->properties->Zipcode;
              $temp["latitude"] =  (string)$physical->inline->entry->content->properties->Ycoordinate;
              $temp["longitude"] = (string)$physical->inline->entry->content->properties->Xcoordinate;
          }

          //Get Media
          foreach($media->inline->feed->entry as $media){
              $temp["media"][] = (string)$media->content->properties->Hlink;
              if((string)$media->content->properties->Main == "true"){
                  $temp["thumbnail"] = (string)$media->content->properties->Hlink;
              }
          }
        
          //Get Calendar
            if($calendar){
                  $patterns = $this->getLink($calendar->inline->entry->link, "Patterns");
                  $singles = $this->getLink($calendar->inline->entry->link, "Singles");
                  if($singles->inline->feed->entry->content){
                      foreach($singles->inline->feed->entry as $single){
                          $temp["singledates"][] = str_replace("/","-", (string)$single->content->properties->Date);
                      }
                  }
                  if($patterns->inline->feed->entry->content){
                      foreach($patterns->inline->feed->entry as $period){
                          $temp["datepattern_startdate"] = str_replace("/","-", (string)$period->content->properties->Startdate);
                          $temp["datepattern_enddate"] = str_replace("/","-", (string)$period->content->properties->Enddate);
                      }
                  }
              }    
        
           //GetPrice (Price is not yet included. Difficult although, because in TRC-XML Price can be saved at two different places.
           //$priceResponse = $svc->LoadProperty($item, 'Price');
           //print("<PRE>"); print_r($price); print("</PRE>");
         

          //Get Contactinfo (Currently only Urls, same can be done with Addresses, Mails, Phones and Faxes)
          if($contactinfo){
              $urls = $this->getLink($contactinfo->inline->entry->link, "Urls");
              if($urls){
                  foreach($urls->inline->feed->entry as $url){
                      $temp["urls"][] = (string)$url->content->properties->Value;
                  }
              }
          }

           $temp["types"] = implode(",", $temp["types"]);
           $temp["ids"] = implode(",", $temp["ids"]);
           $temp["urls"] = implode(",", $temp["urls"]);
           $temp["media"] = implode(",", $temp["media"]);
           $temp["singledates"] = implode(",", $temp["singledates"]);
         
           $this->items[] = $temp;
         } else {
         	// Gelukt...
         }
    }
    
    function getItems(){
        $this->debug("Start download list for ". $this->typestart ."*");
        $this->getList();
        $this->debug("List downloaded and filtered (". count($this->list) ." of ". $this->total ." items)");
        foreach($this->list as $trcid){
            $this->addItem($trcid);
        }
        $this->debug("Items downloaded");
    }
    
    function saveToXML($last=true){
      $xml = new XML("./output/xml/" . $this->output . ".xml");
      foreach($this->items as $item){
          $xml->addArray($item);
      }
      $xml->write($this->append, $last);

      $this->debug("Saved as XML (" . $this->output . ")");
    }
    
    function saveToCSV(){
        $csv = new CSV("./output/csv/" . $this->output . ".csv");
        $csv->addArrayHeader(Array("trcid", "title", "shortdescription", "longdescription", "calendarsummary", "titleEN", "shortdescriptionEN", "longdescriptionEN", "calendarsummaryEN", "types", "ids","locatienaam", "city", "adres", "zipcode", "latitude", "longitude", "urls", "media", "thumbnail", "datepattern_startdate", "datepattern_enddate", "singledates", "lastupdated"));
        foreach($this->items as $item){
            $csv->addArray($item);
        }
        if($this->append){
            $csv->append();
        } else {
            $csv->write();
        }
        
        $this->debug("Saved as CSV (" . $this->output . ")");
    }
} 
?>
