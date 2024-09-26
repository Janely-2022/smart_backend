package com.zedeck.smartoutletserver.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "sensor_readings")
public class SensorReading extends BaseEntity {

    @Column(name = "voltage")
    private double voltage;

    @Column(name = "current")
    private double current;

    @Column(name = "power")
    private double power;

    @Column(name = "energyConsumed")
    private double energyConsumed;

    @ManyToOne
    @JoinColumn(name = "deviceUuid", referencedColumnName = "uuid")
    private Devices deviceUuid;
}
