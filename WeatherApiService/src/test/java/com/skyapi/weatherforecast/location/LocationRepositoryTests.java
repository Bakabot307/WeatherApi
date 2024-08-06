package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
public class LocationRepositoryTests {

  @Autowired
  private LocationRepository locationRepository;

  @Test
  public void testAddSuccess() {
    Location location = new Location();
    location.setCode("MBMH_IN");
    location.setCityName("Mumbai");
    location.setRegionName("Maharashtra");
    location.setCountryCode("IN");
    location.setCountryName("India");
    location.setEnabled(true);
    location.setTrashed(false);

    Location savedLocation = locationRepository.save(location);
    assertThat(savedLocation).isNotNull();
    assertThat(savedLocation.getCode()).isEqualTo("NYC_USA");
  }

  @Test
  public void testListSuccess() {
    List<Location> locationList = locationRepository.findUntrashed();

    assertThat(locationList).isNotNull();
    locationList.forEach(System.out::println);
  }

  @Test
  public void testGetNotFound() {
    String code = "abcd";
    Location location = locationRepository.findByCode(code);

    assertThat(location).isNull();
  }

  @Test
  public void testGetFound() {
    String code = "DELHI_IN";
    Location location = locationRepository.findByCode(code);

    assertThat(location).isNotNull();
    assertThat(location.getCode()).isEqualTo(code);
  }

  @Test
  public void testTrashSuccess() {
    String code = "DELHI_IN";
    locationRepository.trashByCode(code);

    Location location = locationRepository.findByCode(code);
    assertThat(location).isNull();
  }

  @Test
  public void testAddRealtimeWeatherData() {
    String code = "NYC_USA";
    Location location = locationRepository.findByCode(code);

    RealtimeWeather realTimeWeather = location.getRealtimeWeather();

    if (realTimeWeather == null) {
      realTimeWeather = new RealtimeWeather();
      realTimeWeather.setLocation(location);
      location.setRealtimeWeather(realTimeWeather);
    }
    realTimeWeather.setTemperature(-1);
    realTimeWeather.setHumidity(30);
    realTimeWeather.setPrecipitation(40);
    realTimeWeather.setStatus("Snowy");
    realTimeWeather.setWindSpeed(15);
    realTimeWeather.setLastUpdate(new Date());
    Location updatedLocation = locationRepository.save(location);

    assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(code);
  }

  @Test
  public void testAddHourlyWeatherData() {
    Location location = locationRepository.findById("DELHI_IN").get();
    List<HourlyWeather> list = location.getListHourlyWeather();

    System.out.println(location);

    HourlyWeather hourlyWeather = new HourlyWeather().id(location, 8)
        .temperature(6)
        .precipitation(0)
        .status("Rainy");

    HourlyWeather hourlyWeather2 = new HourlyWeather().id(location, 9)
        .temperature(11)
        .precipitation(20)
        .status("Cloudy");
    HourlyWeather hourlyWeather3 = new HourlyWeather().id(location, 10)
        .temperature(3)
        .precipitation(54)
        .status("Sunny");
    HourlyWeather hourlyWeather4 = new HourlyWeather().id(location, 11)
        .temperature(34)
        .precipitation(41)
        .status("Windy");

    list.add(hourlyWeather);
    list.add(hourlyWeather2);
    list.add(hourlyWeather3);
    list.add(hourlyWeather4);

    locationRepository.save(location);

    assertThat(location.getListHourlyWeather().size()).isEqualTo(4);
  }

  @Test
  public void testFindByCountryCodeAndCityNameNotFound() {
    String countryCode = "IN";
    String cityName = "New York City";
    Location location = locationRepository.findByCountryCodeAndAndCityName(countryCode, cityName);
    assertThat(location).isNull();
  }

  @Test
  public void testFindByCountryCodeAndCityNameFound() {
    String countryCode = "IN";
    String cityName = "Delhi";
    Location location = locationRepository.findByCountryCodeAndAndCityName(countryCode, cityName);

    assertThat(location).isNotNull();
    assertThat(location.getCityName()).isEqualTo(cityName);

  }

  @Test
  public void testAddDailyWeatherData() {
    Location location = locationRepository.findById("DELHI_IN").get();
    List<DailyWeather> list = location.getListDailyWeather();

    System.out.println(location);

    DailyWeather dailyWeather = new DailyWeather()
        .location(location)
        .dayOfMonth(1)
        .month(1)
        .maxTemp(30)
        .minTemp(20)
        .precipitation(0)
        .status("Rainy");

    DailyWeather dailyWeather2 = new DailyWeather()
        .location(location)
        .dayOfMonth(2)
        .month(1)
        .maxTemp(40)
        .minTemp(10)
        .precipitation(10)
        .status("Rainy");

    list.add(dailyWeather);
    list.add(dailyWeather2);


    locationRepository.save(location);

    assertThat(location.getListDailyWeather().size()).isEqualTo(2);
  }
}
