package com.skyapi.weatherforecast.dailyweather;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DailyWeatherService {

  private final DailyWeatherRepository repository;
  private final LocationRepository locationRepository;

  public DailyWeatherService(DailyWeatherRepository repository, LocationRepository locationRepository) {
    this.repository = repository;
    this.locationRepository = locationRepository;
  }

  public List<DailyWeather> getByLocation(Location location) {

    String countryCode = location.getCountryCode();
    String cityName = location.getCityName();

    Location locationInDB = locationRepository.findByCountryCodeAndAndCityName(countryCode, cityName);

    if (locationInDB == null) {
      throw new LocationNotFoundException(countryCode, cityName);
    }

    return repository.getByLocationCode(locationInDB.getCode());
  }
}
