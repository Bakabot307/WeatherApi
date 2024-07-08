package com.skyapi.weatherforecast.hourlyweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class HourlyWeatherListDTO {
  private String location;
  @JsonProperty("hourly_weather")
  private List<HourlyWeatherDTO> hourlyWeatherList  = new ArrayList<>();

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<HourlyWeatherDTO> getHourlyWeatherList() {
    return hourlyWeatherList;
  }

  public void setHourlyWeatherList(List<HourlyWeatherDTO> hourlyWeatherList) {
    this.hourlyWeatherList = hourlyWeatherList;
  }

  public void addHourlyWeather(HourlyWeatherDTO hourlyWeather) {
    hourlyWeatherList.add(hourlyWeather);
  }
}
