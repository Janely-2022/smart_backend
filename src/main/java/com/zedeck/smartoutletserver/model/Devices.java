package com.zedeck.smartoutletserver.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "devices")
public class Devices extends BaseEntity {

    @Column(name = "deviceName")
    private String deviceName;

    @Column(name = "deviceUuid", unique = true)
    private String deviceUuid;

    @Column(name = "state")
    private boolean state;

    @Column(name = "registered")
    private boolean registered;

    @Column(name = "timer")
    private double timer = 0.0;

    @Column(name="start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @ManyToOne
    @JoinColumn(name ="owner", referencedColumnName = "id")
    private UserAccount owner;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    @JsonBackReference
    private Group group;
}
