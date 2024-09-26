package com.zedeck.smartoutletserver.serviceImpl;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.dto.DeviceStateDto;
import com.zedeck.smartoutletserver.dto.GroupDeviceDto;
import com.zedeck.smartoutletserver.dto.SheduleDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.Group;
import com.zedeck.smartoutletserver.model.SensorReading;
import com.zedeck.smartoutletserver.model.UserAccount;
import com.zedeck.smartoutletserver.repository.DeviceRepository;
import com.zedeck.smartoutletserver.repository.GroupRepository;
import com.zedeck.smartoutletserver.repository.SensorReadingsRepository;
import com.zedeck.smartoutletserver.service.DeviceService;
import com.zedeck.smartoutletserver.service.SensorReadindService;
import com.zedeck.smartoutletserver.utils.*;
import com.zedeck.smartoutletserver.utils.userextractor.LoggedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorReadindService sensorReadindService;
    @Autowired
    private SensorReadingsRepository sensorReadingsRepository;


    @Autowired
    private GroupRepository groupRepository;

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Override
    public Response<Devices> registerDevice(DeviceDto deviceDto) {


        try {
            UserAccount user = loggedUser.getUser();
            Devices devices = new Devices();

            if (deviceDto.getDeviceUUid() != null) {
                Optional<Devices> devicesOptional = deviceRepository.findFirstByDeviceUuidAndDeletedFalse(deviceDto.getDeviceUUid());
                if (devicesOptional.isPresent()) {

                    SensorReading sensorReading  = new SensorReading();
                    logger.info("DEVICE ALREADY REGISTERED");

                    if (devicesOptional.get().isRegistered()){
                        sensorReading.setCurrent(deviceDto.getCurrent());
                        sensorReading.setVoltage(deviceDto.getVoltage());
                        sensorReading.setPower(deviceDto.getPower());
                        sensorReading.setEnergyConsumed(deviceDto.getEnergyConsumed());
                        sensorReading.setDeviceUuid(devicesOptional.get());

                        SensorReading sensorReading1 = sensorReadingsRepository.save(sensorReading);
                        return new Response<>(false,ResponseCode.SUCCESS,devicesOptional.get(), "Sensor data saved successfully");
                    }
                    else {
                        return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"Device already registered");
                    }
//                    sensorReadindService.saveSensorReading(deviceDto);
//                    return new Response<>(false,ResponseCode.SUCCESS,"Called method to save sensor data");

                }
                else {
                    if (deviceDto.getDeviceUUid() == null) {
                        return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Device UUID is null");
                    }

                    devices.setDeviceUuid(deviceDto.getDeviceUUid());
                    devices.setState(false);
                    devices.setRegistered(false);
                    Devices savedDevice = deviceRepository.save(devices);

                    return new Response<>(false, ResponseCode.SUCCESS, savedDevice, "Device registered successfully");
                }
            }
            else {
                return new Response<>(true,ResponseCode.DUPLICATE,"Device already registered");
            }


        } catch (Exception e) {
            logger.error("Error during device registration", e);
        }

        return new Response<>(true, ResponseCode.FAIL, "Request failed");
    }





    @Override
    public Response<DeviceResponse2> findDeviceByUuid(String uuid) {
        try {
            Optional<Devices> optionalDevice = deviceRepository.findFirstByUuid(uuid);
            if (optionalDevice.isEmpty()) {
                return new Response<>(true, ResponseCode.NO_RECORD_FOUND, "Device not found");
            }

            Devices device = optionalDevice.get();

            // Fetch sensor readings associated with the device
            List<SensorReading> readings = sensorReadingsRepository.findAllByDeviceUuidOrderByCreatedAtDesc(device.getUuid());
            logger.info("Sensor readings ===== " + readings);

            // Calculate totalEnergyConsumed for the device by summing the energyConsumed from sensor readings
            double totalEnergyConsumed = readings.stream()
                    .mapToDouble(SensorReading::getEnergyConsumed)
                    .sum();

            // Convert SensorReading to SensorReadingsResponse
            List<SensorReadingsResponse> sensorReadingsResponses = readings.stream()
                    .map(reading -> new SensorReadingsResponse(
                            reading.getVoltage(),
                            reading.getCurrent(),
                            reading.getPower(),
                            reading.getEnergyConsumed()
                    ))
                    .collect(Collectors.toList());

            // Create DeviceResponse with totalEnergyConsumed
            DeviceResponse2 deviceResponse = new DeviceResponse2(
                    device.getId(),
                    device.getDeviceName(),
                    device.getDeviceUuid(),
                    device.getUuid(),
                    device.isState(),
                    device.isRegistered(),
                    device.getTimer(),
                    device.getStartTime(),
                    device.getEndTime(),
                    device.getGroup() != null ? device.getGroup().getGroupName() : null,
                    device.getGroup() != null ? device.getGroup().getUuid() : null,
                    sensorReadingsResponses
                    // Add total energy consumed
            );

            return new Response<>(false, ResponseCode.SUCCESS, deviceResponse, "Device found successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.FAIL, "Request failed");
    }
    @Override
    public Response<Devices> updateDevice(DeviceDto deviceDto) {
        try {
            UserAccount user =  loggedUser.getUser();
            if(user == null){
                return new Response<>(true,ResponseCode.UNAUTHORIZED, "Unauthorized");
            }

            if(deviceDto.getDeviceUUid() == null){
                return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"");
            }

            Optional<Devices> devicesOptional =  deviceRepository.findFirstByDeviceUuidAndDeletedFalse(deviceDto.getDeviceUUid());
            if (devicesOptional.isPresent()) {

                logger.info("===== updating device ==========" + deviceDto.getDeviceUUid());

                if (devicesOptional.get().isRegistered()) {
                    sensorReadindService.saveSensorReading(deviceDto);
                    return new Response<>(false, ResponseCode.DUPLICATE, devicesOptional.get(), "Device already registered");
                } else {

                    Devices devices = devicesOptional.get();
                    if (deviceDto.getDeviceName() == null) {
                        return new Response<>(true, ResponseCode.NULL_ARGUMENT, "device name is null");
                    }
                    if (!deviceDto.getDeviceName().isBlank() && !Objects.equals(deviceDto.getDeviceName(), devices.getDeviceName())) {
                        devices.setDeviceName(deviceDto.getDeviceName());
                    }
                    devices.setRegistered(true);
                    devices.setState(false);
                    devices.setOwner(user);
                    devices.setStartTime("00 : 00");
                    devices.setEndTime("00 : 01");
                    devices.setTimer(0.0);

                    Devices devices1 = deviceRepository.save(devices);
                    return new Response<>(false, ResponseCode.SUCCESS, devices1, "device updated successful");


                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public Page<DeviceResponse2> findAllRegisteredDevices(boolean registered, Pageable pageable) {
        try {

            // Fetch registered devices
            Page<Devices> devicesPage = deviceRepository.findAllByRegistered(registered, pageable);

            // Use an array to wrap the sum of voltages, as arrays can be modified inside lambdas
            final double[] totalFirstVoltage = {0.0};

            List<DeviceResponse2> deviceResponses = devicesPage.getContent().stream()
                    .map(device -> {
                        List<SensorReading> readings = sensorReadingsRepository.findAllByDeviceUuidOrderByCreatedAtDesc(device.getUuid());

                        double totalEnergyConsumed = readings.stream()
                                .mapToDouble(SensorReading::getEnergyConsumed) // Sum the energy consumed
                                .sum();
                        List<SensorReadingsResponse> sensorReadingResponses = readings.stream()
                                .map(reading -> new SensorReadingsResponse(
                                        reading.getVoltage(),
                                        reading.getCurrent(),
                                        reading.getPower(),
                                        reading.getEnergyConsumed()
                                ))
                                .collect(Collectors.toList());

                        if (!sensorReadingResponses.isEmpty()) {
                            totalFirstVoltage[0] += sensorReadingResponses.get(0).getVoltage();
                        }

                        return new DeviceResponse2(
                                device.getId(),
                                device.getDeviceName(),
                                device.getDeviceUuid(),
                                device.getUuid(),
                                device.isState(),
                                device.isRegistered(),
                                device.getTimer(),
                                device.getStartTime(),
                                device.getEndTime(),
                                device.getGroup() != null ? device.getGroup().getGroupName() : null,
                                device.getGroup() != null ? device.getGroup().getUuid() : null,
                                sensorReadingResponses
                                 // Add total energy consumed
                        );
                    })
                    .collect(Collectors.toList());
            return new PageImpl<>(deviceResponses, pageable, devicesPage.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Page<Devices> findAllActiveDevices(Pageable pageable) {
        return null;
    }

    @Override
    public Boolean findDeviceStateByUuid(String uuid) {
        try {
            Boolean response = deviceRepository.findFirstByDeviceUuid(uuid);
            return response;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<Devices> deleteDeviceByUuid(String uuid) {
        return null;
    }

    @Override
    public Response<Devices> controlDeviceByUuid(DeviceStateDto deviceStateDto) {
        try {
            if(deviceStateDto.getDeviceUuid() != null){
                Optional<Devices> devicesOptional = deviceRepository.findFirstByUuid(deviceStateDto.getDeviceUuid());
                if(devicesOptional.isEmpty()){
                    return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"No device found");
                }

                Devices device =  devicesOptional.get();
                device.setState(deviceStateDto.isState());

                Devices devices = deviceRepository.save(device);

                return new Response<>(false,ResponseCode.SUCCESS, devices, "device turned " + deviceStateDto.isState() + " successful");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.FAIL,"operation failed");
    }

    @Override
    public Response<Devices> updateDeviceByUuid(String uuid, DeviceDto deviceDto) {
        return null;
    }

    @Override
    public Page<Devices> findDevicesByGroup(Long groupId, Pageable pageable) {
        try {
            Page<Devices>  devicesPage =  deviceRepository.findAllByGroupId(groupId,pageable);
            return devicesPage;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(null);
    }

    @Override
    public Response<Devices> assignDeviceToGroup(GroupDeviceDto groupDeviceDto) {
        try {
            Optional<Devices>  deviceOpt =  deviceRepository.findById(groupDeviceDto.getDeviceId());
            Optional<Group> groupOpt = groupRepository.findById(groupDeviceDto.getGroupId());

            if(deviceOpt.isPresent() && groupOpt.isPresent()){
                Devices device =  deviceOpt.get();
                Group group =  groupOpt.get();

                device.setGroup(group);
                group.getDevicesList().add(device);

                Devices devices = deviceRepository.save(device);
                Group g = groupRepository.save(group);

                return new Response<>(false,ResponseCode.SUCCESS,devices,"device assigned to group successfully");
            }


            return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"device or group not found");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.FAIL, "Request failed");
    }

    @Override
    public Response<Devices> scheduleDevice(SheduleDto sheduleDto) {
        try {
            if(sheduleDto.getDeviceUuid() == null || sheduleDto.getDeviceUuid().trim().equals("")) {
                return new Response<>(true,ResponseCode.NULL_ARGUMENT, "device uuid can not be empty");
            }

            Optional<Devices> optionalDevice =  deviceRepository.findFirstByDeviceUuidAndDeletedFalse(sheduleDto.getDeviceUuid());

            if (optionalDevice.isEmpty()){
                return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"No device found");
            }

            Devices device =  optionalDevice.get();

            device.setStartTime(sheduleDto.getStartTime());
            device.setEndTime(sheduleDto.getEndTime());

            Devices devices = deviceRepository.save(device);
            return new Response<>(false,ResponseCode.SUCCESS,devices,"Device scheduled successfully");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
