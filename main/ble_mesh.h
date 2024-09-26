#ifndef BLE_MESH_H
#define BLE_MESH_H

#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include <stdlib.h>
#include <stdbool.h>

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>

#include "driver/uart.h"
#include "driver/gpio.h"
#include "esp_log.h"
#include "esp_err.h"

#include "esp_ble_mesh_defs.h"
#include "esp_ble_mesh_common_api.h"
#include "esp_ble_mesh_networking_api.h"
#include "esp_ble_mesh_provisioning_api.h"

#include "board.h"

#define PROVISION_TAG           "PROVISIONING"
#define KEY_BIND_TAG            "KEY BINDING"
#define NODE_DETECT_TAG         "ON DETECTING A NODE IN BLE-MESH"

#define CID_ESP                 0x02E5

extern struct _led_state        led_state[3];
extern uint8_t                  dev_uuid[ESP_BLE_MESH_OCTET16_LEN];
extern esp_ble_mesh_elem_t      elements[];
extern esp_ble_mesh_comp_t      composition;
extern esp_ble_mesh_prov_t      provision;

esp_err_t ble_mesh_init(void);
esp_err_t bluetooth_init(void);

void ble_mesh_get_dev_uuid(uint8_t *dev_uuid);
void example_ble_mesh_provisioning_cb(esp_ble_mesh_prov_cb_event_t event, esp_ble_mesh_prov_cb_param_t *param);

void load_appkey_from_nvs();
void save_appkey_to_nvs(const uint8_t *app_key, uint16_t net_idx, uint16_t app_idx);
void save_provisioning_data(uint16_t net_idx, uint16_t addr, uint8_t flags, uint32_t iv_index);
bool load_provisioning_data(uint16_t *net_idx, uint16_t *addr, uint8_t *flags, uint32_t *iv_index);


#endif //BLE_MESH_H