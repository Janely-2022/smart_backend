package com.zedeck.smartoutletserver.serviceImpl;

import com.zedeck.smartoutletserver.dto.DeviceDto;
import com.zedeck.smartoutletserver.dto.DeviceStateDto;
import com.zedeck.smartoutletserver.dto.GroupDto;
import com.zedeck.smartoutletserver.dto.SheduleDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.Group;
import com.zedeck.smartoutletserver.repository.DeviceRepository;
import com.zedeck.smartoutletserver.repository.GroupRepository;
import com.zedeck.smartoutletserver.repository.SensorReadingsRepository;
import com.zedeck.smartoutletserver.service.DeviceService;
import com.zedeck.smartoutletserver.service.GroupService;
import com.zedeck.smartoutletserver.utils.GroupResponse;
import com.zedeck.smartoutletserver.utils.Response;
import com.zedeck.smartoutletserver.utils.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorReadingsRepository sensorReadingsRepository;

    @Autowired
    private DeviceService deviceService;


    @Override
    public Response<Group> registerGroup(GroupDto groupDto) {
        try {

            Group group = new Group();

            if(groupDto.getGroupName() == null || groupDto.getGroupName().trim().equals("")) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT,"Group name cannot be empty");
            }

            group.setGroupName(groupDto.getGroupName());
            group.setStartTime("00 : 00");
            group.setEndTime("00 : 01");

            Group group1 =  groupRepository.save(group);

            return new Response<>(true, ResponseCode.SUCCESS,group1,"group registered successfully");


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.FAIL,"operation failed");
    }

    @Override
    public Response<Group> updateGroup(GroupDto groupDto) {
        return null;
    }

    @Override
    public Response<GroupResponse> getGroupByUuid(String groupUuid) {
        try {
            Optional<Group> group = groupRepository.findFirstByUuid(groupUuid);
            if(group.isPresent()) {
                Group group1 =  group.get();

                List<Devices> devicesList =  group1.getDevicesList();
                GroupResponse  groupResponse = GroupResponse.fromDevices(group1.getGroupName(),group1.getUuid(), group1.getId(), group1.isState(), group1.getStartTime(), group1.getEndTime(), devicesList,sensorReadingsRepository);
                return new Response<>(true, ResponseCode.SUCCESS,groupResponse,"group data found successfully");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.FAIL,"operation failed");
    }

    @Override
    public Page<GroupResponse> getAllGroups(Pageable pageable) {
        try {
            // Fetch all groups with pagination
            Page<Group> groups = groupRepository.findAllByOrderByGroupNameAsc(pageable);

            // Map each group to GroupResponse
            List<GroupResponse> groupResponses = groups.getContent().stream()
                    .map(group -> {
                        List<Devices> devicesList = group.getDevicesList();
                        return GroupResponse.fromDevices(
                                group.getGroupName(),
                                group.getUuid(),
                                group.getId(),
                                group.isState(),
                                group.getStartTime(),
                                group.getEndTime(),
                                devicesList,
                                sensorReadingsRepository
                        );
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(groupResponses, pageable, groups.getTotalElements());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0); // Return an empty page in case of failure
    }


    @Override
    public List<Devices> getGroupDevices(String uuid) {
        try {
            Optional<Group> group = groupRepository.findFirstByUuid(uuid);
            if (group.isPresent()) {
                List<Devices> devicesList = group.map(Group::getDevicesList).orElse(null);
                return devicesList;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<Group> controlGroupByUuid(DeviceStateDto deviceStateDto) {
        try {
            if(deviceStateDto.getGroupUuid() == null || deviceStateDto.getGroupUuid().trim().equals("")) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT,"Group uuid cannot be empty");
            }

            Optional<Group> group = groupRepository.findFirstByUuid(deviceStateDto.getGroupUuid());
            if(group.isEmpty()){
                return new Response<>(true, ResponseCode.NO_RECORD_FOUND,"No group found with that id");
            }

            Group group1 =  group.get();
            group1.setState(deviceStateDto.isState());


            List<Devices> devicesList = group1.getDevicesList();

            for (Devices devices : devicesList) {
                DeviceStateDto dto = new DeviceStateDto();
                dto.setGroupUuid(group1.getUuid());
                dto.setDeviceUuid(devices.getUuid());
                dto.setState(deviceStateDto.isState());
                deviceService.controlDeviceByUuid(dto);

                System.out.println("Device state updating");
            }

            Group group2 =  groupRepository.save(group1);

            return new Response<>(false, ResponseCode.SUCCESS,group2,"group control successfully");

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<Group> scheduleGroupByUuid(SheduleDto sheduleDto) {
        try {
            if(sheduleDto.getGroupUuid() == null || sheduleDto.getGroupUuid().trim().equals("")) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT,"Group uuid cannot be empty");
            }

            Optional<Group> group = groupRepository.findFirstByUuid(sheduleDto.getGroupUuid());
            if(group.isEmpty()){
                return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"No group found with that id");
            }

            Group group1 =  group.get();
            group1.setStartTime(sheduleDto.getStartTime());
            group1.setEndTime(sheduleDto.getEndTime());
            List<Devices> devicesList = group1.getDevicesList();

            for (Devices devices : devicesList) {
                SheduleDto deviceDto = new SheduleDto();

                deviceDto.setDeviceUuid(devices.getDeviceUuid());
                deviceDto.setStartTime(sheduleDto.getStartTime());
                deviceDto.setEndTime(sheduleDto.getEndTime());

                deviceService.scheduleDevice(deviceDto);

            }

            Group group2 =  groupRepository.save(group1);
            return new Response<>(false, ResponseCode.SUCCESS,group2,"group scheduled successfully");

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
