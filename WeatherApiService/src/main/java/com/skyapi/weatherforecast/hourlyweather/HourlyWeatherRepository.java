package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import org.springframework.data.repository.CrudRepository;

public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather, HourlyWeatherId> {

}
