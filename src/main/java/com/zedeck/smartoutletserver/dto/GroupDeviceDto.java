package com.zedeck.smartoutletserver.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GroupDeviceDto {
    private Long groupId;
    private Long deviceId;
}
