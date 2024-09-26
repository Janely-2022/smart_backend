package com.zedeck.smartoutletserver.repository;

import com.zedeck.smartoutletserver.model.SensorReading;
import com.zedeck.smartoutletserver.utils.SensorReadingsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.lang.annotation.Native;
import java.util.List;
import java.util.Optional;

public interface SensorReadingsRepository extends JpaRepository<SensorReading, Integer> {


    Optional<SensorReading> findByUuid(String uuid);
    Page<SensorReading> findAllByUuidAndDeletedFalse(String uuid, Pageable pageable);
    Page<SensorReading> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT sensor_reading  FROM sensor_readings sensor_reading WHERE sensor_reading.deviceUuid.uuid = ?1  order by sensor_reading.createdAt desc ")
    Page<SensorReading> findAllByDeviceUuidOrderByCreatedAtDesc(String uuid, Pageable pageable);

    @Query("SELECT sensor_reading  FROM sensor_readings sensor_reading WHERE sensor_reading.deviceUuid.uuid = ?1  order by sensor_reading.createdAt desc ")
    static List<SensorReading> findAllByDeviceUuid(String uuid) {
        return null;
    }

    @Query("SELECT sensor_reading  FROM sensor_readings sensor_reading WHERE sensor_reading.deviceUuid.uuid = ?1  order by sensor_reading.createdAt desc ")
    List<SensorReading> findAllByDeviceUuidOrderByCreatedAtDesc(String uuid);


}
