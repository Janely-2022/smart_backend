#include "general_header.h"
#include "hlw8032.h"
#include "wifi.h"

static void init_nvs(void);
static void init_blutooth(void);
static void init_ble_mesh(void);
static void create_rx_task(void);
static void create_wifi_task(void);



void app_main(void)
{
    init_nvs();
    board_init();
    init_blutooth();
    ble_mesh_get_dev_uuid(dev_uuid);
    init_ble_mesh();
    create_rx_task();
    create_wifi_task();
    vTaskDelete(NULL);
}



static void init_nvs(void)
{
    esp_err_t err = nvs_flash_init();
    if (err == ESP_ERR_NVS_NO_FREE_PAGES) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        err = nvs_flash_init();
    }
    ESP_ERROR_CHECK(err);
}

static void init_blutooth(void)
{
    esp_err_t err = bluetooth_init();
    if (err) {
        ESP_LOGE("BLUTOOTH", "esp32_bluetooth_init failed (err %d)", err);
        return;
    }
}

static void init_ble_mesh(void)
{
    esp_err_t err = ble_mesh_init();
    if (err) {
        ESP_LOGE("BLE_MESH_INIT", "Bluetooth mesh init failed (err %d)", err);
    }
}

static void create_rx_task(void)
{
    BaseType_t rx_handle;
    rx_handle = xTaskCreatePinnedToCore(receiver_task, "uart_receiver_task", RX_TASK_STACK_SIZE, NULL, 1, NULL, 1);
    if (pdFAIL == rx_handle) {
    	ESP_LOGE(HLW8032_TAG, "Receiver task creation failed!");
    } else {
        ESP_LOGI(HLW8032_TAG, "Receiver task created successfully.");
    }
}


static void create_wifi_task(void)
{
    BaseType_t rx_handle;
    rx_handle = xTaskCreatePinnedToCore(wifi_transm_task, "wifi transmit", WIFI_TASK_STACK_SIZE, NULL, 1, NULL, 0);
    if (pdFAIL == rx_handle) {
    	ESP_LOGE(HLW8032_TAG, "Receiver task creation failed!");
    } else {
        ESP_LOGI(HLW8032_TAG, "Receiver task created successfully.");
    }
}