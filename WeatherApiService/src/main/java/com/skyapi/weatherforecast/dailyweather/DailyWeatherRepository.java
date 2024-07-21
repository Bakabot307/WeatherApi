package com.skyapi.weatherforecast.dailyweather;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.DailyWeatherId;
import com.skyapi.weatherforecast.common.HourlyWeather;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DailyWeatherRepository extends CrudRepository<DailyWeather, DailyWeatherId>{
  @Query("SELECT h FROM DailyWeather h WHERE h.id.location.code = ?1 and h.id.location.trashed = false")
  public List<DailyWeather> getByLocationCode(String locationCode);

}
