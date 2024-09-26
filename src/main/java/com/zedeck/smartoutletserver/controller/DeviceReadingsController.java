package com.zedeck.smartoutletserver.controller;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.model.SensorReading;
import com.zedeck.smartoutletserver.service.SensorReadindService;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensor")

public class DeviceReadingsController {

    @Autowired
    private SensorReadindService sensorReadindService;


    @PostMapping("/new-reading")
    public ResponseEntity<?> saveReadinngs(@RequestBody DeviceDto deviceDto) {
        Response<SensorReading> response =  sensorReadindService.saveSensorReading(deviceDto);

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all-devices-readings")
    public ResponseEntity<?> getAllDeviceReadings( @RequestParam(value = "page", defaultValue = "0")Integer page,
                                                   @RequestParam(value = "size", defaultValue = "20")Integer size) {
        PageRequest pageRequest =  PageRequest.of(page,size);

        Page<SensorReading> sensorReadings =  sensorReadindService.getAllDeviceSensorReading(pageRequest);
        return ResponseEntity.ok().body(sensorReadings);
    }

    @GetMapping("/device-readings/{uuid}")
    public ResponseEntity<?> getDeviceReadings(@PathVariable("uuid") String uuid,
                                               @RequestParam(value = "page", defaultValue = "0")Integer page,
                                               @RequestParam(value = "size", defaultValue = "20")Integer size) {

        PageRequest pageRequest = PageRequest.of(page,size);
        Page<SensorReading>  pageReadings =  sensorReadindService.getSensorReading(uuid,pageRequest);
        return ResponseEntity.ok().body(pageReadings);
    }


}
