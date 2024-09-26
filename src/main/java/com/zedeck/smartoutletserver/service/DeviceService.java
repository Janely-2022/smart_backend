package com.zedeck.smartoutletserver.service;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.dto.DeviceStateDto;
import com.zedeck.smartoutletserver.dto.GroupDeviceDto;
import com.zedeck.smartoutletserver.dto.SheduleDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.utils.DeviceResponse;
import com.zedeck.smartoutletserver.utils.DeviceResponse2;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface DeviceService {

    Response<Devices>  registerDevice(DeviceDto deviceDto);

    Response<DeviceResponse2>  findDeviceByUuid(String uuid);

    Response<Devices> updateDevice(DeviceDto deviceDto);

    Page<DeviceResponse2> findAllRegisteredDevices(boolean registered, Pageable pageable);

    Page<Devices> findAllActiveDevices(Pageable pageable);

    Boolean findDeviceStateByUuid(String uuid);

    Response<Devices> deleteDeviceByUuid(String uuid);

    Response<Devices> controlDeviceByUuid(DeviceStateDto deviceStateDto);

    Response<Devices> updateDeviceByUuid(String uuid, DeviceDto deviceDto);

    Page<Devices> findDevicesByGroup(Long groupId, Pageable pageable);

    Response<Devices> assignDeviceToGroup(GroupDeviceDto groupDeviceDto);

    Response<Devices> scheduleDevice(SheduleDto sheduleDto);
}
