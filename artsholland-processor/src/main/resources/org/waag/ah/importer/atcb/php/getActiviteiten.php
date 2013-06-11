<?php
require_once 'items.php';
require_once 'csv.php';

error_reporting(E_ALL ^E_NOTICE);
set_time_limit(1800); //Time limit of half an hour! (Which is pretty high...)
ini_set('memory_limit', '256M');


$items = new Items();
$items->setDebug(false);
$items->setOutput("Activiteiten.csv");
$items->setTypeStart("2.11.");
$items->setAppend(false);
$items->getItems();
$items->saveToCSV();
$items->saveToXML(true);

/*
$items = new Items();
$items->setDebug(true);
$items->setOutput("/home/atcb/domains/atcbopendata.nl/public_html/data/Evenementen.csv");
$items->setTypeStart("2.4.");
$items->setAppend(true);
$items->getItems();
$items->saveToCSV();
*/

?>
