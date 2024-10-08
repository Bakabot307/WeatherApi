package com.skyapi.weatherforecast.dailyweather;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DailyWeatherApiController.class)
public class DailyWeatherApiControllerTests {

  private static final String END_POINT_PATH = "/v1/daily";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DailyWeatherService dailyWeatherService;
  @MockBean
  private LocationService locationService;
  @MockBean
  private GeoLocationService geoLocationService;
  @Autowired
  private ObjectMapper objectMapper;



  @Test
  public void testGetByIPShouldReturn404BadRequestBecauseGeolocationException() throws Exception {
    GeoLocationException ex = new GeoLocationException("Geolocation error");
    Mockito.when(geoLocationService.getLocation(Mockito.anyString())).thenThrow(ex);

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound()).andExpect(jsonPath("$.errors[0]", is(ex.getMessage()))).andDo(print());
  }

  @Test
  public void testGetByIPShouldReturn404NotFound() throws Exception {
    Location location = new Location().code("DELHI_IND");

    when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);

    LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
    when(dailyWeatherService.getByLocation(location)).thenThrow(ex);

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound()).andExpect(jsonPath("$.errors[0]", is(ex.getMessage()))).andDo(print());
  }

  @Test
  public void testGetByIPShouldReturn204NoContent() throws Exception {
    Location location = new Location().code("DELHI_IN");

    Mockito.when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
    when(dailyWeatherService.getByLocation(location)).thenReturn(new ArrayList<>());

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent()).andDo(print());
  }

  @Test
  public void testGetByIPShouldReturn200OK() throws Exception {
    Location location = new Location();
    location.setCode("NYC_USA");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");

    DailyWeather forecast1 = new DailyWeather().location(location).dayOfMonth(16).month(7).minTemp(23).maxTemp(32).precipitation(40).status("Cloudy");

    DailyWeather forecast2 = new DailyWeather().location(location).dayOfMonth(17).month(7).minTemp(25).maxTemp(34).precipitation(30).status("Sunny");

    Mockito.when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
    when(dailyWeatherService.getByLocation(location)).thenReturn(List.of(forecast1, forecast2));

    String expectedLocation = location.toString();

    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk()).andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.location", is(expectedLocation))).andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16))).andDo(print());
  }


  @Test
  public void testGetByCodeShouldReturn404NotFound() throws Exception {
    String locationCode = "LACA_US";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    LocationNotFoundException ex = new LocationNotFoundException(locationCode);
    when(dailyWeatherService.getByLocationCode(locationCode)).thenThrow(ex);

    mockMvc.perform(get(requestURI)).andExpect(status().isNotFound()).andExpect(jsonPath("$.errors[0]", is(ex.getMessage()))).andDo(print());
  }

  @Test
  public void testGetByCodeShouldReturn204NoContent() throws Exception {
    String locationCode = "LACA_US";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(new ArrayList<>());

    mockMvc.perform(get(requestURI)).andExpect(status().isNoContent()).andDo(print());
  }

  @Test
  public void testGetByCodeShouldReturn200OK() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    Location location = new Location();
    location.setCode(locationCode);
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");

    DailyWeather forecast1 = new DailyWeather().location(location).dayOfMonth(16).month(7).minTemp(23).maxTemp(32).precipitation(40).status("Cloudy");

    DailyWeather forecast2 = new DailyWeather().location(location).dayOfMonth(17).month(7).minTemp(25).maxTemp(34).precipitation(30).status("Sunny");

    when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(List.of(forecast1, forecast2));

    String expectedLocation = location.toString();

    mockMvc.perform(get(requestURI)).andExpect(status().isOk()).andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.location", is(expectedLocation))).andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16))).andDo(print());
  }

  @Test
  public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
    String requestURI = END_POINT_PATH + "/NYC_USA";

    List<DailyWeatherDTO> listDTO = Collections.emptyList();

    String requestBody = objectMapper.writeValueAsString(listDTO);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0]", is("Daily weather data is empty to update")))
        .andDo(print());
  }

  @Test
  public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
    String requestURI = END_POINT_PATH + "/NYC_USA";

    DailyWeatherDTO dto1 = new DailyWeatherDTO()
        .dayOfMonth(40)
        .month(7)
        .minTemp(23)
        .maxTemp(30)
        .precipitation(20)
        .status("Clear");

    DailyWeatherDTO dto2 = new DailyWeatherDTO()
        .dayOfMonth(20)
        .month(7)
        .minTemp(23)
        .maxTemp(30)
        .precipitation(20)
        .status("Clear");

    List<DailyWeatherDTO> listDTO = List.of(dto1, dto2);

    String requestBody = objectMapper.writeValueAsString(listDTO);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0]", containsString("Day of month must be between 1-31")))
        .andDo(print());

  }

  @Test
  public void testUpdateShouldReturn404NotFound() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    DailyWeatherDTO dto = new DailyWeatherDTO()
        .dayOfMonth(21)
        .month(7)
        .minTemp(23)
        .maxTemp(30)
        .precipitation(20)
        .status("Clear");

    List<DailyWeatherDTO> listDTO = List.of(dto);

    String requestBody = objectMapper.writeValueAsString(listDTO);

    LocationNotFoundException ex = new LocationNotFoundException(locationCode);
    when(dailyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenThrow(ex);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isNotFound())
        .andDo(print());

  }

  @Test
  public void testUpdateShouldReturn200OK() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    DailyWeatherDTO dto1 = new DailyWeatherDTO()
        .dayOfMonth(17)
        .month(7)
        .minTemp(25)
        .maxTemp(35)
        .precipitation(40)
        .status("Sunny");

    DailyWeatherDTO dto2 = new DailyWeatherDTO()
        .dayOfMonth(18)
        .month(7)
        .minTemp(26)
        .maxTemp(34)
        .precipitation(50)
        .status("Clear");

    Location location = new Location();
    location.setCode("NYC_USA");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");

    DailyWeather forecast1 = new DailyWeather()
        .location(location)
        .dayOfMonth(17)
        .month(7)
        .minTemp(25)
        .maxTemp(35)
        .precipitation(40)
        .status("Sunny")
        ;

    DailyWeather forecast2 = new DailyWeather()
        .location(location)
        .dayOfMonth(18)
        .month(7)
        .minTemp(26)
        .maxTemp(34)
        .precipitation(50)
        .status("Clear")
        ;

    var listDTO = List.of(dto1, dto2);

    var dailyForecast = List.of(forecast1, forecast2);

    String requestBody = objectMapper.writeValueAsString(listDTO);

    when(dailyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenReturn(dailyForecast);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.location", is(location.toString())))
        .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(17)))
        .andDo(print());

  }
}