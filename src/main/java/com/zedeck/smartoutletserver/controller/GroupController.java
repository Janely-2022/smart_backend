package com.zedeck.smartoutletserver.controller;

import com.zedeck.smartoutletserver.dto.DeviceStateDto;
import com.zedeck.smartoutletserver.dto.GroupDto;
import com.zedeck.smartoutletserver.dto.SheduleDto;
import com.zedeck.smartoutletserver.model.Devices;
import com.zedeck.smartoutletserver.model.Group;
import com.zedeck.smartoutletserver.service.GroupService;
import com.zedeck.smartoutletserver.utils.GroupResponse;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/groups")

public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/new-group")
    public ResponseEntity<?> registerGroup(@RequestBody GroupDto groupDto){
        Response<Group> response = groupService.registerGroup(groupDto);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all-groups")
    public ResponseEntity<?> getAllGroups(@RequestParam(value = "page", defaultValue = "0")Integer page,
                                          @RequestParam(value = "size", defaultValue = "20")Integer size){
        PageRequest pageRequest  =  PageRequest.of(page,size);

        Page<GroupResponse> groups = groupService.getAllGroups(pageRequest);

        return ResponseEntity.ok().body(groups);
    }

    @GetMapping("/get-group/{uuid}")
     public ResponseEntity<?> getGroupByUuid(@PathVariable String uuid){
        Response<GroupResponse> response = groupService.getGroupByUuid(uuid);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get-group-devices/{uuid}")
    public ResponseEntity<?> getGroupDevices(@PathVariable String uuid){
        List<Devices> response = groupService.getGroupDevices(uuid);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/control-group")
    public ResponseEntity<?> controlGroup(@RequestBody DeviceStateDto  groupDto){
        Response<Group> response = groupService.controlGroupByUuid(groupDto);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/schedule-group")
    public ResponseEntity<?> sheduleGroup(@RequestBody SheduleDto groupDto){
        Response<Group> response =  groupService.scheduleGroupByUuid(groupDto);

        return ResponseEntity.ok().body(response);
    }


}
