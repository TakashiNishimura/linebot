package com.example.linebot.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GardenRepository {

    private JdbcTemplate jdbc;

    @Autowired
    public GardenRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public int insert(GardenItem gardenItem) {
        var sql = "insert into " +
                    "garden_item (time, CELSIUS_DEGREE, humidity, analog, moisture)" +
                    "values (?, ?, ?, ?, ?)";
        return jdbc.update(sql,
                gardenItem.getTime(), gardenItem.getCelsius_degree(), gardenItem.getHumidity(), gardenItem.getAnalog(), gardenItem.getMoisture());
    }//DBnologic

}
