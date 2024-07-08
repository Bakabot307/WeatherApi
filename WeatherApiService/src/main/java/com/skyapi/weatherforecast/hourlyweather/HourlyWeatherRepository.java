package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather, HourlyWeatherId> {
  @Query("SELECT hw from HourlyWeather hw where hw.id.location.code = ?1 and hw.id.hourOfDay > ?2 "
      + "and hw.id.location.trashed = false")
  public List<HourlyWeather> findByLocationCode(String locationCode, int currentHour);
}
