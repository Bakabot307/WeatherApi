package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
public class HourlyWeatherTest {
  @Autowired
  private HourlyWeatherRepository hourlyWeatherRepository;

  @Test
  public void testAdd() {
    String locationCode = "DELHI_IN";
    int hourOfDay = 12;

    Location location = new Location().code(locationCode);

    HourlyWeather hourlyWeather = new HourlyWeather().id(location, 8)
        .location(location)
        .hourOfDay(hourOfDay)
        .temperature(6)
        .precipitation(0)
        .status("Snowy");

    location.getListHourlyWeather().add(hourlyWeather);
    HourlyWeather savedHourly = hourlyWeatherRepository.save(hourlyWeather);
    assertThat(savedHourly).isNotNull();
  }

  @Test
  public void testDelete(){
    String locationCode = "DELHI_IN";
    int hourOfDay = 12;

    Location location = new Location().code(locationCode);


    HourlyWeatherId hourlyWeatherId = new HourlyWeatherId(hourOfDay,location);


    hourlyWeatherRepository.deleteById(hourlyWeatherId);
  }
}
