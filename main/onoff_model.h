#ifndef ONOFF_MODEL_H
#define ONOFF_MODEL_H

#include "esp_ble_mesh_config_model_api.h"
#include "esp_ble_mesh_generic_model_api.h"
#include "esp_ble_mesh_local_data_operation_api.h"

#define ONOFF_TAG                       "ONOFF_MODEL"
#define ON_JUST_TURN_TAG                "JUST after TURNING ON/OFF"
#define ON_TURNING_TAG                  "ON TURNING A LOAD"

extern esp_ble_mesh_gen_onoff_srv_t     onoff_server_0;
extern esp_ble_mesh_gen_onoff_srv_t     onoff_server_1;
extern esp_ble_mesh_gen_onoff_srv_t     onoff_server_2;

void example_ble_mesh_generic_server_cb(esp_ble_mesh_generic_server_cb_event_t event, esp_ble_mesh_generic_server_cb_param_t *param);


#endif // ONOFF_MODEL_H