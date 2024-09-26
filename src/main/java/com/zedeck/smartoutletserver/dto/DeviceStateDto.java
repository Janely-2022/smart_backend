package com.zedeck.smartoutletserver.dto;


import jakarta.persistence.NamedEntityGraph;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NamedEntityGraph
@Getter
@Setter
public class DeviceStateDto {

    private String deviceUuid;
    private String groupUuid;
    private boolean state;

    public DeviceStateDto() {

    }
}
