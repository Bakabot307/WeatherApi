package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocationService {

  private final LocationRepository repository;

  public LocationService(LocationRepository repository) {
    this.repository = repository;
  }

  public Location add(Location location) {
    return repository.save(location);
  }

  public List<Location> list() {
    return repository.findUntrashed();
  }

  public Location get(String code) {
    return repository.findByCode(code);
  }

  public Location update(Location locationInRequest) throws LocationNotFoundException {
    String code = locationInRequest.getCode();
    Location locationDB = repository.findByCode(code);
    if (locationDB == null) {
      throw new LocationNotFoundException("No location with the given code:" + code);
    }
    locationDB.setCityName(locationInRequest.getCityName());
    locationDB.setRegionName(locationInRequest.getRegionName());
    locationDB.setCountryCode(locationInRequest.getCountryCode());
    locationDB.setCountryName(locationInRequest.getCountryName());
    locationDB.setEnabled(locationInRequest.isEnabled());
    return repository.save(locationDB);
  }

  public void delete(String code) throws LocationNotFoundException {

    if (!repository.existsById(code)) {
      throw new LocationNotFoundException("No location with the given code:" + code);
    }
    repository.trashByCode(code);
  }

}
