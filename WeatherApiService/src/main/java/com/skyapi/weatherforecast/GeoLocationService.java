package com.skyapi.weatherforecast;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GeoLocationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeoLocationService.class);
  private String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";
  private IP2Location ip2Locator = new IP2Location();

  public GeoLocationService() {
    try {
      ip2Locator.Open(DBPath);
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }
  }

  public Location getLocation(String ipAddress) throws GeoLocationException {
    try {
      IPResult ipResult = ip2Locator.IPQuery(ipAddress);
      if (!"OK".equals(ipResult.getStatus())) {
        throw new GeoLocationException("GeoLocation failed with status " + ipResult.getStatus());
      } else {
        return new Location(ipResult.getCity(), ipResult.getRegion(), ipResult.getCountryLong(), ipResult.getCountryShort());
      }
    } catch (IOException e) {
      throw new GeoLocationException("Error query database", e);
    }
  }
}
