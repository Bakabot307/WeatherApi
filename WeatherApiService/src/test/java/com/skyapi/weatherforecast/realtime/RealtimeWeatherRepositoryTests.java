package com.skyapi.weatherforecast.realtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.skyapi.weatherforecast.common.RealtimeWeather;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
public class RealtimeWeatherRepositoryTests {

  @Autowired
  private RealtimeWeatherRepository repository;

  @Test
  public void testUpdate() {
    String code = "NYC_USA";
    RealtimeWeather realTimeWeather = repository.findById(code).get();
    realTimeWeather.setTemperature(-2);
    realTimeWeather.setHumidity(32);
    realTimeWeather.setPrecipitation(42);
    realTimeWeather.setStatus("Snowy");
    realTimeWeather.setWindSpeed(12);
    realTimeWeather.setLastUpdate(new Date());
    RealtimeWeather updatedRealtimeWeather = repository.save(realTimeWeather);
    assertThat(updatedRealtimeWeather.getHumidity()).isEqualTo(32);
  }

  @Test
  public void testFindByCountryCodeAndCityNotFound() {
    String countryCode = "Japan";
    String city = "Tokyo";

    RealtimeWeather realTimeWeather = repository.findByCountryCodeAndCity(countryCode, city);

    assertThat(realTimeWeather).isNull();
  }

  @Test
  public void testFindByCountryCodeAndCityFound() {
    String countryCode = "US";
    String city = "New York City";

    RealtimeWeather realTimeWeather = repository.findByCountryCodeAndCity(countryCode, city);

    assertThat(realTimeWeather).isNotNull();
    assertThat(realTimeWeather.getLocation().getCityName()).isEqualTo(city);
  }

  @Test
  public void testFindCountryByLocationCode() {
    String code = "NYC_USA";
    RealtimeWeather realTimeWeather = repository.findByLocationCode(code);
    assertThat(realTimeWeather).isNotNull();
    assertThat(realTimeWeather.getLocation().getCode()).isEqualTo(code);
  }

}
