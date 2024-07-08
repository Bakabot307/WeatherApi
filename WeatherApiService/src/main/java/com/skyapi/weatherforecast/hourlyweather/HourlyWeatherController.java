package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeoLocationException;
import com.skyapi.weatherforecast.GeoLocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherController {

  private HourlyWeatherService hourlyWeatherService;
  private GeoLocationService geoLocationService;
  private ModelMapper modelMapper;

  public HourlyWeatherController(HourlyWeatherService hourlyWeatherService, GeoLocationService geoLocationService, ModelMapper modelMapper) {
    this.hourlyWeatherService = hourlyWeatherService;
    this.geoLocationService = geoLocationService;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<?> listHourlyAddressByIPAddress(HttpServletRequest request) {
    String ipAddress = CommonUtility.GetIpAddress(request);
    try {
      int currentHour = Integer.parseInt(request.getHeader("X-CURRENT-HOUR"));
      Location locationFromIP = geoLocationService.getLocation(ipAddress);
      List<HourlyWeather> list = hourlyWeatherService.getByLocation(locationFromIP, currentHour);

      if (list.isEmpty()) {
        return ResponseEntity.noContent().build();
      } else {
        return ResponseEntity.ok(convertToDTO(list));
      }
    } catch (NumberFormatException | GeoLocationException e) {
      return ResponseEntity.badRequest().build();
    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{locationCode}")
  public ResponseEntity<?> listHourlyAddressByLocationCode(HttpServletRequest request, @PathVariable("locationCode") String locationCode) {

    try {
      int currentHour = Integer.parseInt(request.getHeader("X-CURRENT-HOUR"));
      List<HourlyWeather> list = hourlyWeatherService.getByLocationCode(locationCode, currentHour);
      if (list.isEmpty()) {
        return ResponseEntity.noContent().build();
      } else {
        return ResponseEntity.ok(convertToDTO(list));
      }
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().build();
    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{locationCode}")
  public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode, @RequestBody @Valid List<HourlyWeatherDTO> listDTO)
      throws BadRequestException {

    if (listDTO.isEmpty()) {
      throw new BadRequestException("Hourly forecast data cannot be empty");
    }
    listDTO.forEach(System.out::println);
    List<HourlyWeather> listHourlyWeather = convertToEntity(listDTO);
    listHourlyWeather.forEach(System.out::println);
    try {
      List<HourlyWeather> updateHourlyWeather = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
      return ResponseEntity.ok(convertToDTO(updateHourlyWeather));
    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  private List<HourlyWeather> convertToEntity(List<HourlyWeatherDTO> listDTO) {
    List<HourlyWeather> hwList = new ArrayList<>();
    listDTO.forEach(hwDTO -> {
      HourlyWeather hw = modelMapper.map(hwDTO, HourlyWeather.class);
      hwList.add(hw);
    });
    return hwList;
  }

  private HourlyWeatherListDTO convertToDTO(List<HourlyWeather> list) {
    Location location = list.get(0).getId().getLocation();
    HourlyWeatherListDTO dto = new HourlyWeatherListDTO();
    dto.setLocation(location.toString());

    list.forEach(hw -> {
      HourlyWeatherDTO hwDTO = modelMapper.map(hw, HourlyWeatherDTO.class);
      dto.addHourlyWeather(hwDTO);
    });
    return dto;
  }
}
