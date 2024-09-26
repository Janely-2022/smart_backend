package com.zedeck.smartoutletserver.repository;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Devices, Long> {

    Optional<Devices> findFirstByUuid(String uuid);

    Optional<Devices> findFirstByDeviceUuidAndDeletedFalse(String deviceUuid);

    Response<Devices> deleteByUuid(String uuid);

    Optional<Devices> findFirstById(Long id);

    @Query("SELECT device.state from devices device where device.deviceUuid = ?1 ")
    Boolean findFirstByDeviceUuid(String uuid);

    Page<Devices> findAllByRegistered(boolean registered, Pageable pageable);

    Page<Devices> findAllByState(boolean state, Pageable pageable);

    Page<Devices> findAllByGroupId(long groupId, Pageable pageable);


}
