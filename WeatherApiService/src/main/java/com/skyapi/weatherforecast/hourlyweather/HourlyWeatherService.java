package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HourlyWeatherService {

  private HourlyWeatherRepository hourlyWeatherRepository;
  private LocationRepository locationRepository;


  public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepository, LocationRepository locationRepository) {
    this.hourlyWeatherRepository = hourlyWeatherRepository;
    this.locationRepository = locationRepository;
  }

  public List<HourlyWeather> getByLocation(Location location, int currentHour) {
    String countryCode = location.getCountryCode();
    String cityName = location.getCityName();

    Location locationFound = locationRepository.findByCountryCodeAndAndCityName(countryCode, cityName);

    if (locationFound == null) {
      throw new LocationNotFoundException(countryCode, cityName);
    }
    return hourlyWeatherRepository.findByLocationCode(locationFound.getCode(), currentHour);
  }

  public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) {
    Location location = locationRepository.findByCode(locationCode);
    if (location == null) {
      throw new LocationNotFoundException(locationCode);
    }
    return hourlyWeatherRepository.findByLocationCode(locationCode, currentHour);
  }

  public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeatherList) {
    Location location = locationRepository.findByCode(locationCode);
    if (location == null) {
      throw new LocationNotFoundException(locationCode);
    }
    System.out.println(hourlyWeatherList);
    for (HourlyWeather hourlyWeather : hourlyWeatherList) {
      hourlyWeather.getId().setLocation(location);
    }

    List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
    List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();

    for (HourlyWeather item : hourlyWeatherInDB) {
      if (!hourlyWeatherList.contains(item)) {
        hourlyWeatherToBeRemoved.add(item.getShallowCopy());
      }
    }

    for (HourlyWeather item : hourlyWeatherToBeRemoved) {
      hourlyWeatherInDB.remove(item);
    }
    return (List<HourlyWeather>) hourlyWeatherRepository.saveAll(hourlyWeatherList);
  }
}
