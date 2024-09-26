package com.zedeck.smartoutletserver.dto;

import com.zedeck.smartoutletserver.model.Devices;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GroupDto {
    private String groupName;
    private String startTime;
    private String endTime;

    private List<Devices>  devicesList;
}
