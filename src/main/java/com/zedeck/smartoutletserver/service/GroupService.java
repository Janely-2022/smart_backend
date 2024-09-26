package com.zedeck.smartoutletserver.service;

import com.zedeck.smartoutletserver.dto.DeviceStateDto;
import com.zedeck.smartoutletserver.dto.GroupDto;
import com.zedeck.smartoutletserver.dto.SheduleDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.Group;
import com.zedeck.smartoutletserver.utils.GroupResponse;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    Response<Group> registerGroup(GroupDto groupDto);
    Response<Group> updateGroup(GroupDto groupDto);
    Response<GroupResponse> getGroupByUuid(String groupUuid);

    Page<GroupResponse> getAllGroups(Pageable pageable);

    List<Devices> getGroupDevices(String uuid);

    Response<Group> controlGroupByUuid(DeviceStateDto deviceStateDto);

    Response<Group> scheduleGroupByUuid(SheduleDto sheduleDto);


}
