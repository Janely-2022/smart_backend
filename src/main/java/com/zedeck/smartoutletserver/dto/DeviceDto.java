package com.zedeck.smartoutletserver.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DeviceDto {
    private String deviceName;
    private String deviceUUid;
    private boolean state;
    private String startTime;
    private String endTime;
    private boolean registered;
    private double timer;

    private double voltage;
    private double current;
    private double power;
    private double energyConsumed;
    private String deviceId;
}
