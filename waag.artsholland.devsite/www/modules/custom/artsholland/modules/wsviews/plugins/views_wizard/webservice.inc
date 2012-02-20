<?php

/**
 * @file
 * Views wizard for node views.
 */
$plugin = array(
  'title' => t('Webservice'),
	'name' => 'wsviews_webservice',
  'base_table' => 'wsviews_webservice',
  'form_wizard_class' => array(
    'file' => 'wsviews_webservice_views_wizard.class.php',
    'class' => 'WebserviceViewsWizard',
  ),
//   'path_field' => array(
//     'id' => 'nid',
//     'table' => 'node',
//     'field' => 'nid',
//     'exclude' => TRUE,
//     'link_to_node' => FALSE,
//     'alter' => array(
//       'alter_text' => 1,
//       'text' => 'node/[nid]',
//     ),
//   ),
);
