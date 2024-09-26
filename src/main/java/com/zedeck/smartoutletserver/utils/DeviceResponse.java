package com.zedeck.smartoutletserver.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeviceResponse {

    private Long id;
    private String deviceName;
    private String deviceUuid;
    private String uuid;
    private boolean state;
    private boolean registered;
    private double timer;
    private String startTime;
    private String endTime;
    private String groupName;
    private String groupUuid;
    private List<SensorReadingsResponse> sensorReadings; // Updated field

}
