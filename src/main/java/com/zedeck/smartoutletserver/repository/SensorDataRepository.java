package com.zedeck.smartoutletserver.repository;
import com.zedeck.smartoutletserver.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    Optional<SensorData> findFirstByUuid(String uuid);

}
