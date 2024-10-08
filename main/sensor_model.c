#include "general_header.h"
#include "hlw8032.h"

#define SENSOR_TAG "SENSOR_MODEL"

static struct net_buf_simple 	sensor_data_current;
static struct net_buf_simple 	sensor_data_voltage;
static struct net_buf_simple 	sensor_data_power;
static struct net_buf_simple 	sensor_data_energy_consum;

#define SENSOR_POSITIVE_TOLERANCE   ESP_BLE_MESH_SENSOR_UNSPECIFIED_POS_TOLERANCE
#define SENSOR_NEGATIVE_TOLERANCE   ESP_BLE_MESH_SENSOR_UNSPECIFIED_NEG_TOLERANCE
#define SENSOR_SAMPLE_FUNCTION      ESP_BLE_MESH_SAMPLE_FUNC_UNSPECIFIED
#define SENSOR_MEASURE_PERIOD       ESP_BLE_MESH_SENSOR_NOT_APPL_MEASURE_PERIOD
#define SENSOR_UPDATE_INTERVAL      ESP_BLE_MESH_SENSOR_NOT_APPL_UPDATE_INTERVAL

NET_BUF_SIMPLE_DEFINE_STATIC(sensor_data_current,       2);
NET_BUF_SIMPLE_DEFINE_STATIC(sensor_data_voltage,       2);
NET_BUF_SIMPLE_DEFINE_STATIC(sensor_data_power,         2);
NET_BUF_SIMPLE_DEFINE_STATIC(sensor_data_energy_consum, 2);

esp_ble_mesh_sensor_state_t sensor_states[4] = {
    /* Mesh Model Spec:
     * Multiple instances of the Sensor states may be present within the same model,
     * provided that each instance has a unique value of the Sensor Property ID to
     * allow the instances to be differentiated. Such sensors are known as multisensors.
     * In this example, two instances of the Sensor states within the same model are
     * provided.
     */
    [0] = {
        .sensor_property_id                 = SENSOR_PROPERTY_ID_0,
        .descriptor.positive_tolerance      = SENSOR_POSITIVE_TOLERANCE,
        .descriptor.negative_tolerance      = SENSOR_NEGATIVE_TOLERANCE,
        .descriptor.sampling_function       = SENSOR_SAMPLE_FUNCTION,
        .descriptor.measure_period          = SENSOR_MEASURE_PERIOD,
        .descriptor.update_interval         = SENSOR_UPDATE_INTERVAL,
        .sensor_data.format                 = ESP_BLE_MESH_SENSOR_DATA_FORMAT_A,
        .sensor_data.length                 = 0, /* 0 represents the length is 1 */
        .sensor_data.raw_value              = &sensor_data_current,
    },
    [1] = {
        .sensor_property_id                 = SENSOR_PROPERTY_ID_1,
        .descriptor.positive_tolerance      = SENSOR_POSITIVE_TOLERANCE,
        .descriptor.negative_tolerance      = SENSOR_NEGATIVE_TOLERANCE,
        .descriptor.sampling_function       = SENSOR_SAMPLE_FUNCTION,
        .descriptor.measure_period          = SENSOR_MEASURE_PERIOD,
        .descriptor.update_interval         = SENSOR_UPDATE_INTERVAL,
        .sensor_data.format                 = ESP_BLE_MESH_SENSOR_DATA_FORMAT_A,
        .sensor_data.length                 = 0, /* 0 represents the length is 1 */
        .sensor_data.raw_value              = &sensor_data_voltage,
    },
    [2] = {
        .sensor_property_id                 = SENSOR_PROPERTY_ID_2,
        .descriptor.positive_tolerance      = SENSOR_POSITIVE_TOLERANCE,
        .descriptor.negative_tolerance      = SENSOR_NEGATIVE_TOLERANCE,
        .descriptor.sampling_function       = SENSOR_SAMPLE_FUNCTION,
        .descriptor.measure_period          = SENSOR_MEASURE_PERIOD,
        .descriptor.update_interval         = SENSOR_UPDATE_INTERVAL,
        .sensor_data.format                 = ESP_BLE_MESH_SENSOR_DATA_FORMAT_A,
        .sensor_data.length                 = 0, /* 0 represents the length is 1 */
        .sensor_data.raw_value              = &sensor_data_power,
    },
    [3] = {
        .sensor_property_id                 = SENSOR_PROPERTY_ID_3,
        .descriptor.positive_tolerance      = SENSOR_POSITIVE_TOLERANCE,
        .descriptor.negative_tolerance      = SENSOR_NEGATIVE_TOLERANCE,
        .descriptor.sampling_function       = SENSOR_SAMPLE_FUNCTION,
        .descriptor.measure_period          = SENSOR_MEASURE_PERIOD,
        .descriptor.update_interval         = SENSOR_UPDATE_INTERVAL,
        .sensor_data.format                 = ESP_BLE_MESH_SENSOR_DATA_FORMAT_A,
        .sensor_data.length                 = 1, /* 0 represents the length is 1 */
        .sensor_data.raw_value              = &sensor_data_energy_consum,
    },
};

struct example_sensor_descriptor {
    uint16_t sensor_prop_id;
    uint32_t pos_tolerance:12,
             neg_tolerance:12,
             sample_func:8;
    uint8_t  measure_period;
    uint8_t  update_interval;
} __attribute__((packed));

struct example_sensor_setting {
    uint16_t sensor_prop_id;
    uint16_t sensor_setting_prop_id;
} __attribute__((packed));

void example_ble_mesh_send_sensor_descriptor_status(esp_ble_mesh_sensor_server_cb_param_t *param)
{
    struct example_sensor_descriptor descriptor = {0};
    uint8_t *status = NULL;
    uint16_t length = 0;
    esp_err_t err;
    int i;

    status = calloc(1, ARRAY_SIZE(sensor_states) * ESP_BLE_MESH_SENSOR_DESCRIPTOR_LEN);
    if (!status) {
        ESP_LOGE(SENSOR_TAG, "No memory for sensor descriptor status!");
        return;
    }

    if (param->value.get.sensor_descriptor.op_en == false) {
        /* Mesh Model Spec:
         * Upon receiving a Sensor Descriptor Get message with the Property ID field
         * omitted, the Sensor Server shall respond with a Sensor Descriptor Status
         * message containing the Sensor Descriptor states for all sensors within the
         * Sensor Server.
         */
        for (i = 0; i < ARRAY_SIZE(sensor_states); i++) {
            descriptor.sensor_prop_id       = sensor_states[i].sensor_property_id;
            descriptor.pos_tolerance        = sensor_states[i].descriptor.positive_tolerance;
            descriptor.neg_tolerance        = sensor_states[i].descriptor.negative_tolerance;
            descriptor.sample_func          = sensor_states[i].descriptor.sampling_function;
            descriptor.measure_period       = sensor_states[i].descriptor.measure_period;
            descriptor.update_interval      = sensor_states[i].descriptor.update_interval;
            memcpy(status + length, &descriptor, ESP_BLE_MESH_SENSOR_DESCRIPTOR_LEN);
            length                         += ESP_BLE_MESH_SENSOR_DESCRIPTOR_LEN;
        }
        goto send;
    }

    for (i = 0; i < ARRAY_SIZE(sensor_states); i++) {
        if (param->value.get.sensor_descriptor.property_id == sensor_states[i].sensor_property_id) {
            descriptor.sensor_prop_id       = sensor_states[i].sensor_property_id;
            descriptor.pos_tolerance        = sensor_states[i].descriptor.positive_tolerance;
            descriptor.neg_tolerance        = sensor_states[i].descriptor.negative_tolerance;
            descriptor.sample_func          = sensor_states[i].descriptor.sampling_function;
            descriptor.measure_period       = sensor_states[i].descriptor.measure_period;
            descriptor.update_interval      = sensor_states[i].descriptor.update_interval;
            memcpy(status, &descriptor, ESP_BLE_MESH_SENSOR_DESCRIPTOR_LEN);
            length                          = ESP_BLE_MESH_SENSOR_DESCRIPTOR_LEN;
            goto send;
        }
    }

    /* Mesh Model Spec:
     * When a Sensor Descriptor Get message that identifies a sensor descriptor
     * property that does not exist on the element, the Descriptor field shall
     * contain the requested Property ID value and the other fields of the Sensor
     * Descriptor state shall be omitted.
     */
    memcpy(status, &param->value.get.sensor_descriptor.property_id, ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN);
    length = ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN;

send:
    ESP_LOG_BUFFER_HEX("Sensor Descriptor", status, length);

    err = esp_ble_mesh_server_model_send_msg(param->model, &param->ctx,
            ESP_BLE_MESH_MODEL_OP_SENSOR_DESCRIPTOR_STATUS, length, status);
    if (err != ESP_OK) {
        ESP_LOGE(SENSOR_TAG, "Failed to send Sensor Descriptor Status");
    }
    free(status);
}

void example_ble_mesh_send_sensor_cadence_status(esp_ble_mesh_sensor_server_cb_param_t *param)
{
    esp_err_t err;

    /* Sensor Cadence state is not supported currently. */
    err = esp_ble_mesh_server_model_send_msg(param->model, &param->ctx,
            ESP_BLE_MESH_MODEL_OP_SENSOR_CADENCE_STATUS,
            ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN,
            (uint8_t *)&param->value.get.sensor_cadence.property_id);
    if (err != ESP_OK) {
        ESP_LOGE(SENSOR_TAG, "Failed to send Sensor Cadence Status");
    }
}

void example_ble_mesh_send_sensor_settings_status(esp_ble_mesh_sensor_server_cb_param_t *param)
{
    esp_err_t err;

    /* Sensor Setting state is not supported currently. */
    err = esp_ble_mesh_server_model_send_msg(param->model, &param->ctx,
            ESP_BLE_MESH_MODEL_OP_SENSOR_SETTINGS_STATUS,
            ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN,
            (uint8_t *)&param->value.get.sensor_settings.property_id);
    if (err != ESP_OK) {
        ESP_LOGE(SENSOR_TAG, "Failed to send Sensor Settings Status");
    }
}

void example_ble_mesh_send_sensor_setting_status(esp_ble_mesh_sensor_server_cb_param_t *param)
{
    struct example_sensor_setting setting = {0};
    esp_err_t err;

    /* Mesh Model Spec:
     * If the message is sent as a response to the Sensor Setting Get message or
     * a Sensor Setting Set message with an unknown Sensor Property ID field or
     * an unknown Sensor Setting Property ID field, the Sensor Setting Access
     * field and the Sensor Setting Raw field shall be omitted.
     */

    setting.sensor_prop_id = param->value.get.sensor_setting.property_id;
    setting.sensor_setting_prop_id = param->value.get.sensor_setting.setting_property_id;

    err = esp_ble_mesh_server_model_send_msg(param->model, &param->ctx,
            ESP_BLE_MESH_MODEL_OP_SENSOR_SETTING_STATUS,
            sizeof(setting), (uint8_t *)&setting);
    if (err != ESP_OK) {
        ESP_LOGE(SENSOR_TAG, "Failed to send Sensor Setting Status");
    }
}

static uint16_t example_ble_mesh_get_sensor_data(esp_ble_mesh_sensor_state_t *state, uint8_t *data)
{
    uint8_t mpid_len = 0, data_len = 0;
    uint32_t mpid = 0;

    if (state == NULL || data == NULL) {
        ESP_LOGE(SENSOR_TAG, "%s, Invalid parameter", __func__);
        return 0;
    }

    if (state->sensor_data.length == ESP_BLE_MESH_SENSOR_DATA_ZERO_LEN) {
        /* For zero-length sensor data, the length is 0x7F, and the format is Format B. */
        mpid = ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID(state->sensor_data.length, state->sensor_property_id);
        mpid_len = ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID_LEN;
        data_len = 0;
    } else {
        if (state->sensor_data.format == ESP_BLE_MESH_SENSOR_DATA_FORMAT_A) {
            mpid = ESP_BLE_MESH_SENSOR_DATA_FORMAT_A_MPID(state->sensor_data.length, state->sensor_property_id);
            mpid_len = ESP_BLE_MESH_SENSOR_DATA_FORMAT_A_MPID_LEN;
        } else {
            mpid = ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID(state->sensor_data.length, state->sensor_property_id);
            mpid_len = ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID_LEN;
        }
        /* Use "state->sensor_data.length + 1" because the length of sensor data is zero-based. */
        data_len = state->sensor_data.length + 1;
    }

    memcpy(data, &mpid, mpid_len);
    memcpy(data + mpid_len, state->sensor_data.raw_value->data, data_len);

    return (mpid_len + data_len);
}

void example_ble_mesh_send_sensor_status(esp_ble_mesh_sensor_server_cb_param_t *param)
{
    uint8_t *status     = NULL;
    uint16_t buf_size   = 0;
    uint16_t length     = 0;
    uint32_t mpid       = 0;
    esp_err_t err;
    int i;

    /**
     * Sensor Data state from Mesh Model Spec
     * |--------Field--------|-Size (octets)-|------------------------Notes-------------------------|
     * |----Property ID 1----|-------2-------|--ID of the 1st device property of the sensor---------|
     * |-----Raw Value 1-----|----variable---|--Raw Value field defined by the 1st device property--|
     * |----Property ID 2----|-------2-------|--ID of the 2nd device property of the sensor---------|
     * |-----Raw Value 2-----|----variable---|--Raw Value field defined by the 2nd device property--|
     * | ...... |
     * |----Property ID n----|-------2-------|--ID of the nth device property of the sensor---------|
     * |-----Raw Value n-----|----variable---|--Raw Value field defined by the nth device property--|
     */
    for (i = 0; i < ARRAY_SIZE(sensor_states); i++) {
        esp_ble_mesh_sensor_state_t *state = &sensor_states[i];
        if (state->sensor_data.length == ESP_BLE_MESH_SENSOR_DATA_ZERO_LEN) {
            buf_size += ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID_LEN;
        } else {
            /* Use "state->sensor_data.length + 1" because the length of sensor data is zero-based. */
            if (state->sensor_data.format == ESP_BLE_MESH_SENSOR_DATA_FORMAT_A) {
                buf_size += ESP_BLE_MESH_SENSOR_DATA_FORMAT_A_MPID_LEN + state->sensor_data.length + 1;
            } else {
                buf_size += ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID_LEN + state->sensor_data.length + 1;
            }
        }
    }

    status = calloc(1, buf_size);
    if (!status) {
        ESP_LOGE(SENSOR_TAG, "No memory for sensor status!");
        return;
    }

    if (param->value.get.sensor_data.op_en == false) {
        /* Mesh Model Spec:
         * If the message is sent as a response to the Sensor Get message, and if the
         * Property ID field of the incoming message is omitted, the Marshalled Sensor
         * Data field shall contain data for all device properties within a sensor.
         */
        for (i = 0; i < ARRAY_SIZE(sensor_states); i++) {
            length += example_ble_mesh_get_sensor_data(&sensor_states[i], status + length);
        }
        goto send;
    }

    /* Mesh Model Spec:
     * Otherwise, the Marshalled Sensor Data field shall contain data for the requested
     * device property only.
     */
    for (i = 0; i < ARRAY_SIZE(sensor_states); i++) {
        if (param->value.get.sensor_data.property_id == sensor_states[i].sensor_property_id) {
            length = example_ble_mesh_get_sensor_data(&sensor_states[i], status);
            goto send;
        }
    }

    /* Mesh Model Spec:
     * Or the Length shall represent the value of zero and the Raw Value field shall
     * contain only the Property ID if the requested device property is not recognized
     * by the Sensor Server.
     */
    mpid = ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID(ESP_BLE_MESH_SENSOR_DATA_ZERO_LEN,
            param->value.get.sensor_data.property_id);
    memcpy(status, &mpid, ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID_LEN);
    length = ESP_BLE_MESH_SENSOR_DATA_FORMAT_B_MPID_LEN;

send:
    ESP_LOG_BUFFER_HEX(SENSOR_DATA_TAG, status, length);

    err = esp_ble_mesh_server_model_send_msg(param->model, &param->ctx,
            ESP_BLE_MESH_MODEL_OP_SENSOR_STATUS, length, status);
    if (err != ESP_OK) {
        ESP_LOGE(SENSOR_TAG, "Failed to send Sensor Status");
    }
    free(status);
}

void example_ble_mesh_send_sensor_column_status(esp_ble_mesh_sensor_server_cb_param_t *param)
{
    uint8_t *status = NULL;
    uint16_t length = 0;
    esp_err_t err;

    length = ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN +param->value.get.sensor_column.raw_value_x->len;

    status = calloc(1, length);
    if (!status) {
        ESP_LOGE(SENSOR_TAG, "No memory for sensor column status!");
        return;
    }

    memcpy(status, &param->value.get.sensor_column.property_id, ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN);
    memcpy(status + ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN, param->value.get.sensor_column.raw_value_x->data,
        param->value.get.sensor_column.raw_value_x->len);

    err = esp_ble_mesh_server_model_send_msg(param->model, &param->ctx,
            ESP_BLE_MESH_MODEL_OP_SENSOR_COLUMN_STATUS, length, status);
    if (err != ESP_OK) {
        ESP_LOGE(SENSOR_TAG, "Failed to send Sensor Column Status");
    }
    free(status);
}

void example_ble_mesh_send_sensor_series_status(esp_ble_mesh_sensor_server_cb_param_t *param)
{
    esp_err_t err;

    err = esp_ble_mesh_server_model_send_msg(param->model, &param->ctx,
            ESP_BLE_MESH_MODEL_OP_SENSOR_SERIES_STATUS,
            ESP_BLE_MESH_SENSOR_PROPERTY_ID_LEN,
            (uint8_t *)&param->value.get.sensor_series.property_id);
    if (err != ESP_OK) {
        ESP_LOGE(SENSOR_TAG, "Failed to send Sensor Column Status");
    }
}

void example_ble_mesh_sensor_server_cb(esp_ble_mesh_sensor_server_cb_event_t event, esp_ble_mesh_sensor_server_cb_param_t *param)
{
    ESP_LOGI(ON_JUST_GET_SENSOR_TAG, "Sensor server, event %d, src 0x%04x, dst 0x%04x, model_id 0x%04x",
        event, param->ctx.addr, param->ctx.recv_dst, param->model->model_id);

    switch (event) {
    case ESP_BLE_MESH_SENSOR_SERVER_RECV_GET_MSG_EVT:
        switch (param->ctx.recv_op) {
            case ESP_BLE_MESH_MODEL_OP_SENSOR_DESCRIPTOR_GET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_DESCRIPTOR_GET");
                example_ble_mesh_send_sensor_descriptor_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_CADENCE_GET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_CADENCE_GET");
                example_ble_mesh_send_sensor_cadence_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_SETTINGS_GET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_SETTINGS_GET");
                example_ble_mesh_send_sensor_settings_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_SETTING_GET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_SETTINGS_GET");
                example_ble_mesh_send_sensor_setting_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_GET:
                ESP_LOGI(GET_SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_GET");
                example_ble_mesh_send_sensor_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_COLUMN_GET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_COLUMN_GET");
                example_ble_mesh_send_sensor_column_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_SERIES_GET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_SERIES_GET");
                example_ble_mesh_send_sensor_series_status(param);
                break;
            default:
                ESP_LOGE(SENSOR_TAG, "Unknown Sensor Get opcode 0x%04" PRIx32, param->ctx.recv_op);
                return;
        }
        break;
    case ESP_BLE_MESH_SENSOR_SERVER_RECV_SET_MSG_EVT:
        switch (param->ctx.recv_op) {
            case ESP_BLE_MESH_MODEL_OP_SENSOR_CADENCE_SET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_CADENCE_SET");
                example_ble_mesh_send_sensor_cadence_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_CADENCE_SET_UNACK:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_CADENCE_SET_UNACK");
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_SETTING_SET:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_SETTING_SET");
                example_ble_mesh_send_sensor_setting_status(param);
                break;
            case ESP_BLE_MESH_MODEL_OP_SENSOR_SETTING_SET_UNACK:
                ESP_LOGI(SENSOR_TAG, "ESP_BLE_MESH_MODEL_OP_SENSOR_SETTING_SET_UNACK");
                break;
            default:
                ESP_LOGE(SENSOR_TAG, "Unknown Sensor Set opcode 0x%04" PRIx32, param->ctx.recv_op);
                break;
            }
            break;
        default:
            ESP_LOGE(SENSOR_TAG, "Unknown Sensor Server event %d", event);
            break;
    }
}

void resetAdd_or_log(HLW8032_values_t hlw8032_value)
{
    printf("\tVoltage:\t\t%.1f V\n", hlw8032_value.Vrms);
    printf("\tCurrent:\t\t%.4f A\n", hlw8032_value.Irms);
    printf("\tPower(active):\t\t%f W\n", hlw8032_value.P_act);
    printf("\n\tEnergy Consumed:\t%.8lf kWh\n", hlw8032_value.energyConsum_kWh);
    printf("\tLoad's Energy Consumption Efficiency:\t%.4f%%\n\n\n\n", hlw8032_value.P_factor * 100);

    net_buf_simple_reset(&sensor_data_current);
	net_buf_simple_reset(&sensor_data_voltage);
	net_buf_simple_reset(&sensor_data_power);
    net_buf_simple_reset(&sensor_data_energy_consum);

	net_buf_simple_add_u8(&sensor_data_current, hlw8032_value.Irms);
	net_buf_simple_add_u8(&sensor_data_voltage, hlw8032_value.Vrms);
	net_buf_simple_add_u8(&sensor_data_power, hlw8032_value.P_act);
    net_buf_simple_add_u8(&sensor_data_energy_consum, hlw8032_value.energyConsum_kWh);
}