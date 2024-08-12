package com.skyapi.weatherforecast.fullweather;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FullWeatherController.class)
public class FullWeatherApiControllerTests {

  private static final String END_POINT_PATH = "/v1/full";
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FullWeatherService fullWeatherService;
  @MockBean
  private GeoLocationService geoLocationService;

  @Test
  public void testListFullWeatherByIPAddressReturn400BadRequestGeoLocationService() throws Exception {
    GeoLocationException geoLocationException = new GeoLocationException("Location not found");
    when(geoLocationService.getLocation(Mockito.anyString())).thenThrow(geoLocationException);
    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest());
  }

  @Test
  public void testListFullWeatherByIPAddressReturn404NotFound() throws Exception {
    Location location = new Location().code("DELHI_IN");
    LocationNotFoundException exception = new LocationNotFoundException(location.getCode());
    when(geoLocationService.getLocation(Mockito.anyString())).thenThrow(exception);
    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound());
  }


  @Test
  public void testListFullWeatherByIPAddressReturn200Ok() throws Exception {
    Location location = new Location().code("DELHI_IN");
    location.setCountryCode("IN");
    location.setCityName("DELHI");
    location.setRegionName("DELHI");
    location.setCountryName("INDIA");
    location.setEnabled(true);

    RealtimeWeather realtimeWeather = new RealtimeWeather();
    realtimeWeather.setLocation(location);
    realtimeWeather.setTemperature(30);
    realtimeWeather.setHumidity(50);
    realtimeWeather.setWindSpeed(10);
    realtimeWeather.setLastUpdate(new Date());
    realtimeWeather.setStatus("Sunny");

    List<DailyWeather> dailyWeathersList = new ArrayList<>();
    DailyWeather dailyWeather = new DailyWeather().id(1, 1, location).minTemp(20).maxTemp(30).precipitation(0).status("Sunny");
    dailyWeathersList.add(dailyWeather);

    List<HourlyWeather> hourlyWeatherList = new ArrayList<>();
    HourlyWeather hourlyWeather = new HourlyWeather().id(location, 1).precipitation(0).temperature(30).status("Sunny");
    hourlyWeatherList.add(hourlyWeather);

    location.setRealtimeWeather(realtimeWeather);
    location.setListHourlyWeather(hourlyWeatherList);
    location.setListDailyWeather(dailyWeathersList);

    when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
    when(fullWeatherService.getLocation(location)).thenReturn(location);

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk()).andDo(print());
  }


  @Test
  public void testListFullWeatherByCode404Notfound() throws Exception {
    String locationCode = "DELHI_IN";
    String pathRequest = END_POINT_PATH + "/" + locationCode;
    LocationNotFoundException exception = new LocationNotFoundException(locationCode);
    when(fullWeatherService.getLocation(locationCode)).thenThrow(exception);
    mockMvc.perform(get(pathRequest)).andExpect(status().isNotFound());
  }

  @Test
  public void testListFullWeatherByCode200Ok() throws Exception {
    Location location = new Location().code("DELHI_IN");
    location.setCountryCode("IN");
    location.setCityName("DELHI");
    location.setRegionName("DELHI");
    location.setCountryName("INDIA");
    location.setEnabled(true);

    RealtimeWeather realtimeWeather = new RealtimeWeather();
    realtimeWeather.setLocation(location);
    realtimeWeather.setTemperature(30);
    realtimeWeather.setHumidity(50);
    realtimeWeather.setWindSpeed(10);
    realtimeWeather.setLastUpdate(new Date());
    realtimeWeather.setStatus("Sunny");

    List<DailyWeather> dailyWeathersList = new ArrayList<>();
    DailyWeather dailyWeather = new DailyWeather().id(1, 1, location).minTemp(20).maxTemp(30).precipitation(0).status("Sunny");
    dailyWeathersList.add(dailyWeather);

    List<HourlyWeather> hourlyWeatherList = new ArrayList<>();
    HourlyWeather hourlyWeather = new HourlyWeather().id(location, 1).precipitation(0).temperature(30).status("Sunny");
    hourlyWeatherList.add(hourlyWeather);

    location.setRealtimeWeather(realtimeWeather);
    location.setListHourlyWeather(hourlyWeatherList);
    location.setListDailyWeather(dailyWeathersList);
    when(fullWeatherService.getLocation(location.getCode())).thenReturn(location);
    mockMvc.perform(get(END_POINT_PATH + "/DELHI_IN")).andExpect(status().isOk()).andDo(print());
  }
}
