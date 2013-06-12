<?php

class XML{
	var $pre;
	var $text;
	var $post;
	var $name;                                    

	function XML($name = "output.csv") {
		$this->pre = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<items>";
		$this->post = "</items>";	
		$this->name = $name;
    $this->text = "";    
	}  
    
  function addArray($rec){
      $this->text .= "<item>";
      foreach ($rec as $key => $value){
        if ($key == "longitude" || $key == "latitude") {
          $this->text .= "<" . $key . ">" . str_replace(",", ".", $value) . "</" . $key . ">";
        } else {
          $this->text .= "<" . $key . ">" . htmlspecialchars($value) . "</" . $key . ">";          
        }
      }
      $this->text .= "</item>\n";
  }

	function write($append, $last){
    $f = null;
    if ($append) {
      $f = fopen($this->name, "a");
    } else {
      $f = fopen($this->name, "w");
      fwrite($f, $this->pre);
    }    
    
    fwrite($f, $this->text);
    
    if ($last) {
      fwrite($f, $this->post);      
    }
    fclose($f);
	}    

}



?>