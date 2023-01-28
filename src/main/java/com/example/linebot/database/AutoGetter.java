package com.example.linebot.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Slf4j
@Component
public class AutoGetter {

    private GardenRepository repository;

    @Autowired
    public AutoGetter(GardenRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Tokyo")
//    @Scheduled(cron = "0 0 */6 * * *", zone = "Asia/Tokyo")
    public void insertAllSensorData() {
        log.info("Activate Sensor...");
        try {
            var item = new GardenItem();
            repository.insert(item);
        } catch (DataAccessException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }

}
