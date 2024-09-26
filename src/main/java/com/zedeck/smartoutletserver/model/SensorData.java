package com.zedeck.smartoutletserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "sensorData")
public class SensorData extends BaseEntity {
    @Column(name = "voltage")
    private Double voltage;

    @Column(name = "current")
    private Double current;

    @Column(name = "power")
    private Double power;

    @Column(name = "energyConsumpt")
    private Double energy;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    private Devices devices;
}

