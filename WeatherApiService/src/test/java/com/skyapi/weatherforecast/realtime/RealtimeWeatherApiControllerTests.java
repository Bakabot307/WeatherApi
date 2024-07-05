package com.skyapi.weatherforecast.realtime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RealtimeWeatherApiController.class)
public class RealtimeWeatherApiControllerTests {

  private final String END_POINT_PATH = "/v1/realtime";
  private static final String REQUEST_CONTENT_TYPE = "application/json";
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  RealtimeWeatherService realtimeWeatherService;
  @MockBean
  GeoLocationService geoLocationService;

  @Test
  public void testGetShouldReturnStatus400BadRequest() throws Exception {
    Mockito.when(geoLocationService.getLocation(Mockito.anyString()))
        .thenThrow(GeoLocationException.class);

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest()).andDo(print());
  }

  @Test
  public void testGetShouldReturnStatus404NotFound() throws Exception {
    Location location = new Location();

    Mockito.when(geoLocationService.getLocation(Mockito.anyString()))
        .thenReturn(location);
    Mockito.when(realtimeWeatherService.getByLocation(location)).thenThrow(LocationNotFoundException.class);

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound()).andDo(print());
  }

  @Test
  public void testGetShouldReturnStatus200Ok() throws Exception {
    Location location = new Location();
    location.setCode("SFCA_USA");
    location.setCountryCode("USA");
    location.setCityName("San Francisco");
    location.setCountryName("United States of America");
    location.setCountryCode("US");

    RealtimeWeather realTimeWeather = new RealtimeWeather();
    realTimeWeather.setTemperature(12);
    realTimeWeather.setHumidity(32);
    realTimeWeather.setPrecipitation(88);
    realTimeWeather.setStatus("Cloudy");
    realTimeWeather.setWindSpeed(12);
    realTimeWeather.setLatUpdate(new Date());

    location.setRealTimeWeather(realTimeWeather);
    realTimeWeather.setLocation(location);

    Mockito.when(geoLocationService.getLocation(Mockito.anyString()))
        .thenReturn(location);
    Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realTimeWeather);

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk()).andDo(print());
  }

  @Test
  public void testGetByCodeShouldReturnStatus404NotFound() throws Exception {
    String locationCode = "asdasd";
    Mockito.when(realtimeWeatherService.getByLocationCode(locationCode))
        .thenThrow(LocationNotFoundException.class);
    mockMvc.perform(get(END_POINT_PATH + "/" + locationCode))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void testGetByCodeShouldReturnStatus200OK() throws Exception {
    Location location = new Location();
    location.setCode("SFCA_USA");
    location.setCountryCode("USA");
    location.setCityName("San Francisco");
    location.setCountryName("United States of America");
    location.setCountryCode("US");

    RealtimeWeather realTimeWeather = new RealtimeWeather();
    realTimeWeather.setTemperature(12);
    realTimeWeather.setHumidity(32);
    realTimeWeather.setPrecipitation(88);
    realTimeWeather.setStatus("Cloudy");
    realTimeWeather.setWindSpeed(12);
    realTimeWeather.setLatUpdate(new Date());

    location.setRealTimeWeather(realTimeWeather);
    realTimeWeather.setLocation(location);

    Mockito.when(realtimeWeatherService.getByLocationCode(location.getCode())).thenReturn(realTimeWeather);
    mockMvc.perform(get(END_POINT_PATH + "/" + location.getCode()))
        .andExpect(status().isOk())
        .andDo(print());
  }
  @Test
  public void testUpdateWeatherShouldReturnStatus404NotFound() throws Exception {
    String locationCode = "asdasd";
    RealtimeWeather realtimeWeather = new RealtimeWeather();
    realtimeWeather.setTemperature(10);
    realtimeWeather.setHumidity(32);
    realtimeWeather.setPrecipitation(88);
    realtimeWeather.setStatus("Cloudy");
    realtimeWeather.setWindSpeed(12);
    String bodyContent = objectMapper.writeValueAsString(realtimeWeather);
    Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenThrow(LocationNotFoundException.class);
    mockMvc.perform(put(END_POINT_PATH + "/" + locationCode).contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE)).content(bodyContent))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void testUpdateWeatherShouldReturnStatus400BadRequest() throws Exception {

    String locationCode = "US_USA";
    RealtimeWeather realTimeWeather = new RealtimeWeather();
    realTimeWeather.setTemperature(999);
    realTimeWeather.setHumidity(32);
    realTimeWeather.setPrecipitation(88);
    realTimeWeather.setStatus("Cloudy");
    realTimeWeather.setWindSpeed(12);

    Mockito.when(realtimeWeatherService.update(locationCode, realTimeWeather)).thenReturn(realTimeWeather);
    String bodyContent = objectMapper.writeValueAsString(realTimeWeather);
    mockMvc.perform(put(END_POINT_PATH + "/" + locationCode).contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE)).content(bodyContent))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  public void testUpdateWeatherShouldReturnStatus200OK() throws Exception {

    String locationCode = "NYC_USA";
    Location location = new Location();
    location.setCode(locationCode);
    location.setCountryCode("NYC");
    location.setCityName("New York City");
    location.setCountryName("United States of America");
    location.setCountryCode("US");

    RealtimeWeather realTimeWeather = new RealtimeWeather();
    realTimeWeather.setTemperature(39);
    realTimeWeather.setHumidity(32);
    realTimeWeather.setPrecipitation(88);
    realTimeWeather.setStatus("Hot");
    realTimeWeather.setWindSpeed(12);
    realTimeWeather.setLatUpdate(new Date());

    realTimeWeather.setLocation(location);
    location.setRealTimeWeather(realTimeWeather);


    Mockito.when(realtimeWeatherService.update(locationCode, realTimeWeather)).thenReturn(realTimeWeather);
    String bodyContent = objectMapper.writeValueAsString(realTimeWeather);
    mockMvc.perform(put(END_POINT_PATH + "/" + locationCode).contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE)).content(bodyContent))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
