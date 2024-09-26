#ifndef HLW8032_H
#define HLW8032_H

#define HLW8032_TAG "HLW8032"

// UART Configuration
#define TXD_PIN                     (UART_PIN_NO_CHANGE)
#define RXD_PIN                     (UART_PIN_NO_CHANGE)
#define ECHO_TEST_RTS               (UART_PIN_NO_CHANGE)
#define ECHO_TEST_CTS               (UART_PIN_NO_CHANGE)
#define LED_GPIO_PIN                (GPIO_NUM_6)
#define UART_PORT_NUM               (UART_NUM_0)
#define UART_BAUD_RATE              (4800)
#define RX_TASK_STACK_SIZE          (4096)
#define BUF_SIZE                    (256)
#define PACKET_LENGTH               (24)
#define MAX_FX_ISSUES               (0)    								// max tolerable FX issues out of a total of 4 param overflow issues

// HLW8032 Sensor Data Structure
typedef struct {
    float Vrms;
    float Irms;
    float P_act;
    float P_factor;
    float energyConsum_kWh;
} HLW8032_values_t;

// HLW8032 State Machine for Sensor Task
typedef enum {
    STATE_WAITING,
    STATE_CHECK_REG,
    STATE_JUST_RECEIVE,
    STATE_CHECKSUM,
    STATE_PROCESS
} HLW8032_states_e;

void receiver_task(void *pvParameters);
void resetAdd_or_log(HLW8032_values_t hlw8032_value);
HLW8032_values_t getSensorValue(void);


#endif // HLW8032_H