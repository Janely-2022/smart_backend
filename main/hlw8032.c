#include "ble_mesh.h"
#include "hlw8032.h"



static HLW8032_values_t     hlw8032_value;

static uint8_t rxProcess(uint8_t data);
static void extractProcess(uint8_t* frame);
static bool decideToTakeFX(uint8_t stateReg);
static bool isChecksumValid(const uint8_t* data, uint8_t checksum);


void receiver_task(void *pvParameters)
{
    uart_config_t uart_config = {
        .baud_rate  =  UART_BAUD_RATE,
        .data_bits  =  UART_DATA_8_BITS,
        .parity     =  UART_PARITY_EVEN,
        .stop_bits  =  UART_STOP_BITS_1,
        .flow_ctrl  =  UART_HW_FLOWCTRL_DISABLE,
        .source_clk =  UART_SCLK_DEFAULT
    };
    int intr_alloc_flags = 0;

#if CONFIG_UART_ISR_IN_IRAM
    intr_alloc_flags = ESP_INTR_FLAG_IRAM;
#endif

    ESP_ERROR_CHECK(uart_driver_install(UART_PORT_NUM, BUF_SIZE, 0, 0, NULL, intr_alloc_flags));
    ESP_ERROR_CHECK(uart_param_config(UART_PORT_NUM, &uart_config));
    ESP_ERROR_CHECK(uart_set_pin(UART_PORT_NUM, TXD_PIN, RXD_PIN, ECHO_TEST_RTS, ECHO_TEST_CTS));

    uint8_t *data = (uint8_t*) malloc(PACKET_LENGTH * 2);
	int len;
	uint8_t MS_ToWait 		  = 0;
	uint8_t detectPacketCount = 0;

	for ( ;; ) {
		len = uart_read_bytes(UART_PORT_NUM, data, 1, 50 / portTICK_PERIOD_MS);
		if (len > 0) {
			MS_ToWait = rxProcess(*data);
			if (MS_ToWait > 0) {
				vTaskDelay(MS_ToWait / portTICK_PERIOD_MS);
			} else {
				if (detectPacketCount > 50) {
					vTaskDelay(50/portTICK_PERIOD_MS);
					detectPacketCount = 0;
				}
				++detectPacketCount;
			}
		} else if (len == 0) {
			ESP_LOGW(HLW8032_TAG, "No data received within the timeout period.");
			vTaskDelay(50/portTICK_PERIOD_MS);
		} else {
			ESP_LOGE(HLW8032_TAG, "!!! An ERROR, try fixing: uart read returns -1.");
			vTaskDelay(50/portTICK_PERIOD_MS);
		}
	}
}

static uint8_t rxProcess(uint8_t data)
{
    static uint8_t buffer[24];
    static uint8_t stepIndex= 0;
    static uint8_t checksum = 0;
    static HLW8032_states_e currState = STATE_WAITING;

    switch (currState) {
        case STATE_WAITING:
            if (0x55 == data) {
                buffer[0] = data;
                currState = STATE_CHECK_REG;
            } else if ((data & (uint8_t)0xF0) == (uint8_t)0xF0) {
                buffer[0] = data;
                if (decideToTakeFX(buffer[0])) {
                    currState = STATE_CHECK_REG;
                }
            }
            return 0;

        case STATE_CHECK_REG:
            if (0x5A == data) {
                buffer[1] = data;
                currState = STATE_JUST_RECEIVE;
            } else {
                currState = STATE_WAITING;
            }
            stepIndex = 2;
            return 0;

        case STATE_JUST_RECEIVE:
            if (stepIndex < 23) {
                buffer[stepIndex] = data;
                ++stepIndex;
            } else {
                buffer[23] = checksum = data;
                stepIndex = 2;
                currState = STATE_CHECKSUM;
            }
            return 0;

        case STATE_CHECKSUM:
            if(isChecksumValid(buffer, checksum)) {
                for (int i = 0; i < 24; ++i) {
                    printf("0x%02X, ", buffer[i]);
                }
                printf("\n");
                currState = STATE_PROCESS;
            } else {
                currState = STATE_WAITING;
            }
            return 0;

        case STATE_PROCESS:
            extractProcess(buffer);
            currState = STATE_WAITING;
            return 50;

        default:
            currState = STATE_WAITING;
            return 0;
    }
}



static bool decideToTakeFX(uint8_t stateReg)
{
    bool paramRegUnusable = (stateReg & 1);

    if (paramRegUnusable) {
        return false;
    }
        /*
        ---Accept data if and only if there's NO power parameter register overflow issue
        since this param is used to calc energy consumption regardless current/voltage register overflowed.
        ---Here total energy consumption measurement is prioritized to voltage, power, current and power factor params
        */
    return true;
}


static bool isChecksumValid(const uint8_t* data, uint8_t checksum)
{
    uint8_t calcChecksum = 0;
    for (int i = 2; i < PACKET_LENGTH - 1; i++) {
        calcChecksum += data[i];
    }
    return calcChecksum == checksum;
}


static void extractProcess(uint8_t* frame)
{
	static const float Vcoef      = 1.88;
	static const float Icoef      = 1.0;
	static const float Pcoef      = Vcoef * Icoef;
	static uint32_t pulseCount    = 0;
	static uint8_t prevBit7_DUr   = 0;
	static uint8_t currBit7_DUr   = 0;
	static uint16_t negationTimes = 0;
	static float P_app            = 0;

	uint32_t Vpr = (frame[2]  << 16) | (frame[3]  << 8) | (frame[4]);
	uint32_t Vr  = (frame[5]  << 16) | (frame[6]  << 8) | (frame[7]);
	uint32_t Ipr = (frame[8]  << 16) | (frame[9]  << 8) | (frame[10]);
	uint32_t Ir  = (frame[11] << 16) | (frame[12] << 8) | (frame[13]);
	uint32_t Ppr = (frame[14] << 16) | (frame[15] << 8) | (frame[16]);
	uint32_t Pr  = (frame[17] << 16) | (frame[18] << 8) | (frame[19]);
	uint8_t  DUr = (frame[20]);
	uint16_t PFr = (frame[21] << 8)  | (frame[22]);

	currBit7_DUr = (DUr >> 7) & 0x01;
	if (currBit7_DUr != prevBit7_DUr) {
		negationTimes++;
		prevBit7_DUr = currBit7_DUr;
	}

	pulseCount = (negationTimes * 65536) + PFr;
	uint32_t pulseCountPerKWh   = (uint32_t)((1.0 / ((float)Ppr * (float)Pcoef)) * 1e9 * 3600);
	hlw8032_value.energyConsum_kWh     = (float)pulseCount / (float)pulseCountPerKWh;

	if ((frame[0] = (uint8_t)0xF0) == (uint8_t)0xF0){
		bool voltageRegOverflow = frame[0] & (1 << 3);  // Bit 3: Voltage overflow
		bool currentRegOverflow = frame[0] & (1 << 2);  // Bit 2: Current overflow
		bool powerRegOverflow   = frame[0] & (1 << 1);  // Bit 1: Power overflow

		if (currentRegOverflow | powerRegOverflow) {
			hlw8032_value.Irms      = 0;
			hlw8032_value.P_act     = 0;
			hlw8032_value.P_factor  = 0;
			P_app                   = 0;
			if (!voltageRegOverflow) {
				hlw8032_value.Vrms = ((float)Vpr / Vr) * Vcoef;
			} else {
				hlw8032_value.Vrms = 0;
			}
		} else {
			hlw8032_value.Irms     = ((float)Ipr / Ir) * Icoef;
			hlw8032_value.P_act    = ((float)Ppr / Pr) * Pcoef;
			hlw8032_value.P_factor = (float)hlw8032_value.P_act/P_app;
			P_app                  = hlw8032_value.Vrms * hlw8032_value.Irms;
			if (!voltageRegOverflow) {
				hlw8032_value.Vrms = ((float)Vpr / Vr) * Vcoef;
			} else {
				hlw8032_value.Vrms = 0;
			}
		}
	} else {
		hlw8032_value.Vrms     = ((float)Vpr / Vr) * Vcoef;
		hlw8032_value.Irms     = ((float)Ipr / Ir) * Icoef;
		hlw8032_value.P_act    = ((float)Ppr / Pr) * Pcoef;
		hlw8032_value.P_factor = ((float)hlw8032_value.P_act/P_app);
		P_app    			   = (hlw8032_value.Vrms * hlw8032_value.Irms);
	}
	resetAdd_or_log(hlw8032_value);
}


HLW8032_values_t getSensorValue(void)
{
    return hlw8032_value;
}