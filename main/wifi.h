#ifndef WIFI_H
#define WIFI_H

#define WIFI_TASK_STACK_SIZE 4096

#include <esp_wifi.h>
#include <esp_http_client.h>
#include <esp_netif.h>
#include <freertos/event_groups.h>

// WiFi Configuration
#define WIFI_SSID   "Fuel"
#define WIFI_PASS   "0987654321"

// API endpoint for sending data
#define API_URL     "http://172.17.17.137:8000/analysis/"

void wifi_transm_task(void *pvParameters);


#endif