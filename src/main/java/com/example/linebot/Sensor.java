package com.example.linebot;

import com.example.linebot.sensors.Analog;
import com.example.linebot.sensors.Celsius_degree;
import com.example.linebot.sensors.Humidity;
import com.example.linebot.sensors.Moisture;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class Sensor {

    public static Celsius_degree createTemperature() throws HttpClientErrorException {
        String url = "https://us.wio.seeed.io/v1/node/GroveTempHumD0/temperature?access_token=";
        String key = "a6d12fb410d75d342036d1b192f76afe";
        URI uri = URI.create(url + key);
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        Celsius_degree celsiusdegree = restTemplate.getForObject(uri, Celsius_degree.class);
        return celsiusdegree;
    }

    public static Humidity createHumidity() throws HttpClientErrorException {
        String url = "https://us.wio.seeed.io/v1/node/GroveTempHumD0/humidity?access_token=";
        String key = "a6d12fb410d75d342036d1b192f76afe";
        URI uri = URI.create(url + key);
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        Humidity humidity = restTemplate.getForObject(uri, Humidity.class);
        return humidity;
    }

    public static Analog createAnalog() throws HttpClientErrorException {
        String url = "https://us.wio.seeed.io/v1/node/GenericAInA0/analog?access_token=";
        String key = "1224957e2c0aa40efa00a1205a1f5b4c";
        URI uri = URI.create(url + key);
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        Analog analog = restTemplate.getForObject(uri, Analog.class);
        return analog;
    }

    public static Moisture createMoisture() throws HttpClientErrorException {
        String url = "https://us.wio.seeed.io/v1/node/GroveMoistureA0/moisture?access_token=";
        String key = "a6d12fb410d75d342036d1b192f76afe";
        URI uri = URI.create(url + key);
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        Moisture moisture = restTemplate.getForObject(uri, Moisture.class);
        return moisture;
    }

}
