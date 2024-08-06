package com.skyapi.weatherforecast.fullweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyapi.weatherforecast.dailyweather.DailyWeatherDTO;
import com.skyapi.weatherforecast.hourlyweather.HourlyWeatherDTO;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDTO;
import java.util.ArrayList;
import java.util.List;

public class FullWeatherDTO {

  private String location;
  @JsonProperty("realtime_weather")
  private RealtimeWeatherDTO realtimeWeather;
  @JsonProperty("hourly_weather")
  private List<HourlyWeatherDTO> listHourlyWeather = new ArrayList<>();
  @JsonProperty("daily_weather")
  private List<DailyWeatherDTO> listDailyWeather = new ArrayList<>();

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public RealtimeWeatherDTO getRealtimeWeather() {
    return realtimeWeather;
  }

  public void setRealtimeWeather(RealtimeWeatherDTO realtimeWeather) {
    this.realtimeWeather = realtimeWeather;
  }

  public List<HourlyWeatherDTO> getListHourlyWeather() {
    return listHourlyWeather;
  }

  public void setListHourlyWeather(List<HourlyWeatherDTO> listHourlyWeather) {
    this.listHourlyWeather = listHourlyWeather;
  }

  public List<DailyWeatherDTO> getListDailyWeather() {
    return listDailyWeather;
  }

  public void setListDailyWeather(List<DailyWeatherDTO> listDailyWeather) {
    this.listDailyWeather = listDailyWeather;
  }
}
