package com.zedeck.smartoutletserver.service;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.model.SensorReading;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SensorReadindService {

    Response<SensorReading> saveSensorReading(DeviceDto deviceDto);

    Response<SensorReading> updateSensorReading(DeviceDto deviceDto);
    Response<SensorReading> deleteSensorReading(DeviceDto deviceDto);
    Page<SensorReading> getDeviceSensorReading(String  deviceUuid, Pageable pageable);
    Page<SensorReading> getAllDeviceSensorReading(Pageable pageable);
    Page<SensorReading> getSensorReading(String uuid, Pageable pageable);
}
