<?php

/**
 * Implementation of hook_views_plugins().
 */
function wsviews_views_plugins() {
  return array(
    'query' => array(
      'wsviews_request' => array(
        'title' => t('Webservice Request'),
        'help' => t('Webservice Request (REST/SOAP)'),
        'handler' => 'wsviews_plugin_query_webservice'
      ),
    ),
  );
}

/**
 * Implementation of hook_views_data().
 */
function wsviews_views_data() {
  $data['wsviews_webservice']['table']['group']  = t('Webservice');

  $data['wsviews_webservice']['table']['base'] = array(
    'title' => t('Webservice'),
    'query class' => 'wsviews_request',
    'help' => t('Views webservice query builder'),
  );

  $data['wsviews_webservice']['text'] = array(
    'title' => t('Text'),
    'help' => t('A field from the webservice response message.'),
    'field' => array(
      'handler' => 'wsviews_handler_field',
      'click sortable' => TRUE,
    ),
//     'filter' => array(
//       'handler' => 'views_xml_backend_handler_filter',
//     ),
//     'sort' => array(
//       'handler' => 'views_xml_backend_handler_sort',
//     ),
  );

  return $data;
}
