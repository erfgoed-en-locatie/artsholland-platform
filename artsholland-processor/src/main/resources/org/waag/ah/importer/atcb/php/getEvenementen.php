<?php
require_once 'items.php';
require_once 'csv.php';

error_reporting(E_ALL ^E_NOTICE);
set_time_limit(1800); //Time limit of half an hour! (Which is pretty high...)
ini_set('memory_limit', '256M');

/*
Activiteiten en excursies           2.11.* 
Eten en drinken                     3.1.* en 3.2.*
Uit in Amsterdam                    2.1.3, 3.3.1, 3.3.2, 4.3.5
Musea en galleries                  2.1.6 en 2.2.4
Tentoonstellingen                   2.3.14
Attracties en bezienswaardigheden   2.1.* en 2.2.*
Festivals                           2.4
Theater                             2.9.*
Shoppen in Amsterdam                4.6.*
Evenementen                         2.3.* en 2.4.*
*/

$items = new Items();
$items->setDebug(false);
$items->setOutput("Evenementen");
$items->setTypeStart("2.3.");
$items->setAppend(false);
$items->getItems();
$items->saveToCSV();
$items->saveToXML(false);

$items = new Items();
$items->setDebug(false);
$items->setOutput("Evenementen");
$items->setTypeStart("2.4.");
$items->setAppend(true);
$items->getItems();
$items->saveToCSV();
$items->saveToXML(true);

?>
