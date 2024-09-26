package com.zedeck.smartoutletserver.repository;

import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Long> {

    Optional<Group> findFirstByGroupName(String name);
    Optional<Group> findFirstByUuid(String uuid);

    Page<Group> findAllByOrderByGroupNameAsc(Pageable pageable);

//    Page<Group> findAllByDeviceUuidOrderByGroupNameAsc(String uuid, Pageable pageable);

//    List<Devices> findDevicesByGroupUuid(String uuid);

}
