<?php

class XML{
	var $pre;
	var $text;
	var $post;
	var $name;                                    

	function XML($name = "output.csv") {
		$this->pre = "<items>";
		$this->post = "</items>";	
		$this->name = $name;
    $this->text = "";    
	}  
    
  function addArray($rec){
      $this->text .= "<item>";
      foreach ($rec as $key => $value){
          $this->text .= "<" . $key . ">" . html_entity_decode(htmlspecialchars($value)) . "</" . $key . ">";
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