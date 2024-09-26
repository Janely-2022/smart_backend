package com.zedeck.smartoutletserver.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SensorReadingsResponse {

    private double voltage;
    private double current;
    private double power;
    private double energyConsumed;
}
