package com.skyapi.weatherforecast.hourlyweather;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HourlyWeatherController.class)
public class HourWeatherApiControllerTests {

  private static final String END_POINT_PATH = "/v1/hourly";
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private HourlyWeatherService hourlyWeatherService;
  @MockBean
  private GeoLocationService geoLocationService;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private ModelMapper modelMapper;

  @Test
  public void testGetByIPAddressShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest());
  }

  @Test
  public void testGetByIPAddressShouldReturn400BadRequestBecauseGeoLocationException() throws Exception {
    when(geoLocationService.getLocation(Mockito.anyString())).thenThrow(GeoLocationException.class);
    mockMvc.perform(get(END_POINT_PATH).header("X-CURRENT-HOUR", "10")).andExpect(status().isBadRequest());
  }

  @Test
  public void testGetByIPAddressShouldReturn204NoContent() throws Exception {
    int currentHour = 10;
    List<HourlyWeather> list = new ArrayList<>();
    Location location = new Location().code("DELHI_IN");
    when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
    when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(list);

    mockMvc.perform(get(END_POINT_PATH).header("X-CURRENT-HOUR", currentHour)).andExpect(status().isNoContent());
  }

  @Test
  public void testGetByIPAddressShouldReturn200Ok() throws Exception {
    int currentHour = 10;

    Location location = new Location().code("DELHI_IN");
    location.setCityName("Delhi");
    location.setRegionName("Delhi");
    location.setCountryCode("IN");
    location.setCountryName("India");
    location.setEnabled(true);
    location.setTrashed(false);

    HourlyWeather hourlyWeather = new HourlyWeather().id(location, 8).temperature(6).precipitation(0).status("Rainy");

    HourlyWeather hourlyWeather2 = new HourlyWeather().id(location, 9).temperature(11).precipitation(20).status("Cloudy");
    HourlyWeather hourlyWeather3 = new HourlyWeather().id(location, 10).temperature(3).precipitation(54).status("Sunny");
    HourlyWeather hourlyWeather4 = new HourlyWeather().id(location, 11).temperature(34).precipitation(41).status("Windy");

    List<HourlyWeather> list = new ArrayList<>();
    list.add(hourlyWeather);
    list.add(hourlyWeather2);
    list.add(hourlyWeather3);
    list.add(hourlyWeather4);

    when(geoLocationService.getLocation(Mockito.anyString())).thenReturn(location);
    when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(list);

    mockMvc.perform(get(END_POINT_PATH).header("X-CURRENT-HOUR", currentHour)).andExpect(status().isOk()).andDo(print());
  }

  @Test
  public void testUpdateHourlyWeatherShouldReturn400BadRequestBecauseInvalidData() throws Exception {
    String requestUri = END_POINT_PATH + "/DELHI_IN";
    List<HourlyWeatherDTO> listDto = new ArrayList<>();
    HourlyWeatherDTO dto1 = new HourlyWeatherDTO().hourOfDay(8).temperature(666).precipitation(0).status("Rainy");

    HourlyWeatherDTO dto2 = new HourlyWeatherDTO().hourOfDay(9).temperature(11).precipitation(20).status("Cloudy");

    listDto.add(dto1);
    listDto.add(dto2);

    objectMapper.writeValueAsString(listDto);

    mockMvc.perform(put(requestUri).contentType("application/json").content(objectMapper.writeValueAsString(listDto)))
        .andExpect(status().isBadRequest()).andDo(print());
  }

  @Test
  public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
    String requestURI = END_POINT_PATH + "/NYC_USA";

    HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
        .hourOfDay(10)
        .temperature(133)
        .precipitation(70)
        .status("Cloudy");

    HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
        .hourOfDay(11)
        .temperature(15)
        .precipitation(60)
        .status("Sunny");

    List<HourlyWeatherDTO> listDTO = List.of(dto1, dto2);

    String requestBody = objectMapper.writeValueAsString(listDTO);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  public void testUpdateShouldReturn404NotFound() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
        .hourOfDay(10)
        .temperature(13)
        .precipitation(70)
        .status("Cloudy");

    List<HourlyWeatherDTO> listDTO = List.of(dto1);

    String requestBody = objectMapper.writeValueAsString(listDTO);

    when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
        .thenThrow(LocationNotFoundException.class);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void testUpdateShouldReturn200OK() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = END_POINT_PATH + "/" + locationCode;

    HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
        .hourOfDay(10)
        .temperature(13)
        .precipitation(70)
        .status("Cloudy");

    HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
        .hourOfDay(11)
        .temperature(15)
        .precipitation(60)
        .status("Sunny");

    Location location = new Location();
    location.setCode(locationCode);
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");

    HourlyWeather forecast1 = new HourlyWeather()
        .location(location)
        .hourOfDay(10)
        .temperature(13)
        .precipitation(70)
        .status("Cloudy");

    HourlyWeather forecast2 = new HourlyWeather()
        .location(location)
        .hourOfDay(11)
        .temperature(15)
        .precipitation(60)
        .status("Sunny");

    List<HourlyWeatherDTO> listDTO = List.of(dto1, dto2);

    var hourlyForecast = List.of(forecast1, forecast2);

    String requestBody = objectMapper.writeValueAsString(listDTO);

    when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
        .thenReturn(hourlyForecast);
    System.out.println(requestBody);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
