package com.zedeck.smartoutletserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorDataDto {

    private Double voltage;
    private Double current;
    private Double power;
    private Double energy;
    private Long deviceId;

}
