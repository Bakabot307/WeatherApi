package com.skyapi.weatherforecast.fullweather;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.Location;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/full")
public class FullWeatherController {

  private GeoLocationService geoLocationService;
  private FullWeatherService fullWeatherService;
  private ModelMapper modelMapper;

  public FullWeatherController(GeoLocationService geoLocationService, FullWeatherService fullWeatherService,ModelMapper modelMapper) {
    this.geoLocationService = geoLocationService;
    this.fullWeatherService = fullWeatherService;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<?> listFullWeatherByIPAddress(HttpServletRequest request) {
    String ipAddress = CommonUtility.GetIpAddress(request);
    Location location = geoLocationService.getLocation(ipAddress);
    Location locationInDB = fullWeatherService.getLocation(location);

    return ResponseEntity.ok(entityToDTO(locationInDB));
  }

  private FullWeatherDTO entityToDTO(Location location) {

    return modelMapper.map(location, FullWeatherDTO.class);
  }

}
