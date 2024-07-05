package com.skyapi.weatherforecast.location;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {

  private static final String END_POINT_PATH = "/v1/locations";
  private static final String REQUEST_CONTENT_TYPE = "application/json";
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  LocationService locationService;

  @Test
  public void testAddLocationShouldReturn400BadQuest() throws Exception {
    Location location = new Location();
    String bodyContent = objectMapper.writeValueAsString(location);
    mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE)).content(bodyContent)).andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  public void testAddLocationShouldReturn201Created() throws Exception {
    Location location = new Location();
    location.setCode("NYC_USA");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");
    location.setEnabled(true);
    location.setTrashed(false);

    Mockito.when(locationService.add(location)).thenReturn(location);

    String bodyContent = objectMapper.writeValueAsString(location);
    mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE)).content(bodyContent)).andExpect(status().isCreated())
        .andExpect(content().contentType(REQUEST_CONTENT_TYPE)).andExpect(jsonPath("$.code", is("NYC_USA")))
        .andExpect(jsonPath("$.city_name", is("New York City"))).andExpect(header().string("Location", "/v1/locations/NYC_USA")).andDo(print());
  }

  @Test
  public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
    Location location = new Location();
    location.setCode(null);
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");
    location.setEnabled(true);
    location.setTrashed(false);

    String bodyContent = objectMapper.writeValueAsString(location);
    mockMvc.perform(post(END_POINT_PATH)
            .contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE))
            .content(bodyContent))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(REQUEST_CONTENT_TYPE))
        .andExpect(jsonPath("$.errors[0]", is("Location code can't be null")));
  }

  @Test
  public void testValidateRequestBodyLocationCodeLength() throws Exception {
    Location location = new Location();
    location.setCode("");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");
    location.setEnabled(true);
    location.setTrashed(false);

    String bodyContent = objectMapper.writeValueAsString(location);
    mockMvc.perform(post(END_POINT_PATH)
            .contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE))
            .content(bodyContent))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(REQUEST_CONTENT_TYPE))
        .andExpect(jsonPath("$.errors[0]", is("Location code must be 3-12 characters")));
  }

  @Test
  public void testValidateRequestBodyLocationAllFields() throws Exception {
    Location location = new Location();
    location.setCountryName("");

    String bodyContent = objectMapper.writeValueAsString(location);
    MvcResult result =mockMvc.perform(post(END_POINT_PATH)
            .contentType(MediaType.valueOf(REQUEST_CONTENT_TYPE))
            .content(bodyContent))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(REQUEST_CONTENT_TYPE)).andDo(print()).andReturn();

    String response = result.getResponse().getContentAsString();
    assertThat(response).contains("Country name must have 3-128 characters");
  }

  @Test
  public void testListShouldReturn204NoContent() throws Exception {
    Mockito.when(locationService.list()).thenReturn(Collections.emptyList());
    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent()).andDo(print());
  }

  @Test
  public void testListShouldReturn200Ok() throws Exception {
    Location location1 = new Location();
    location1.setCode("NYC_USA");
    location1.setCityName("New York City");
    location1.setRegionName("New York");
    location1.setCountryCode("US");
    location1.setCountryName("United States of America");
    location1.setEnabled(true);

    Location location2 = new Location();
    location2.setCode("LACA_USA");
    location2.setCityName("Los Angeles");
    location2.setRegionName("California");
    location2.setCountryCode("US");
    location2.setCountryName("United States of America");
    location2.setEnabled(true);

    Location location3 = new Location();
    location3.setCode("Delhi_IN");
    location3.setCityName("New Delhi");
    location3.setRegionName("Delhi");
    location3.setCountryCode("IN");
    location3.setCountryName("India");
    location3.setEnabled(true);
    location3.setTrashed(true);

    Mockito.when(locationService.list()).thenReturn(List.of(location1, location2, location3));
    mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk()).andExpect(content().contentType(REQUEST_CONTENT_TYPE))
        .andExpect(jsonPath("$[0].code", is("NYC_USA"))).andExpect(jsonPath("$[0].city_name", is("New York City"))).andDo(print());
  }

  @Test
  public void testGetShouldReturn405MethodNotAllowed() throws Exception {
    String requestUri = END_POINT_PATH + "/abcd";
    mockMvc.perform(post(requestUri)).andExpect(status().isMethodNotAllowed()).andDo(print());
  }

  @Test
  public void testGetShouldReturn404NotFound() throws Exception {
    String requestUri = END_POINT_PATH + "/abcd";
    mockMvc.perform(get(requestUri)).andExpect(status().isNotFound()).andDo(print());
  }

  @Test
  public void testGetShouldReturn200OK() throws Exception {
    String code = "NYC_USA";
    String requestUri = END_POINT_PATH + "/" + code;

    Location location = new Location();
    location.setCode("NYC_USA");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");
    location.setEnabled(true);

    Mockito.when(locationService.get(code)).thenReturn(location);

    mockMvc.perform(get(requestUri)).andExpect(status().isOk()).andExpect(content().contentType(REQUEST_CONTENT_TYPE))
        .andExpect(jsonPath("$.code", is(code))).andExpect(jsonPath("$.city_name", is("New York City"))).andDo(print());
  }

  @Test
  public void testUpdateShouldReturn404NotFound() throws Exception {
    Location location = new Location();
    location.setCode("abcd");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");
    location.setEnabled(true);

    String bodyContent = objectMapper.writeValueAsString(location);
    Mockito.when(locationService.update(location)).thenThrow(new LocationNotFoundException("No Location Found"));
    mockMvc.perform(put(END_POINT_PATH).contentType(REQUEST_CONTENT_TYPE).content(bodyContent)).andExpect(status().isNotFound()).andDo(print());
  }

  @Test
  public void testUpdateShouldReturn400BadRequest() throws Exception {
    Location location = new Location();
    String bodyContent = objectMapper.writeValueAsString(location);
    mockMvc.perform(put(END_POINT_PATH).contentType(REQUEST_CONTENT_TYPE).content(bodyContent)).andExpect(status().isBadRequest()).andDo(print());
  }


  @Test
  public void testUpdateShouldReturn200OK() throws Exception {
    String code = "NYC_USA";
    Location location = new Location();
    location.setCode("NYC_USA");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United States of America");
    location.setEnabled(false);

    String bodyContent = objectMapper.writeValueAsString(location);

    Mockito.when(locationService.update(location)).thenReturn(location);

    mockMvc.perform(put(END_POINT_PATH).contentType(REQUEST_CONTENT_TYPE).content(bodyContent)).andExpect(status().isOk())
        .andExpect(content().contentType(REQUEST_CONTENT_TYPE)).andExpect(jsonPath("$.code", is(code)))
        .andExpect(jsonPath("$.city_name", is("New York City"))).andDo(print());
  }

  @Test
  public void testDeleteShouldReturn404NotFound() throws Exception {
    String code = "abcd";
    String requestUri = END_POINT_PATH + "/" + code;
    Mockito.doThrow(LocationNotFoundException.class).when(locationService).delete(code);
    mockMvc.perform(delete(requestUri)).andExpect(status().isNotFound()).andDo(print());
  }

  @Test
  public void testDeleteShouldReturn204NoContent() throws Exception {
    String code = "NYC_USA";
    String requestUri = END_POINT_PATH + "/" + code;

    Mockito.doNothing().when(locationService).delete(code);
    mockMvc.perform(delete(requestUri)).andExpect(status().isNoContent()).andDo(print());

  }
}
