package com.zedeck.smartoutletserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SheduleDto {

    private String deviceUuid;
    private String groupUuid;
    private String startTime;
    private String endTime;
}
