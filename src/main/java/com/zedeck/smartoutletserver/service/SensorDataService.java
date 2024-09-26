package com.zedeck.smartoutletserver.service;
import com.zedeck.smartoutletserver.dto.SensorDataDto;
import com.zedeck.smartoutletserver.model.SensorData;
import com.zedeck.smartoutletserver.utils.Response;

import java.util.List;


public interface SensorDataService {
    Response<SensorData> saveSensorData(SensorDataDto sensorDataDto);
    Response<SensorData> findSensorByUuid(String uuid);
    Response<List<SensorData>> findAllSensors();
}
