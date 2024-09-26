#include "wifi.h"
#include "hlw8032.h"
#include "ble_mesh.h"


static char                         *device_id;
static const int CONNECTED_BIT      = BIT0;

// Event group for Wi-Fi connection
EventGroupHandle_t wifi_event_group;

char *get_device_id(void);
static void wifi_init(void);
static void get_then_send_sensor_data(void);
void send_data_to_server(float voltage, float power, float current, bool gpio_status, float energy_consumption);

void wifi_transm_task(void *pvParameters)
{
    wifi_init();
    device_id = get_device_id();
    printf("Device ID: %s\n", device_id);

    for (;;) {
        get_then_send_sensor_data();
        vTaskDelay(5000/portTICK_PERIOD_MS);
    }
}

// Function to handle Wi-Fi events
static void event_handler(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data)
{
    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_START)
    {
        esp_wifi_connect();
    }
    else if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_DISCONNECTED)
    {
        esp_wifi_connect();
    }
    else if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP)
    {
        xEventGroupSetBits(wifi_event_group, CONNECTED_BIT);
        printf("Connection Successful. Connected to %s\n", WIFI_SSID);
    }
}

// Initialize and connect to Wi-Fi
static void wifi_init(void)
{
    wifi_event_group = xEventGroupCreate();
    esp_netif_init();
    esp_event_loop_create_default();
    esp_netif_create_default_wifi_sta();

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    esp_wifi_init(&cfg);

    esp_event_handler_register(WIFI_EVENT, ESP_EVENT_ANY_ID, &event_handler, NULL);
    esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, &event_handler, NULL);

    wifi_config_t wifi_config = {
        .sta = {
            .ssid = WIFI_SSID,
            .password = WIFI_PASS,
        },
    };
    esp_wifi_set_mode(WIFI_MODE_STA);
    esp_wifi_set_config(WIFI_IF_STA, &wifi_config);
    esp_wifi_start();

    // Wait until connected to Wi-Fi
    xEventGroupWaitBits(wifi_event_group, CONNECTED_BIT, false, true, portMAX_DELAY);
    printf("Connected to Wi-Fi!\n");
}

// Get ESP32 MAC address as device ID
char *get_device_id(void)
{
    uint8_t mac[6];
    esp_wifi_get_mac(WIFI_IF_STA, mac);
    static char device_id[13];
    snprintf(device_id, sizeof(device_id), "%02X%02X%02X%02X%02X%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
    return device_id;
}

static void get_then_send_sensor_data(void)
{
    HLW8032_values_t hlw8032_value = getSensorValue();
    bool gpio_status = gpio_get_level(GPIO_NUM_6);

    float voltage             = hlw8032_value.Vrms;
    float power               = hlw8032_value.P_act;
    float current             = hlw8032_value.Irms;
    float energy_consumption  = hlw8032_value.energyConsum_kWh;

    send_data_to_server(voltage, power, current, gpio_status, energy_consumption);
}

// Send data to the API
void send_data_to_server(float voltage, float power, float current, bool gpio_status, float energy_consumption)
{
    esp_http_client_config_t config = {
        .url = API_URL,
        .method = HTTP_METHOD_POST,
    };
    esp_http_client_handle_t client = esp_http_client_init(&config);

    // JSON formatted data
    char post_data[512];
    snprintf(post_data, sizeof(post_data),
             "{\"device_id\":\"%s\",\"voltage\":%.2f,\"power\":%.2f,\"current\":%.2f,\"gpio_status\":%d,\"energy_consumption\":%.6f}",
             device_id, voltage, power, current, gpio_status, energy_consumption);

    // Send HTTP POST request
    esp_http_client_set_header(client, "Content-Type", "application/json");
    esp_http_client_set_post_field(client, post_data, strlen(post_data));

    esp_err_t err = esp_http_client_perform(client);
    if (err == ESP_OK)
    {
        printf("Data sent successfully!\n");
    }
    else
    {
        printf("Failed to send data: %s\n", esp_err_to_name(err));
    }

    esp_http_client_cleanup(client);
}