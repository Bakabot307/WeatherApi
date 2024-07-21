package com.skyapi.weatherforecast.dailyweather;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/daily")
public class DailyWeatherApiController {
  private final LocationService locationService;
  private final DailyWeatherService dailyWeatherService;
  private final ModelMapper modelMapper;

  private final GeoLocationService geoLocationService;

  public DailyWeatherApiController(LocationService locationService, DailyWeatherService dailyWeatherService, ModelMapper modelMapper,
      GeoLocationService geoLocationService) {
    this.locationService = locationService;
    this.dailyWeatherService = dailyWeatherService;
    this.modelMapper = modelMapper;
    this.geoLocationService = geoLocationService;
  }

  @GetMapping
  public ResponseEntity<?> getDailyWeather(HttpServletRequest request) {
    String ipAddress = CommonUtility.GetIpAddress(request);

    Location locationFromIP = geoLocationService.getLocation(ipAddress);
    List<DailyWeather> dailyWeathers = dailyWeatherService.getByLocation(locationFromIP);
    if (dailyWeathers.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(convertToListDto(dailyWeathers));
  }


  private DailyWeatherListDTO convertToListDto(List<DailyWeather> dailyForecast) {
    Location location = dailyForecast.get(0).getId().getLocation();

    DailyWeatherListDTO listDTO = new DailyWeatherListDTO();
    listDTO.setLocation(location.toString());

    dailyForecast.forEach(dailyWeather -> {
      listDTO.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
    });

    return listDTO;
  }


}
