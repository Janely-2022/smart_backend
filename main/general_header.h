#ifndef GENERAL_HEADER_H
#define GENERAL_HEADER_H

#include "ble_mesh.h"
#include "onoff_model.h"
#include "sensor_model.h"
#include "save_retrieve_nvs.h"

extern esp_ble_mesh_cfg_srv_t   config_server;

void example_ble_mesh_config_server_cb(esp_ble_mesh_cfg_server_cb_event_t event, esp_ble_mesh_cfg_server_cb_param_t *param);


#endif //GENERAL_HEADER_H