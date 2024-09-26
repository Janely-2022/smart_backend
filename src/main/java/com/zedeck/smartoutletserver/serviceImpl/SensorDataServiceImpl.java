package com.zedeck.smartoutletserver.serviceImpl;

import com.zedeck.smartoutletserver.dto.SensorDataDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.SensorData;
import com.zedeck.smartoutletserver.repository.DeviceRepository;
import com.zedeck.smartoutletserver.repository.SensorDataRepository;
import com.zedeck.smartoutletserver.service.SensorDataService;
import com.zedeck.smartoutletserver.utils.Response;
import com.zedeck.smartoutletserver.utils.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorDataServiceImpl implements SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public Response<SensorData> saveSensorData(SensorDataDto sensorDataDto) {
        try {
            SensorData sensorDatas = new SensorData();

            if(sensorDataDto.getDeviceId() == null){
                return new Response<>(true, ResponseCode.NULL_ARGUMENT,"device id is null");
            }

            Optional<Devices> optionalDevice =  deviceRepository.findFirstById(sensorDataDto.getDeviceId());
            if(optionalDevice.isPresent()){
                sensorDatas.setDevices(optionalDevice.get());
            }
            else {
                return new Response<>(true,ResponseCode.NO_RECORD_FOUND, "device not found");
            }

            sensorDatas.setEnergy(sensorDataDto.getEnergy());
            sensorDatas.setPower(sensorDataDto.getPower());
            sensorDatas.setCurrent(sensorDataDto.getCurrent());
            sensorDatas.setVoltage(sensorDataDto.getVoltage());

            SensorData sensorData = sensorDataRepository.save(sensorDatas);

            return new Response<>(false, ResponseCode.SUCCESS, sensorData, "Sensor data saved");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new Response<>(true,ResponseCode.FAIL,"");
    }

    @Override
    public Response<SensorData> findSensorByUuid(String uuid) {
        try {
            Optional<SensorData> optionalSensorData = sensorDataRepository.findFirstByUuid(uuid);

            return optionalSensorData.map(sensorData -> new Response<>(false, ResponseCode.SUCCESS, sensorData, "Request successful")).orElseGet(() -> new Response<>(true, ResponseCode.NO_RECORD_FOUND, "Sensor data not found"));

        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(true, ResponseCode.FAIL, "An error occurred while retrieving sensor data");
        }
    }


    public Response<List<SensorData>> findAllSensors() {
        try {
            List<SensorData> sensorDataList = sensorDataRepository.findAll();


            if (sensorDataList.isEmpty()) {
                return new Response<>(true, ResponseCode.NO_RECORD_FOUND, "No sensor data found");
            }
            return new Response<>(false, ResponseCode.SUCCESS, sensorDataList, "Request successful");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(true, ResponseCode.FAIL, "An error occurred while retrieving sensor data");
        }
    }
}


