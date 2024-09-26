package com.zedeck.smartoutletserver.serviceImpl;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.SensorReading;
import com.zedeck.smartoutletserver.repository.DeviceRepository;
import com.zedeck.smartoutletserver.repository.SensorReadingsRepository;
import com.zedeck.smartoutletserver.service.SensorReadindService;
import com.zedeck.smartoutletserver.utils.Response;
import com.zedeck.smartoutletserver.utils.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
public class SensorReadingServiceImpl implements SensorReadindService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorReadingsRepository readingsRepository;

    private Logger logger;

    @Override
    public Response<SensorReading> saveSensorReading(DeviceDto deviceDto) {
        try {
            logger = LoggerFactory.getLogger(SensorReadingServiceImpl.class);

            logger.info("WRITING  SENSOR READING  FROM MICROCONTROLLER ");
        SensorReading sensorReading = new SensorReading();



        if(deviceDto.getDeviceUUid() != null){
            Optional<Devices> optionalDevice =  deviceRepository.findFirstByDeviceUuidAndDeletedFalse(deviceDto.getDeviceUUid());
            if(optionalDevice.isPresent()){
                if (optionalDevice.get().isRegistered()){
                    sensorReading.setCurrent(deviceDto.getCurrent());
                    sensorReading.setVoltage(deviceDto.getVoltage());
                    sensorReading.setPower(deviceDto.getPower());
                    sensorReading.setEnergyConsumed(deviceDto.getEnergyConsumed());
                    sensorReading.setDeviceUuid(optionalDevice.get());

                    SensorReading sensorReading1 = readingsRepository.save(sensorReading);
                    return new Response<>(false,ResponseCode.SUCCESS,sensorReading1, "Sensor data saved successfully");
                }
                else {
                    return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"Device not registered");
                }
            }
            else {
                return new Response<>(true, ResponseCode.NO_RECORD_FOUND,"No device found with that ID");
            }
        }
        else {
            return new Response<>(true, ResponseCode.NULL_ARGUMENT,"device id is null");
        }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return new Response<>(true,ResponseCode.FAIL,"Operation failed");
    }


    @Override
    public Response<SensorReading> updateSensorReading(DeviceDto deviceDto) {
        try {
            SensorReading sensorReading =  new SensorReading();

            if(deviceDto.getDeviceId() == null){
                return new Response<>(true,ResponseCode.NULL_ARGUMENT,"Device uuid is null");
            }

            Optional<Devices> optionalDevices =  deviceRepository.findFirstByDeviceUuidAndDeletedFalse(deviceDto.getDeviceUUid());
            if(optionalDevices.isPresent()){
                sensorReading.setCurrent(deviceDto.getCurrent());
                sensorReading.setVoltage(deviceDto.getVoltage());
                sensorReading.setPower(deviceDto.getPower());
                sensorReading.setEnergyConsumed(deviceDto.getEnergyConsumed());
                sensorReading.setDeviceUuid(optionalDevices.get());

                SensorReading sensorReading1 = readingsRepository.save(sensorReading);
                return new Response<>(false,ResponseCode.SUCCESS,sensorReading1, "Sensor data saved successfully");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<SensorReading> deleteSensorReading(DeviceDto deviceDto) {
        return null;
    }

    @Override
    public Page<SensorReading> getDeviceSensorReading(String uuid, Pageable pageable) {
        try {
            Page<SensorReading> sensorReadings = readingsRepository.findAllByUuidAndDeletedFalse(uuid, pageable);
            return sensorReadings;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<SensorReading> getAllDeviceSensorReading(Pageable pageable) {
        try {
            Page<SensorReading> allSensorReadings = readingsRepository.findAllByDeletedFalseOrderByCreatedAtDesc(pageable);
            return allSensorReadings;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<SensorReading> getSensorReading(String uuid, Pageable pageable) {
        try {
            Page<SensorReading> readingPage = readingsRepository.findAllByDeviceUuidOrderByCreatedAtDesc(uuid,pageable);
            return readingPage;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
