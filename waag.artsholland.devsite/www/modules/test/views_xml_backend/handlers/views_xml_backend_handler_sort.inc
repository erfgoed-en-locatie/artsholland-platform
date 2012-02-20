<?php
/**
 * @file
 * Base sort handler for views_xml_backend.
 */

class views_xml_backend_handler_sort extends views_handler_sort {
  function option_definition() {
    $options = parent::option_definition();
    $options['xpath_selector'] = array('default' => '');
    return $options;
  }

  function options_form(&$form, &$form_state) {
    parent::options_form($form, $form_state);
    // Get a list of the available fields and arguments for token replacement.
    $options = array();
    foreach ($this->view->display_handler->get_handlers('field') as $field => $handler) {
      $options[$handler->options['xpath_selector']] = $handler->ui_name();
    }
    $form['xpath_selector'] = array(
      '#type' => 'select',
      '#title' => t('Field'),
      '#description' => t('The field name in the table that will be used as the filter.'),
      '#default_value' => $this->options['xpath_selector'],
      '#required' => TRUE,
      '#options' => $options,
    );
  }

  /**
   * Called to add the sort to a query.
   */
  function query() {
    $this->query->add_orderby($this->table_alias, $this->options['xpath_selector'], $this->options['order']);
  }

  function ui_name($short = FALSE) {
    if (!empty($this->options['ui_name'])) {
      $title = check_plain($this->options['ui_name']);
      return $title;
    }
    $title = ($short && isset($this->definition['title short'])) ? $this->definition['title short'] : $this->definition['title'];
    return t('!xpath: !title', array('!xpath' => $this->options['xpath_selector'], '!title' => $title));
  }
}
