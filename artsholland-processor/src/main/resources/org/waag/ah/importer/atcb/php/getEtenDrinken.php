<?php
require_once 'items.php';
require_once 'csv.php';

error_reporting(E_ALL ^E_NOTICE);
set_time_limit(1800); //Time limit of half an hour! (Which is pretty high...)
ini_set('memory_limit', '256M');


$items = new Items();
$items->setDebug(false);
$items->setOutput("EtenDrinken");
$items->setTypeStart("3.1.");
$items->setAppend(false);
$items->getItems();
$items->saveToCSV();
$items->saveToXML(false);

$items = new Items();
$items->setDebug(false);
$items->setOutput("EtenDrinken");
$items->setTypeStart("3.2.");
$items->setAppend(true);
$items->getItems();
$items->saveToCSV();
$items->saveToXML(true);

?>
