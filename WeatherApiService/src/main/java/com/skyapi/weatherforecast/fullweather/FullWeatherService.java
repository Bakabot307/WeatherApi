package com.skyapi.weatherforecast.fullweather;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.stereotype.Service;

@Service
public class FullWeatherService {

  private LocationRepository locationRepository;

  public FullWeatherService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }
  public Location getLocation(Location locationIp) {
    String cityName = locationIp.getCityName();
    String countryCode = locationIp.getCountryCode();

    Location locationInDB = locationRepository.findByCountryCodeAndAndCityName(countryCode, cityName);

    if(locationInDB == null){
      throw new LocationNotFoundException(countryCode, cityName);
    }

    return null;

  }

}
