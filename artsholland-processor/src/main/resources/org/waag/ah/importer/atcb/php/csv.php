<?php
/**
 * csv
 * 
 * This file contains the CSV class which is used to create and output CSV files.
 * @author Jasper Soetendal
 * @version 0.5
 * @package Splace
 */

class CSV{
	var $pre;
	var $text;
	var $post;
	var $name;                                    

	function CSV($name = "output.csv"){
		$this->pre = "";
		$this->post = "";	
		$this->name = $name;
        $this->text = "";
	}
    
    function addArrayHeader($rec){
        $this->pre .= "";
        foreach ($rec as $value){
            $this->pre .= "\"". ucfirst($value) ."\";";
        }
        $this->pre .= "\n";
    }
    
    function addArray($rec){
        $this->text .= "";
        foreach ($rec as $key => $value){
            $this->text .= "\"". $this->prepareText($value) ."\";";
        }
        $this->text .= "\n";
    }

	function prepareText($txt){
    $txt = str_replace("<br/>","",$txt);
    $txt = str_replace("\"","&quot;",$txt);
    $txt = str_replace("\n"," ",$txt);
    $txt = str_replace(";",":",$txt);
		return utf8_decode($txt);
	}
    
    function show(){
        print("<PRE>");
        echo $this->pre;
        echo $this->text;
        echo $this->post;
        print("</PRE>");
    }
    
    function append(){
        $f = fopen($this->name, "a");
        fwrite($f, $this->text);
        fclose($f);
    }
    
    function write(){
        $f = fopen($this->name, "w");
        fwrite($f, $this->pre);
        fwrite($f, $this->text);
        fwrite($f, $this->post);
        fclose($f);
    }

	function output(){
		ini_set('zlib.output_compression','Off' );
		header('Expires: '.gmdate("D, d M Y H:i:s", mktime(date("H"), date("i"), date("s")+1, date("m"), date("d"), date("Y"))).' GMT');
		header('Accept-Ranges: bytes');
		header("Cache-control: private");                  
		header('Pragma: private');
        header('Content-Disposition: attachment; filename="' . $this->name .'"');
		header("Content-Type: text/csv;");
        echo $this->pre;
        echo $this->text;
        echo $this->post;
		exit;
	}
}



?>