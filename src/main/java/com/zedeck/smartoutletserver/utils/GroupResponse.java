package com.zedeck.smartoutletserver.utils;

import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.SensorReading;
import com.zedeck.smartoutletserver.repository.SensorReadingsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupResponse {

    private String groupName;
    private String uuid;
    private Long id;
    private boolean state;
    private String startTime;
    private String endTime;
    private double groupEnergyConsumed;
    private List<DeviceResponse2> devices;



    public static GroupResponse fromDevices(String groupName, String uuid, Long id, boolean state, String startTime, String endTime, List<Devices> devices, SensorReadingsRepository sensorReadingsRepository) {

        final double[] totalGroupEnergyConsumed = {0.0};

        List<DeviceResponse2> deviceResponses = devices.stream()
                .map(device -> {
                    List<SensorReading> readings = sensorReadingsRepository.findAllByDeviceUuidOrderByCreatedAtDesc(device.getUuid());

                    List<SensorReadingsResponse> sensorReadingResponses = readings.stream()
                            .map(reading -> new SensorReadingsResponse(
                                    reading.getVoltage(),
                                    reading.getCurrent(),
                                    reading.getPower(),
                                    reading.getEnergyConsumed()
                            ))
                            .collect(Collectors.toList());

                    double totalEnergyConsumed = readings.stream()
                            .mapToDouble(SensorReading::getEnergyConsumed)
                            .sum();


                    totalGroupEnergyConsumed[0] += totalEnergyConsumed;

                    // Create DeviceResponse2 including sensor readings
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
                            sensorReadingResponses // Pass the sensor readings
                    );
                })
                .collect(Collectors.toList());

        return new GroupResponse(groupName, uuid, id, state ,startTime,endTime,totalGroupEnergyConsumed[0], deviceResponses);
    }
}
