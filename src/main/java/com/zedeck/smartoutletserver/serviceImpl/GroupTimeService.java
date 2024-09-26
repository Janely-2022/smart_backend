package com.zedeck.smartoutletserver.serviceImpl;

import com.zedeck.smartoutletserver.dto.DeviceStateDto;
import com.zedeck.smartoutletserver.service.DeviceService;
import com.zedeck.smartoutletserver.service.GroupService;
import com.zedeck.smartoutletserver.utils.GroupResponse;
import com.zedeck.smartoutletserver.utils.userextractor.LoggedUser;
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
public class GroupTimeService {

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private GroupService groupService;  // Assuming there's a GroupService for fetching groups
    @Autowired
    private LoggedUser loggedUser;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH : mm");

    @Scheduled(cron = "0 * * * * *") // Example scheduled task
    public void checkGroupAndDeviceTimes() {
        try {
            Pageable pageable = PageRequest.of(0, 100); // Adjust page and size as needed
            Page<GroupResponse> paginatedGroups = groupService.getAllGroups(pageable);

            if (paginatedGroups != null && !paginatedGroups.isEmpty()) {
                List<GroupResponse> groups = paginatedGroups.getContent();  // Get list from Page
                LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);  // Get current time and truncate to hours and minutes

                for (GroupResponse group : groups) {
                    String groupStartTime = group.getStartTime();
                    String groupEndTime = group.getEndTime();

                    LocalTime parsedGroupStartTime = null;
                    LocalTime parsedGroupEndTime = null;

                    if (groupStartTime != null && groupEndTime != null) {
                        try {
                            parsedGroupStartTime = LocalTime.parse(groupStartTime, timeFormatter);
                            parsedGroupEndTime = LocalTime.parse(groupEndTime, timeFormatter);
                        } catch (DateTimeParseException e) {
                            System.err.println("Error parsing group times for group: " + group.getGroupName() + ". Error: " + e.getMessage());
                            continue;
                        }

                        if (currentTime.equals(parsedGroupStartTime)) {
                            DeviceStateDto dto =  new DeviceStateDto();
                            dto.setGroupUuid(group.getUuid());
                            dto.setState(true);

                            groupService.controlGroupByUuid(dto); // Start the group
                            System.out.println("Current time: " + currentTime + ". Group '" + group.getGroupName() + "' start time reached!");
                        }

                        if (currentTime.equals(parsedGroupEndTime)) {
                            DeviceStateDto dto =  new DeviceStateDto();
                            dto.setGroupUuid(group.getUuid());
                            dto.setState(false);
                            groupService.controlGroupByUuid(dto); // Stop the group
                            System.out.println("Current time: " + currentTime + ". Group '" + group.getGroupName() + "' end time reached!");
                        }
                    }

                }
            } else {
                System.out.println("No groups found or an error occurred.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching groups or devices: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

