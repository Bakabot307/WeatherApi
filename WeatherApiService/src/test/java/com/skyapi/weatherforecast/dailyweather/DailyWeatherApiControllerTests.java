package com.skyapi.weatherforecast.dailyweather;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
}