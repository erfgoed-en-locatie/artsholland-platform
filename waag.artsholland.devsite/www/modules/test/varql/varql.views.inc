<?php

function varql_views_data() {
  $data['varql']['table']['group']  = t('VARQL');

  $data['varql']['table']['base'] = array(
    'field' =>'vsid',
    'title' => t('VARQL'),
    'query class' => 'varql_query',
    'help' => t('Views SPARQL query builder'),
  );
  
  return $data;
}

function varql_views_data_alter(&$data) {
  $base_tables = array(); 
  foreach($data as $key => $value) {
    if(isset($value['table']['base']) && $key !== 'varql') {
      $base_tables[$key] = $value;
    }
  }
  foreach($base_tables as $bt_key => $bt_value) {
    $entity_info = entity_get_info($bt_key);
    if(isset($entity_info)) {
      foreach($entity_info['bundles'] as $bn_key => $bn_value) {
        $bn_rdf_mapping = $bn_value['rdf_mapping'];
        $new_fields = array();
        foreach ($bn_rdf_mapping as $key => $field ) {
          if($key == 'rdftype')
            continue;
          $group = ucwords("$bt_key ( $bn_key )");  
          $new_field = array(
            'title' => $key,
            'help' => t('RDF mapping: %rdf-mapping', array('%rdf-mapping' => implode(', ', $bn_rdf_mapping[$key]['predicates']))),
            'group' => "VARQL :: $group",
            'field' => array(
              'handler' => 'varql_handler_field',
              'rdf_mapping' => $bn_rdf_mapping[$key],
            ),
          );
          $new_fields[$bt_key.'_'.$bn_key.'_'.$key] = $new_field;
        }
        $data['varql'] = array_merge($data['varql'], $new_fields);  
      }
    }
  }  
}

/**
 * Implementation of hook_views_plugins().
 */
function varql_views_plugins() {
  return array(
    'query' => array(
      'varql_query' => array(
        'title' => t('VARQL Query'),
        'help' => t('SPARQL Query'),
        'handler' => 'varql_plugin_query_sparql'
      ),
    ),
  );
}
