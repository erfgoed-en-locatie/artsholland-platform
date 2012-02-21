<?php

function wsviews_element_validate_service($element, &$form_state) {
  $services = array_keys(entity_load_multiple_by_name('wsclient_service', FALSE));
  $value = $element['#value'];
  if ($value == '' || !in_array($value, $services)) {
    form_error($element, t('You must select a webservice endpoint from the list.'));
  }
}

function wsviews_get_service($name = FALSE) {
  if($name) {
    $name = array($name);
  }
  $service = entity_load_multiple_by_name('wsclient_service', $name);
  return is_array($name) ? $service[$name[0]] : $service;
}

function wsviews_get_operations($name) {
  $operations = array();
  foreach (wsviews_get_service($name)->operations as $name => $op) {
    if ($op['result']['type'] != 'void') {
      $operations[$name] = $name;
    }
  }
  return $operations;
}

function wsviews_get_responsefields($service, $operation) {
  dd($service, "SERVICE");
  $service = wsviews_get_service($service);
  dd($service);//->operations[$operation]);
}