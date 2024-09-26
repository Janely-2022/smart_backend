package com.zedeck.smartoutletserver.controller;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.service.DeviceService;
import com.zedeck.smartoutletserver.utils.DeviceResponse2;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/devices")

public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/new-device")
    public ResponseEntity<?> registerDevice(@RequestBody DeviceDto deviceDto){
        Response<Devices> response =  deviceService.registerDevice(deviceDto);

        return ResponseEntity.ok().body(response);

    }

    @PutMapping("/save-device")
    public ResponseEntity<?> updateDevice(@RequestBody DeviceDto deviceDto){
        Response<Devices> response = deviceService.updateDevice(deviceDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get-device/{uuid}")
    public ResponseEntity<?> getDevice(@PathVariable String uuid){
        Response<DeviceResponse2> response =  deviceService.findDeviceByUuid(uuid);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get-device-state/{deviceUuid}")
    public ResponseEntity<?> getDeviceState(@PathVariable String deviceUuid){
        Boolean response = deviceService.findDeviceStateByUuid(deviceUuid);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get-registered-devices")
    public ResponseEntity<?> getRegisteredDevices(@RequestParam(value = "page", defaultValue = "0")Integer page,
                                                  @RequestParam(value = "size", defaultValue = "20")Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<DeviceResponse2> devicesPage = deviceService.findAllRegisteredDevices(true,pageRequest);
        return ResponseEntity.ok().body(devicesPage);
    }

    @GetMapping("/get-unregistered-devices")
    public ResponseEntity<?> getUnRegisteredDevices(@RequestParam(value = "page", defaultValue = "0")Integer page,
                                                  @RequestParam(value = "size", defaultValue = "20")Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<DeviceResponse2> devicesPage = deviceService.findAllRegisteredDevices(false,pageRequest);
        return ResponseEntity.ok().body(devicesPage);
    }



}
