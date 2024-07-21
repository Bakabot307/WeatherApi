package com.skyapi.weatherforecast;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.dailyweather.DailyWeatherDTO;
import com.skyapi.weatherforecast.hourlyweather.HourlyWeatherDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherApiServiceApplication {

  @Bean
  public ModelMapper getModelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    var typeMap1 = modelMapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
    typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);

    var typeMap2 = modelMapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class);
    typeMap2.addMapping(HourlyWeatherDTO::getHourOfDay, (dest, value) -> dest.getId().setHourOfDay(value != null ? (int) value : 0));

    var typeMap3 = modelMapper.typeMap(DailyWeather.class, DailyWeatherDTO.class);
    typeMap3.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO::setDayOfMonth);
    typeMap3.addMapping(src -> src.getId().getMonth(), DailyWeatherDTO::setMonth);

    var typeMap4 = modelMapper.typeMap(DailyWeatherDTO.class, DailyWeather.class);
    typeMap4.addMapping(DailyWeatherDTO::getDayOfMonth, (dest, value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0));
    typeMap4.addMapping(DailyWeatherDTO::getMonth, (dest, value) -> dest.getId().setMonth(value != null ? (int) value : 0));

    return modelMapper;
  }

  public static void main(String[] args) {
    SpringApplication.run(WeatherApiServiceApplication.class, args);
  }

}
