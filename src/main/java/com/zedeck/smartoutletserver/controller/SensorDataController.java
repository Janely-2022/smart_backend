package com.zedeck.smartoutletserver.controller;

import com.zedeck.smartoutletserver.dto.SensorDataDto;
import com.zedeck.smartoutletserver.model.SensorData;
import com.zedeck.smartoutletserver.repository.SensorDataRepository;
import com.zedeck.smartoutletserver.service.SensorDataService;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/sensor-data")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;
    @Autowired
    private SensorDataRepository sensorDataRepository;

    @PostMapping("/new-data")
    public ResponseEntity<?> createSensorData(@RequestBody SensorDataDto sensorDataDto) {
        Response<SensorData> savedSensorData = sensorDataService.saveSensorData(sensorDataDto);
        return ResponseEntity.ok().body(savedSensorData);
    }

    @GetMapping("/get-sensor-data/{uuid}")
    public ResponseEntity<Response<SensorData>> getSensorDataByUuid(@PathVariable String uuid) {
        Response<SensorData> response = sensorDataService.findSensorByUuid(uuid);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getAll-sensor-data")
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        Response<List<SensorData>> response = sensorDataService.findAllSensors();
            List<SensorData> sensorDataList = response.body();
            return ResponseEntity.ok().body(sensorDataList);
        }
    }





