package com.zedeck.smartoutletserver.serviceImpl;

import com.zedeck.smartoutletserver.dto.DeviceStateDto;
import com.zedeck.smartoutletserver.service.DeviceService;
import com.zedeck.smartoutletserver.utils.DeviceResponse2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class DeviceTimeService {

    @Autowired
    private DeviceService deviceService;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH : mm");

    @Scheduled(cron = "0 * * * * *")
    public void checkDeviceTimes() {
        try {
            Pageable pageable = PageRequest.of(0, 100); // Adjust page and size as needed
            Page<DeviceResponse2> paginatedDevices = deviceService.findAllRegisteredDevices(true, pageable);

            System.out.println(paginatedDevices.getTotalElements());

            if (paginatedDevices != null && !paginatedDevices.isEmpty()) {
                List<DeviceResponse2> devices = paginatedDevices.getContent();  // Get list from Page
                LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);  // Get current time and truncate to hours and minutes

                // Loop through all devices
                for (DeviceResponse2 device : devices) {
                    String startTime = device.getStartTime();
                    String endTime = device.getEndTime();

                    // Check for null values
                    if (startTime == null || endTime == null) {
                        System.err.println("Start time or end time is null for device: " + device.getDeviceName());
                        continue;
                    }

                    LocalTime parsedStartTime = null;
                    LocalTime parsedEndTime = null;

                    try {
                        parsedStartTime = LocalTime.parse(startTime, timeFormatter);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing start time for device: " + device.getDeviceName() + ". Error: " + e.getMessage());
                        continue; // Skip to the next device
                    }

                    try {
                        parsedEndTime = LocalTime.parse(endTime, timeFormatter);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing end time for device: " + device.getDeviceName() + ". Error: " + e.getMessage());
                        continue; // Skip to the next device
                    }

                    if (currentTime.equals(parsedStartTime)) {
                        DeviceStateDto deviceStateDto = new DeviceStateDto();
                        deviceStateDto.setDeviceUuid(device.getUuid());
                        deviceStateDto.setState(true);  // Start the device

                        deviceService.controlDeviceByUuid(deviceStateDto);
                        System.out.println("Current time: " + currentTime + ". Device '" + device.getDeviceName() + "' start time reached!");
                    }

                    if (currentTime.equals(parsedEndTime)) {
                        DeviceStateDto deviceStateDto = new DeviceStateDto();
                        deviceStateDto.setDeviceUuid(device.getUuid());
                        deviceStateDto.setState(false);  // Stop the device

                        deviceService.controlDeviceByUuid(deviceStateDto);
                        System.out.println("Current time: " + currentTime + ". Device '" + device.getDeviceName() + "' end time reached! Time is over.");
                    }
                }
            } else {
                System.out.println("No devices found or an error occurred.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching devices: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
