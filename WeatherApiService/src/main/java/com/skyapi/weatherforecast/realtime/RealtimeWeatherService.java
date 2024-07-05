package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RealtimeWeatherService {

  private final RealtimeWeatherRepository repository;
  private final LocationRepository locationRepository;

  @Autowired
  public RealtimeWeatherService(RealtimeWeatherRepository repository, LocationRepository locationRepository) {
    this.repository = repository;
    this.locationRepository = locationRepository;
  }

  public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
    String countryCode = location.getCountryCode();
    String cityName = location.getCityName();

    RealtimeWeather realTimeWeather = repository.findByCountryCodeAndCity(countryCode, cityName);

    if (realTimeWeather == null) {
      throw new LocationNotFoundException("No Location found with given country code and city name");
    }
    return realTimeWeather;
  }

  public RealtimeWeather getByLocationCode(String code) throws LocationNotFoundException {
    RealtimeWeather realTimeWeather = repository.findByLocationCode(code);

    if (realTimeWeather == null) {
      throw new LocationNotFoundException("No Location found with given country code");
    }
    return realTimeWeather;
  }

  public RealtimeWeather update(String locationCode, RealtimeWeather realTimeWeather) throws LocationNotFoundException {
    Location location = locationRepository.findByCode(locationCode);
    if(location == null) {
      throw new LocationNotFoundException("No Location found with given location code");
    }

    realTimeWeather.setLocation(location);
    realTimeWeather.setLatUpdate(new Date());

    if(location.getRealTimeWeather()==null) {
      location.setRealTimeWeather(realTimeWeather);
      Location updatedLocation = locationRepository.save(location);
      return updatedLocation.getRealTimeWeather();
    }
    return repository.save(realTimeWeather);
  }

}
