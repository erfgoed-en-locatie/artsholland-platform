<?php
/**
 * @file
 * Base field handler for views_xml_backend.
 */

class wsviews_handler_field extends views_handler_field {

  function init(&$view, &$options) {
    parent::init($view, $options);

    $service_opts = $view->display['default']->handler->options['webservice'];
    $this->service = wsviews_get_service($service_opts['endpoint']);
    $this->operation = $this->service->operations[$service_opts['request']];

    module_load_include('inc', 'wsclient', 'wsclient.rules');

    $types = wsclient_rules_data_info();
    $this->responsetype = $types[implode('_', array('wsclient',
      $this->service->name,
      $this->operation['result']['type']))];
  }

  function get_field_names($fieldinfo, $fields = array(), $stack = array()) {
    if (isset($fieldinfo['property info'])) {
      foreach ($fieldinfo['property info'] as $type => $data) {
        $stack[] = $data['label'];
        $fields = $this->get_field_names($data, $fields, $stack);
        array_pop($stack);
      }
    } else {
      $fields[] = implode(' : ', $stack);
    }
    return $fields;
  }

  function render($values) {
    if(!isset($values[$this->field_alias])) {
      return;
    }
    $values = $values[$this->field_alias];
    if (isset($this->options['multiple'])) {
      foreach ($values as $i => $value) {
        $values[$i] = check_plain($value);
      }
      if ($this->options['list_type'] == 'other') {
        return implode(check_plain($this->options['custom_separator']), $values);
      }
      if ($this->options['list_type'] == 'br') {
        return implode('<br />', $values);
      }
      return theme('item_list', array(
        'items' => $values,
        'type' => $this->options['list_type']
      ));
    }
    return check_plain($values);
  }

  function option_definition() {
    $options = parent::option_definition();
    $options['response_field'] = array('default' => '');
    return $options;
  }

  function options_form(&$form, &$form_state) {
    parent::options_form($form, $form_state);
    $form['response_field'] = array(
      '#title' => t('Response field'),
    	'#description' => t('Field from the response object'),
    	'#type' => 'select',
      '#options' => drupal_map_assoc($this->get_field_names($this->responsetype)),
    	'#default_value' => $this->options['response_field'],
    );
  }

  /**
   * Called to add the field to a query.
   */
  function query() {
    // Add the field.
    $this->table_alias = 'xml';

    $this->field_alias = $this->query->add_field(
      $this->table_alias,
      $this->options['response_field'],
      '',
      $this->options
    );
  }

  function ui_name($short = FALSE) {
    if (!empty($this->options['ui_name'])) {
      $title = check_plain($this->options['ui_name']);
      return $title;
    }

    return check_plain($this->options['response_field']);
  }

  /**
   * Called to determine what to tell the clicksorter.
   */
  function click_sort($order) {
    if (isset($this->field_alias)) {
      $this->query->add_orderby(NULL, $this->field_alias, $order);
    }
  }
}
