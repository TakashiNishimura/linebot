package com.example.linebot.database;

import com.example.linebot.Sensor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalTime;

//@AllArgsConstructor
@Data
public class GardenItem {

    private LocalTime time;
    private float celsius_degree;
    private float humidity;
    private float analog;
    private float moisture;

    public GardenItem() {
        this.time = LocalTime.now();
        try {
            this.celsius_degree = Sensor.createTemperature().getCelsius_degree();
            this.humidity = Sensor.createHumidity().getHumidity();
            this.analog = Sensor.createAnalog().getAnalog();
            this.moisture = Sensor.createMoisture().getMoisture();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

}
