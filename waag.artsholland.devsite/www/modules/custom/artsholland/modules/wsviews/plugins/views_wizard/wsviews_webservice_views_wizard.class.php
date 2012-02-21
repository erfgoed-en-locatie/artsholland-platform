<?php

class WebserviceViewsWizard extends ViewsUiBaseViewsWizard {

  public function build_form($form, &$form_state) {
    $form = parent::build_form($form, $form_state);
    $service_options = array();
    foreach (wsviews_get_service() as $key => $service) {
      if ($service->type == 'soap') {
        $service_options[$key] = $service->label;
      }
    }
    $form['displays']['webservice'] = array(
      '#title' => t('Webservice'),
      '#type' => 'fieldset',
      '#weight' => 0,
    	'#tree' => TRUE,
    );
    $form['displays']['webservice']['settings'] = array(
      '#type' => 'fieldset',
      '#attributes' => array('class' => array('container-inline', 'fieldset-no-legend')),
    );
    $form_ws = &$form['displays']['webservice']['settings'];
    $form_ws['endpoint'] = array(
      '#title' => t('Endpoint'),
      '#type' => 'select',
      '#options' => $service_options,
    	'#element_validate' => array('wsviews_element_validate_service'),
    );
    $endpoint = $form_state['values']['webservice']['settings']['endpoint'];
    if ($endpoint != "") {
      $form_ws['request'] = array(
        '#title' => t('operation'),
        '#type' => 'select',
        '#options' => wsviews_get_operations($endpoint),
      );
    }
    views_ui_add_ajax_trigger($form_ws, 'endpoint', array('displays', 'webservice', 'settings'));
    return $form;
  }

  protected function build_display_options($form, $form_state) {
    $display_options = parent::build_display_options($form, $form_state);
    $display_options['default']['webservice'] = $form_state['values']['webservice']['settings'];
    return $display_options;
  }
}
