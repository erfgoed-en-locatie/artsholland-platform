<?php

class wsviews_plugin_query_webservice extends views_plugin_query {

  function init($base_table, $base_field, $options) {
    parent::init($base_table, $base_field, $options);
    $this->base_table = $base_table;
    $this->base_field = $base_field;
    $this->unpack_options($this->options, $options);
  }

  function build(&$view) {
//     $view->init_pager();
//     $this->pager->query();
//     dd($view->display_handler->default_display->options['webservice']);
    $view->build_info['query'] = $this->query();
    $view->build_info['count_query'] = '';
    $view->build_info['query_args'] = array();
  }

  function add_field($table, $field, $alias = '', $params = array()) {
    $alias = $field;

//     dd($field, "FIELD");

    $field_info = array(
        'field' => $field,
        'table' => $table,
        'alias' => $field,
        'path' => explode(' : ', $field),
    ) + $params;

    if (empty($this->fields[$field])) {
      $this->fields[$field] = $field_info;
    }

    return $field;
  }

//   function query($get_count = FALSE) {
//   }


  function execute(&$view) {
    $start = microtime();

    $service_opts = $view->display['default']->handler->options['webservice'];
    $service = wsclient_service_load($service_opts['endpoint']);

//     dd($service->entities());
//     foreach (entity_load_multiple_by_name('wsclient_service', FALSE) as $name => $service) {
//       dd($service->entities());
//     }

    try {
      $result = $service->invoke($service_opts['request'], array());
      $view->result = $this->process_result($result);
    } catch (WSClientException $e) {
      dd($e->getMessage(), "ERROR");
    }
  }

  private function process_result($result, $data = array(), $stack = array()) {
    if (is_object($result)) {
      foreach(array_keys(get_object_vars($result)) as $type) {
        $stack[] = $type;
        $data = $this->process_result($result->$type, $data, $stack);
        $type = array_pop($stack);
      }
    } elseif (is_array($result)) {
      foreach ($result as $idx => $row) {
        $data[] = $this->process_result($row, array(), $stack);
      }
    } else {
      $data[implode(' : ', $stack)] = $result;
    }
    return $data;
  }
}
