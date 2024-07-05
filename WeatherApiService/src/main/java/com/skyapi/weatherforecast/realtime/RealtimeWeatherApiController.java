package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);
  private final GeoLocationService service;
  private final RealtimeWeatherService realtimeWeatherService;
  private final ModelMapper modelMapper;

  public RealtimeWeatherApiController(GeoLocationService service, RealtimeWeatherService realtimeWeatherService, ModelMapper modelMapper) {
    this.service = service;
    this.realtimeWeatherService = realtimeWeatherService;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
    String ipAddress = CommonUtility.GetIpAddress(request);
    try {
      Location locationFromIp = service.getLocation(ipAddress);
      RealtimeWeather realTimeWeather = realtimeWeatherService.getByLocation(locationFromIp);
      return ResponseEntity.ok(convertToDto(realTimeWeather));
    } catch (GeoLocationException e) {
      LOGGER.error(e.getMessage(), e);
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (LocationNotFoundException e) {
      LOGGER.error(e.getMessage(), e);
      return ResponseEntity.notFound().build();
    }
  }
  @GetMapping("/{locationCode}")
  public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
    try {
      RealtimeWeather realTimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
      return ResponseEntity.ok(convertToDto(realTimeWeather));
    } catch (LocationNotFoundException e) {
      LOGGER.error(e.getMessage(), e);
      return ResponseEntity.notFound().build();
    }
  }
  @PutMapping("/{locationCode}")
  public ResponseEntity<?> updateRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode,
      @RequestBody @Valid RealtimeWeather realtimeWeatherRequest) {
    realtimeWeatherRequest.setLocationCode(locationCode);
    try {

      RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode,realtimeWeatherRequest);
      return ResponseEntity.ok(convertToDto(updatedRealtimeWeather));
    } catch (LocationNotFoundException e) {
      LOGGER.error(e.getMessage(), e);
      return ResponseEntity.notFound().build();
    }
  }

  private RealtimeWeatherDTO convertToDto(RealtimeWeather realtimeWeather) {
    return modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
  }
}
