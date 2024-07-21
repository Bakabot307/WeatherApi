package com.skyapi.weatherforecast.dailyweather;

import static org.assertj.core.api.Assertions.assertThat;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.DailyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
public class DailyWeatherRepositoryTests {
  @Autowired
  private DailyWeatherRepository repository;
  @Test
  public void testAddSuccess() {
    String locationCode = "DELHI_IN";
    Location location = new Location().code(locationCode);

    DailyWeather dailyWeather = new DailyWeather()
        .location(location)
        .dayOfMonth(3)
        .month(1)
        .maxTemp(30)
        .minTemp(20)
        .precipitation(0)
        .status("Rainy");

    DailyWeather savedDailyWeather = repository.save(dailyWeather);

    assertThat(savedDailyWeather.getId().getLocation().getCode()).isEqualTo(locationCode);
  }

  @Test
  public void testDelete(){
    String locationCode = "DELHI_IN";
    Location location = new Location().code(locationCode);
    DailyWeatherId id = new DailyWeatherId(3, 1, location);

    repository.deleteById(id);
    assertThat(repository.findById(id)).isEmpty();
  }
}
