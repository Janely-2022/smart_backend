#ifndef SENSOR_MODEL_H
#define SENSOR_MODEL_H

#include "esp_ble_mesh_config_model_api.h"
#include "esp_ble_mesh_sensor_model_api.h"

#define SENSOR_TAG                  "SENSOR_MODEL"
#define ON_JUST_GET_SENSOR_TAG      "JUST ON REQUEST SENSOR VALUES"
#define GET_SENSOR_TAG              "READY TO GET SENSOR DATA"
#define SENSOR_DATA_TAG             "SENSOR_DATA"

// BLE Mesh Sensor Property IDs
#define SENSOR_PROPERTY_ID_0        0x0004    							// Present Output Current
#define SENSOR_PROPERTY_ID_1        0x0005    							// Present Output Voltage
#define SENSOR_PROPERTY_ID_2        0x0083    							// Present Output Energy
#define SENSOR_PROPERTY_ID_3        0x0084                              // Total Energy Consumed

#define SENSOR_POSITIVE_TOLERANCE   ESP_BLE_MESH_SENSOR_UNSPECIFIED_POS_TOLERANCE
#define SENSOR_NEGATIVE_TOLERANCE   ESP_BLE_MESH_SENSOR_UNSPECIFIED_NEG_TOLERANCE
#define SENSOR_SAMPLE_FUNCTION      ESP_BLE_MESH_SAMPLE_FUNC_UNSPECIFIED
#define SENSOR_MEASURE_PERIOD       ESP_BLE_MESH_SENSOR_NOT_APPL_MEASURE_PERIOD
#define SENSOR_UPDATE_INTERVAL      ESP_BLE_MESH_SENSOR_NOT_APPL_UPDATE_INTERVAL

extern esp_ble_mesh_sensor_state_t      sensor_states[4];
extern esp_ble_mesh_sensor_srv_t        sensor_server;
extern esp_ble_mesh_sensor_setup_srv_t  sensor_setup_server;

void example_ble_mesh_send_sensor_status(esp_ble_mesh_sensor_server_cb_param_t *param);
void example_ble_mesh_send_sensor_series_status(esp_ble_mesh_sensor_server_cb_param_t *param);
void example_ble_mesh_send_sensor_column_status(esp_ble_mesh_sensor_server_cb_param_t *param);
void example_ble_mesh_send_sensor_cadence_status(esp_ble_mesh_sensor_server_cb_param_t *param);
void example_ble_mesh_send_sensor_setting_status(esp_ble_mesh_sensor_server_cb_param_t *param);
void example_ble_mesh_send_sensor_settings_status(esp_ble_mesh_sensor_server_cb_param_t *param);
void example_ble_mesh_send_sensor_descriptor_status(esp_ble_mesh_sensor_server_cb_param_t *param);

void example_ble_mesh_sensor_server_cb(esp_ble_mesh_sensor_server_cb_event_t event, esp_ble_mesh_sensor_server_cb_param_t *param);


#endif //SENSOR_MODEL_H