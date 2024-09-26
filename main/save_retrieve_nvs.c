#include "general_header.h"


void save_provisioning_data(uint16_t net_idx, uint16_t addr, uint8_t flags, uint32_t iv_index)
{
    nvs_handle_t nvs_handle;
    esp_err_t err;

    err = nvs_open("storage", NVS_READWRITE, &nvs_handle);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_PROVISION_TAG, "Error opening NVS handle!");
        return;
    }

    err = nvs_set_u16(nvs_handle, "net_idx", net_idx);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_PROVISION_TAG, "Error setting net_idx in NVS!");
    }

    err = nvs_set_u16(nvs_handle, "addr", addr);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_PROVISION_TAG, "Error setting addr in NVS!");
    }

    err = nvs_set_u8(nvs_handle, "flags", flags);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_PROVISION_TAG, "Error setting flags in NVS!");
    }

    err = nvs_set_u32(nvs_handle, "iv_index", iv_index);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_PROVISION_TAG, "Error setting iv_index in NVS!");
    }

    err = nvs_commit(nvs_handle);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_PROVISION_TAG, "Error committing NVS!");
    }
    nvs_close(nvs_handle);
}

bool load_provisioning_data(uint16_t *net_idx, uint16_t *addr, uint8_t *flags, uint32_t *iv_index)
{
    nvs_handle_t nvs_handle;
    esp_err_t err;

    err = nvs_open("storage", NVS_READONLY, &nvs_handle);
    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_PROVISION_TAG, "Error opening NVS handle!");
        return false;
    }

    err = nvs_get_u16(nvs_handle, "net_idx", net_idx);
    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_PROVISION_TAG, "Error getting net_idx from NVS!");
        nvs_close(nvs_handle);
        return false;
    }

    err = nvs_get_u16(nvs_handle, "addr", addr);
    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_PROVISION_TAG, "Error getting addr from NVS!");
        nvs_close(nvs_handle);
        return false;
    }

    err = nvs_get_u8(nvs_handle, "flags", flags);
    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_PROVISION_TAG, "Error getting flags from NVS!");
        nvs_close(nvs_handle);
        return false;
    }

    err = nvs_get_u32(nvs_handle, "iv_index", iv_index);
    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_PROVISION_TAG, "Error getting iv_index from NVS!");
        nvs_close(nvs_handle);
        return false;
    }
    nvs_close(nvs_handle);
    return true;
}

void load_appkey_from_nvs()
{
    nvs_handle_t nvs_handle;
    esp_err_t err;
    uint8_t app_key[16];
    uint16_t net_idx, app_idx;
    size_t required_size = sizeof(app_key);

    err = nvs_open("storage", NVS_READONLY, &nvs_handle);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_APP_KEYS_TAG, "Error opening NVS handle!");
        return;
    }
    err = nvs_get_blob(nvs_handle, "app_key", app_key, &required_size);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_APP_KEYS_TAG, "Error getting AppKey from NVS!");
    }
    err = nvs_get_u16(nvs_handle, "net_idx", &net_idx);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_APP_KEYS_TAG, "Error getting net_idx from NVS!");
    }
    err = nvs_get_u16(nvs_handle, "app_idx", &app_idx);
    if (err != ESP_OK) {
        ESP_LOGE(SAVE_APP_KEYS_TAG, "Error getting app_idx from NVS!");
    }
    nvs_close(nvs_handle);

    // Use the retrieved data to initialize the BLE Mesh stack
    // esp_ble_mesh_provisioning_and_configuration(app_key, net_idx, app_idx);
}

void save_appkey_to_nvs(const uint8_t *app_key, uint16_t net_idx, uint16_t app_idx)
{
    nvs_handle_t nvs_handle;
    esp_err_t err;

    err = nvs_open("storage", NVS_READWRITE, &nvs_handle);
    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_APP_KEYS_TAG, "Error opening NVS handle!");
        return;
    }

    err = nvs_set_blob(nvs_handle, "app_key", app_key, 16);

    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_APP_KEYS_TAG, "Error setting AppKey in NVS!");

    }

    err = nvs_set_u16(nvs_handle, "net_idx", net_idx);

    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_APP_KEYS_TAG, "Error setting net_idx in NVS!");

    }

    err = nvs_set_u16(nvs_handle, "app_idx", app_idx);

    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_APP_KEYS_TAG, "Error setting app_idx in NVS!");
    }

    err = nvs_commit(nvs_handle);

    if (err != ESP_OK) {
        ESP_LOGE(RETRIEVE_APP_KEYS_TAG, "Error committing NVS!");
    }

    nvs_close(nvs_handle);
}





/*

esp_err_t init_nvs() {
    esp_err_t ret;
    int retry_count = 0;

    while (retry_count < NVS_INIT_RETRY) {
        ret = nvs_flash_init();

        if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND) {
            ESP_LOGW(NVS_LOG_TAG, "NVS partition error, erasing and retrying...");
            ESP_ERROR_CHECK(nvs_flash_erase());
            ret = nvs_flash_init();
        }

        if (ret == ESP_OK) {
            ESP_LOGI(NVS_LOG_TAG, "NVS initialized successfully");
            break;
        }

        retry_count++;
        ESP_LOGW(NVS_LOG_TAG, "NVS initialization failed, retrying... (%d/%d)", retry_count, NVS_INIT_RETRY);
        vTaskDelay(1000 / portTICK_PERIOD_MS); // Delay 1 second between retries
    }

    if (ret != ESP_OK) {
        ESP_LOGE(NVS_LOG_TAG, "Failed to initialize NVS after %d retries", NVS_INIT_RETRY);
        return ret;
    }

    return ESP_OK;
}

*/