package com.skyapi.weatherforecast.dailyweather;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import java.util.ArrayList;
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

  public List<DailyWeather> getByLocationCode(String locationCode) {
    Location location = locationRepository.findByCode(locationCode);
    if (location == null) {
      throw new LocationNotFoundException(locationCode);
    }
    return repository.getByLocationCode(locationCode);
  }

  public List<DailyWeather> updateByLocationCode(String locationCode, List<DailyWeather> dailyWeathers) {
    Location location = locationRepository.findByCode(locationCode);
    if (location == null) {
      throw new LocationNotFoundException(locationCode);
    }
    for (DailyWeather data : dailyWeathers) {
      data.getId().setLocation(location);
    }
    List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
    List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();

    for (HourlyWeather item : hourlyWeatherInDB) {
      if (!dailyWeathers.contains(item)) {
        hourlyWeatherToBeRemoved.add(item.getShallowCopy());
      }
    }

    for (HourlyWeather item : hourlyWeatherToBeRemoved) {
      hourlyWeatherInDB.remove(item);
    }

    return (List<DailyWeather>) repository.saveAll(dailyWeathers);
  }
}
