set(srcs        "main.c"
                "wifi.c"
                "board.c"
                "hlw8032.c"
                "ble_mesh.c"
                "provision.c"
                "onoff_model.c"
                "sensor_model.c"
                "save_retrieve_nvs.c"
        )

idf_component_register(SRCS "${srcs}"

                        INCLUDE_DIRS    "."
                        )