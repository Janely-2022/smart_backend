#include "general_header.h"



static void example_change_led_state(esp_ble_mesh_model_t *model, esp_ble_mesh_msg_ctx_t *ctx, uint8_t onoff)
{
    uint16_t primary_addr = esp_ble_mesh_get_primary_element_address();
    uint8_t elem_count = esp_ble_mesh_get_element_count();
    struct _led_state *led = NULL;
    uint8_t i;

    if (ESP_BLE_MESH_ADDR_IS_UNICAST(ctx->recv_dst)) {
        for (i = 0; i < elem_count; i++) {
            if (ctx->recv_dst == (primary_addr + i)) {
                led = &led_state[i];
                board_led_operation(led->pin, onoff);
            }
        }
    } else if (ESP_BLE_MESH_ADDR_IS_GROUP(ctx->recv_dst)) {
        if (esp_ble_mesh_is_model_subscribed_to_group(model, ctx->recv_dst)) {
            led = &led_state[model->element->element_addr - primary_addr];
            board_led_operation(led->pin, onoff);
        }
    } else if (ctx->recv_dst == 0xFFFF) {
        led = &led_state[model->element->element_addr - primary_addr];
        board_led_operation(led->pin, onoff);
    }
}

static void example_handle_gen_onoff_msg(esp_ble_mesh_model_t *model, esp_ble_mesh_msg_ctx_t *ctx, esp_ble_mesh_server_recv_gen_onoff_set_t *set)
{
    esp_ble_mesh_gen_onoff_srv_t *srv = model->user_data;

    switch (ctx->recv_op) {
        case ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_GET:
            esp_ble_mesh_server_model_send_msg(model, ctx,
                ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_STATUS, sizeof(srv->state.onoff), &srv->state.onoff);
            break;

        case ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_SET:

        case ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_SET_UNACK:
            if (set->op_en == false) {
                srv->state.onoff = set->onoff;
            } else {
                /* TODO: Delay and state transition */
                srv->state.onoff = set->onoff;
            }
            if (ctx->recv_op == ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_SET) {
                esp_ble_mesh_server_model_send_msg(model, ctx,
                    ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_STATUS, sizeof(srv->state.onoff), &srv->state.onoff);
            }
            esp_ble_mesh_model_publish(model, ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_STATUS,
                sizeof(srv->state.onoff), &srv->state.onoff, ROLE_NODE);
            example_change_led_state(model, ctx, srv->state.onoff);
            break;
            
        default:
            break;
    }
}

void example_ble_mesh_generic_server_cb(esp_ble_mesh_generic_server_cb_event_t event, esp_ble_mesh_generic_server_cb_param_t *param)
{
    esp_ble_mesh_gen_onoff_srv_t *srv;
    ESP_LOGI(ON_JUST_TURN_TAG, "event 0x%02x, opcode 0x%04" PRIx32 ", src 0x%04x, dst 0x%04x",
        event, param->ctx.recv_op, param->ctx.addr, param->ctx.recv_dst);

    switch (event) {
        case ESP_BLE_MESH_GENERIC_SERVER_STATE_CHANGE_EVT:
            ESP_LOGI(ONOFF_TAG, "ESP_BLE_MESH_GENERIC_SERVER_STATE_CHANGE_EVT");
            if (param->ctx.recv_op == ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_SET ||
                param->ctx.recv_op == ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_SET_UNACK) {
                ESP_LOGI(ONOFF_TAG, "onoff 0x%02x", param->value.state_change.onoff_set.onoff);
                example_change_led_state(param->model, &param->ctx, param->value.state_change.onoff_set.onoff);
            }
            break;

        case ESP_BLE_MESH_GENERIC_SERVER_RECV_GET_MSG_EVT:
            ESP_LOGI(ONOFF_TAG, "ESP_BLE_MESH_GENERIC_SERVER_RECV_GET_MSG_EVT");
            if (param->ctx.recv_op == ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_GET) {
                srv = param->model->user_data;
                ESP_LOGI(ONOFF_TAG, "onoff 0x%02x", srv->state.onoff);
                example_handle_gen_onoff_msg(param->model, &param->ctx, NULL);
            }
            break;

        case ESP_BLE_MESH_GENERIC_SERVER_RECV_SET_MSG_EVT:
            ESP_LOGI(ON_TURNING_TAG, "ESP_BLE_MESH_GENERIC_SERVER_RECV_SET_MSG_EVT");
            if (param->ctx.recv_op == ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_SET ||
                param->ctx.recv_op == ESP_BLE_MESH_MODEL_OP_GEN_ONOFF_SET_UNACK) {
                ESP_LOGI(ON_TURNING_TAG, "onoff 0x%02x, tid 0x%02x", param->value.set.onoff.onoff, param->value.set.onoff.tid);
                if (param->value.set.onoff.op_en) {
                    ESP_LOGI(ON_TURNING_TAG, "trans_time 0x%02x, delay 0x%02x",
                        param->value.set.onoff.trans_time, param->value.set.onoff.delay);
                }
                example_handle_gen_onoff_msg(param->model, &param->ctx, &param->value.set.onoff);
            }
            break;

        default:
            ESP_LOGE(ONOFF_TAG, "Unknown Generic Server event 0x%02x", event);
            break;
    }
}