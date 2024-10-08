package com.skyapi.weatherforecast.realtime;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(RealtimeWeatherApiController.class)
public class RealtimeWeatherApiControllerTests {

  private static final String END_POINT_PATH = "/v1/realtime";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper mapper;

  @MockBean RealtimeWeatherService realtimeWeatherService;
  @MockBean
  GeoLocationService locationService;

  @Test
  public void testGetShouldReturnStatus400BadRequest() throws Exception {
    GeoLocationException ex = new GeoLocationException("Geolocation error");
    Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(ex);

    mockMvc.perform(get(END_POINT_PATH))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
        .andDo(print());
  }

  @Test
  public void testGetShouldReturnStatus404NotFound() throws Exception {
    Location location = new Location();
    location.setCountryCode("US");
    location.setCityName("Tampa");

    LocationNotFoundException ex = new LocationNotFoundException(location.getCountryCode(), location.getCityName());

    Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
    Mockito.when(realtimeWeatherService.getByLocation(location)).thenThrow(ex);

    mockMvc.perform(get(END_POINT_PATH))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
        .andDo(print());
  }

  @Test
  public void testGetShouldReturnStatus200OK() throws Exception {
    Location location = new Location();
    location.setCode("SFCA_USA");
    location.setCityName("San Franciso");
    location.setRegionName("California");
    location.setCountryName("United States of America");
    location.setCountryCode("US");

    RealtimeWeather realtimeWeather = new RealtimeWeather();
    realtimeWeather.setTemperature(12);
    realtimeWeather.setHumidity(32);
    realtimeWeather.setLastUpdate(new Date());
    realtimeWeather.setPrecipitation(88);
    realtimeWeather.setStatus("Cloudy");
    realtimeWeather.setWindSpeed(5);

    realtimeWeather.setLocation(location);
    location.setRealtimeWeather(realtimeWeather);


    Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
    Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realtimeWeather);

    String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

    mockMvc.perform(get(END_POINT_PATH))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.location", is(expectedLocation)))
        .andDo(print());
  }

  @Test
  public void testGetByLocationCodeShouldReturnStatus404NotFound() throws Exception {
    String locationCode = "ABC_US";

    LocationNotFoundException ex = new LocationNotFoundException(locationCode);
    Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenThrow(ex);

    String requestURI = END_POINT_PATH + "/" + locationCode;

    mockMvc.perform(get(requestURI))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
        .andDo(print());
  }

  @Test
  public void testGetByLocationCodeShouldReturnStatus200OK() throws Exception {
    String locationCode = "SFCA_USA";

    Location location = new Location();
    location.setCode(locationCode);
    location.setCityName("San Franciso");
    location.setRegionName("California");
    location.setCountryName("United States of America");
    location.setCountryCode("US");

    RealtimeWeather realtimeWeather = new RealtimeWeather();
    realtimeWeather.setTemperature(12);
    realtimeWeather.setHumidity(32);
    realtimeWeather.setLastUpdate(new Date());
    realtimeWeather.setPrecipitation(88);
    realtimeWeather.setStatus("Cloudy");
    realtimeWeather.setWindSpeed(5);

    realtimeWeather.setLocation(location);
    location.setRealtimeWeather(realtimeWeather);


    Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(realtimeWeather);

    String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

    String requestURI = END_POINT_PATH + "/" + locationCode;

    mockMvc.perform(get(requestURI))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.location", is(expectedLocation)))
        .andDo(print());
  }

  @Test
  public void testUpdateShouldReturn400BadRequest() throws Exception {
    String locationCode = "ABC_US";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    RealtimeWeatherDTO dto = new RealtimeWeatherDTO();
    dto.setTemperature(120);
    dto.setHumidity(132);
    dto.setPrecipitation(188);
    dto.setStatus("Cl");
    dto.setWindSpeed(500);

    String bodyContent = mapper.writeValueAsString(dto);

    mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  public void testUpdateShouldReturn404NotFound() throws Exception {
    String locationCode = "ABC_US";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    RealtimeWeatherDTO dto = new RealtimeWeatherDTO();
    dto.setTemperature(12);
    dto.setHumidity(32);
    dto.setPrecipitation(88);
    dto.setStatus("Cloudy");
    dto.setWindSpeed(5);

    LocationNotFoundException ex = new LocationNotFoundException(locationCode);
    Mockito.when(realtimeWeatherService.update(Mockito.eq(locationCode), Mockito.any())).thenThrow(ex);

    String bodyContent = mapper.writeValueAsString(dto);

    mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
        .andDo(print());
  }

  @Test
  public void testUpdateShouldReturn200OK() throws Exception {
    String locationCode = "SFCA_US";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    RealtimeWeather realtimeWeather = new RealtimeWeather();
    realtimeWeather.setTemperature(12);
    realtimeWeather.setHumidity(32);
    realtimeWeather.setPrecipitation(88);
    realtimeWeather.setStatus("Cloudy");
    realtimeWeather.setWindSpeed(5);
    realtimeWeather.setLastUpdate(new Date());

    RealtimeWeatherDTO dto = new RealtimeWeatherDTO();
    dto.setTemperature(12);
    dto.setHumidity(32);
    dto.setPrecipitation(88);
    dto.setStatus("Cloudy");
    dto.setWindSpeed(5);

    Location location = new Location();
    location.setCode(locationCode);
    location.setCityName("San Franciso");
    location.setRegionName("California");
    location.setCountryName("United States of America");
    location.setCountryCode("US");

    realtimeWeather.setLocation(location);
    location.setRealtimeWeather(realtimeWeather);

    Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);

    String bodyContent = mapper.writeValueAsString(dto);

    String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

    mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.location", is(expectedLocation)))
        .andDo(print());
  }
}